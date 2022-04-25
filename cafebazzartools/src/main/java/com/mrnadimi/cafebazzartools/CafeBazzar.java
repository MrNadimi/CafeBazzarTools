package com.mrnadimi.cafebazzartools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.mrnadimi.cafebazzartools.exceptions.BuyingProductCancelException;
import com.mrnadimi.cafebazzartools.exceptions.CafeBazzarException;
import com.mrnadimi.cafebazzartools.interfaces.OnCafeBazzarListener;
import com.mrnadimi.cafebazzartools.util.IabHelper;
import com.mrnadimi.cafebazzartools.util.IabResult;
import com.mrnadimi.cafebazzartools.util.Inventory;
import com.mrnadimi.cafebazzartools.util.Purchase;
import com.mrnadimi.cafebazzartools.util.SkuDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CafeBazzar {

    private static CafeBazzar cafeBazzar;
    private final String TAG = "CafeBazzar";
    /**
     * Dar inja faghat login barrasi mishavad
     */
    public final static int JUST_LOGIN_CHECK_RQEUEST_CODE = 8934;
    public final static int LOGIN_CHECK_WITH_EXTRA_RQEUEST_CODE = 8935;
    public final static int BUY_RQEUEST_CODE = 8936;

    private final IabHelper helper;
    private final CafeBazzarLoginStatus cafeBazzarLoginStatus;
    private final OnCafeBazzarListener listener;

    private boolean started;

    public static CafeBazzar getInstance(Activity activity , String base64EncodedPublicKey , OnCafeBazzarListener listener){
        if (cafeBazzar == null) cafeBazzar= new CafeBazzar(activity ,  base64EncodedPublicKey , listener);
        return cafeBazzar;
    }

    private CafeBazzar(Context context , String base64EncodedPublicKey , OnCafeBazzarListener listener){
        helper = new IabHelper(context , base64EncodedPublicKey);
        helper.enableDebugLogging(true , "aaaaaaa-------");
        this.listener = listener;
        cafeBazzarLoginStatus = new CafeBazzarLoginStatus();
        started = false;

    }

    public void start() {
        if (!PackageUtils.isCafeBazzarInstalled(helper.mContext)){
            listener.onCafeBazzarIsNotInslling(this);
            return;
        }else if (AndroidUtils.isNetworkNotConnected(helper.mContext)) {
            listener.onInternetConnectionError(CafeBazzar.cafeBazzar);
            return;
        }
        if (started)return;
        cafeBazzarLoginStatus.initService(helper.mContext);
        helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                listener.onStart(CafeBazzar.cafeBazzar,result , result.isSuccess() , result.isSuccess()  ? null : new CafeBazzarException(result.getResponse()+":"+result.getMessage()));
            }
        });
    }


    public void isUserLogged(){
        if (!PackageUtils.isCafeBazzarInstalled(helper.mContext)){
            listener.onCafeBazzarIsNotInslling(this);
            return;
        }else if (AndroidUtils.isNetworkNotConnected(helper.mContext)) {
            listener.onInternetConnectionError(CafeBazzar.cafeBazzar);
            return;
        }
        try{
            listener.onLoginStatus(this, cafeBazzarLoginStatus.isUserLogged() , true , null);
        }catch (Exception ex){
            listener.onLoginStatus(this,false , false , ex);
        }
    }




    public void dispose() {
        if (helper != null){
            cafeBazzarLoginStatus.releaseService(helper.mContext);
            helper.dispose();
        }
        started = false;
        cafeBazzar = null;
    }

    public boolean isStarted(){
        return started;
    }


    public void productsInventory(boolean showDetails , List<String> skus) {
        if (!PackageUtils.isCafeBazzarInstalled(helper.mContext)){
            listener.onCafeBazzarIsNotInslling(this);
            return;
        }else if (AndroidUtils.isNetworkNotConnected(helper.mContext)) {
            listener.onInternetConnectionError(CafeBazzar.cafeBazzar);
            return;
        }
        if (skus.size() == 0){
            throw new NullPointerException("Sku list");
        }
        helper.queryInventoryAsync(showDetails, skus, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (result.isFailure()) {
                    /**
                     * Karbar dar cafe bazzar login nakarde ast
                     * Shayad moshkel az internet ham bashad
                     */
                    if (result.getResponse() == 6) {
                        Intent intent = getCafeBazzarLoginIntent();
                        intent.putExtra("skus" , skus.toArray(new String[0]));
                        intent.putExtra("showDetails" , showDetails);
                        ((Activity)helper.mContext).startActivityForResult(intent , LOGIN_CHECK_WITH_EXTRA_RQEUEST_CODE);
                        return;
                    }
                    listener.onProductsInventory(CafeBazzar.cafeBazzar,null , null , false , new CafeBazzarException(result.getResponse()+":"+result.getMessage()));
                    return;
                }

                List<SkuDetails> res = new ArrayList<>();
                List<Purchase> purchases = new ArrayList<>();
                for (String sku : skus){
                    if (inv.hasDetails(sku))  res.add(inv.getSkuDetails(sku));
                    if (inv.hasPurchase(sku)) purchases.add(inv.getPurchase(sku));
                }
                listener.onProductsInventory(CafeBazzar.cafeBazzar,res , purchases , true , null);
            }
        });
    }


    public void buy(String sku, String payload){
        if (!PackageUtils.isCafeBazzarInstalled(helper.mContext)){
            listener.onCafeBazzarIsNotInslling(this);
            return;
        }else if (AndroidUtils.isNetworkNotConnected(helper.mContext)) {
            listener.onInternetConnectionError(CafeBazzar.cafeBazzar);
            return;
        }
        helper.launchPurchaseFlow((Activity) helper.mContext, sku, BUY_RQEUEST_CODE, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()){
                    if (result.getResponse() == 1002){
                        listener.onBuyInventory(CafeBazzar.cafeBazzar , false , info , new BuyingProductCancelException());
                        return;
                    }
                    listener.onBuyInventory(CafeBazzar.cafeBazzar,false , info ,  new CafeBazzarException(result.getResponse() + ":"+result.getMessage()));
                    return;
                }
                listener.onBuyInventory(CafeBazzar.cafeBazzar,true , info , null);
            }
        } , payload);
    }


    public void spend(Purchase purchase){
        if (!PackageUtils.isCafeBazzarInstalled(helper.mContext)){
            listener.onCafeBazzarIsNotInslling(this);
            return;
        }else if (AndroidUtils.isNetworkNotConnected(helper.mContext)) {
            listener.onInternetConnectionError(CafeBazzar.cafeBazzar);
            return;
        }
        helper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                if (result.isFailure()){
                    listener.onSpendPurchase(CafeBazzar.cafeBazzar , false , purchase ,  new CafeBazzarException(result.getResponse() + ":"+result.getMessage()));
                    return;
                }
                listener.onSpendPurchase(CafeBazzar.cafeBazzar , true , purchase , null);
            }
        });
    }



    /**
     *
     * @param context The context
     * Refer to Application page in the market
     */
    public static void referToApp(Context context){
        if (PackageUtils.isCafeBazzarInstalled(context)){
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("bazaar://details?id=" + context.getPackageName()));
                intent.setPackage("com.farsitel.bazaar");
                context.startActivity(intent);
                return;
            }catch (Exception ignored){}
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse( "https://cafebazaar.ir/app/"+context.getPackageName() )));
    }


    public static void referToLogin(Activity activity){
        activity.startActivityForResult(getCafeBazzarLoginIntent() , JUST_LOGIN_CHECK_RQEUEST_CODE);
    }


    private static Intent getCafeBazzarLoginIntent(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("bazaar://login"));
        intent.setPackage("com.farsitel.bazaar");
        return intent;
    }


    /**
     *
     * @param intent The intent
     * Invoke it when
     */
    public static boolean onActivityResult(Activity activity , int requestCode, int resultCode, Intent intent){
        if (requestCode == JUST_LOGIN_CHECK_RQEUEST_CODE || requestCode == LOGIN_CHECK_WITH_EXTRA_RQEUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(activity , "Successfully loged in" , Toast.LENGTH_SHORT).show();
                if (requestCode == JUST_LOGIN_CHECK_RQEUEST_CODE){
                    return true;
                }
                List<String> skus = Arrays.asList(intent.getStringArrayExtra("skus"));
                boolean showDetails = intent.getBooleanExtra("showDetails" , true);
                CafeBazzar.cafeBazzar.productsInventory(showDetails , skus);
            }else{
                Toast.makeText(activity , "Login failed." , Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (requestCode == BUY_RQEUEST_CODE){
            return CafeBazzar.cafeBazzar.helper.handleActivityResult(requestCode, resultCode, intent);
        }
        return false;
    }


}
