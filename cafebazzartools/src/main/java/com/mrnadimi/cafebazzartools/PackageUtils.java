package com.mrnadimi.cafebazzartools;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageUtils {
    /**
     *
     * @param context The context
     *                ...
     * @return if application download from play store or not
     */
    public static boolean isDonwloadedFromPlayStore(Context context) {
        return verifyInstallerId(context , "com.android.vending", "com.google.android.feedback");
    }

    public static boolean isDonwloadedFromCafeBazzar(Context context) {
        return verifyInstallerId(context , "com.farsitel.bazaar");
    }


    public static boolean verifyInstallerId(Context context , String... packageNames){
        if (packageNames == null || packageNames.length == 0){
            throw new NullPointerException("Null packageNames");
        }
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList(packageNames));
        // The package name of the app that has installed your app
        final String installer = getInstallerName(context);
        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    /**
     *
     * @param context The application context
     * @return Apk installed from where ( Returned the packagename that installed the apk)
     */
    public static String getInstallerName(Context context){
        return context.getPackageManager().getInstallerPackageName(context.getPackageName());
    }

    /**
     *
     * @param context The application context
     * @param packageName PackageName
     * @return is app installed or not
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static boolean isCafeBazzarInstalled(Context context){
        return PackageUtils.isAppInstalled(context , "com.farsitel.bazaar");
    }

}
