package com.mrnadimi.cafebazzartools.interfaces;

import com.mrnadimi.cafebazzartools.CafeBazzar;
import com.mrnadimi.cafebazzartools.util.IabResult;
import com.mrnadimi.cafebazzartools.util.Purchase;
import com.mrnadimi.cafebazzartools.util.SkuDetails;

import java.util.List;

public interface OnCafeBazzarListener {

    public void onCafeBazzarIsNotInslling(CafeBazzar cafeBazzar);
    public void onInternetConnectionError(CafeBazzar cafeBazzar);
    public void onStart(CafeBazzar cafeBazzar,IabResult result , boolean success , Exception ex);
    public void onLoginStatus(CafeBazzar cafeBazzar,boolean userLogged ,  boolean success ,  Exception ex);
    public void onVersionCode(CafeBazzar cafeBazzar,long versionCode ,  boolean success ,  Exception ex);
    public void onProductsInventory(CafeBazzar cafeBazzar,List<SkuDetails> res, List<Purchase> purchases, boolean success , Exception ex);
    public void onBuyInventory(CafeBazzar cafeBazzar,boolean success , Purchase purchase , Exception ex);
    public void onSpendPurchase(CafeBazzar cafeBazzar,boolean success , Purchase purchase , Exception ex);
}
