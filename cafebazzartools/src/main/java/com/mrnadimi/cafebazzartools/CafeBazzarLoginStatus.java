package com.mrnadimi.cafebazzartools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.farsitel.bazaar.ILoginCheckService;

public class CafeBazzarLoginStatus {

    private static final String TAG = "Bazzar:LoginStatus";

    ILoginCheckService service;
    LoginCheckServiceConnection connection;

    protected CafeBazzarLoginStatus() {
    }

    protected void initService(Context context) {
        Log.i(TAG, "initService()");
        connection = new LoginCheckServiceConnection();
        Intent i = new Intent(
                "com.farsitel.bazaar.service.LoginCheckService.BIND");
        i.setPackage("com.farsitel.bazaar");
        boolean ret = context.bindService(i, connection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "initService() bound value: " + ret);
    }

    protected boolean isUserLogged() throws RemoteException {
        return service.isLoggedIn();
    }

    /** This is our function to un-binds this activity from our service. */
    protected void releaseService(Context context) {
        context.unbindService(connection);
        connection = null;
        Log.d(TAG, "releaseService(): unbound.");
    }

    public class LoginCheckServiceConnection implements ServiceConnection {

        private static final String TAG = "LoginCheck";

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = ILoginCheckService.Stub
                    .asInterface((IBinder) boundService);
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.e(TAG, "onServiceDisconnected(): Disconnected");
        }
    }

}
