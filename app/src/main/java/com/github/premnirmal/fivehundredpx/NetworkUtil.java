package com.github.premnirmal.fivehundredpx;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by premnirmal on 12/2/14.
 */
public class NetworkUtil {


    public static boolean haveNetworkConnection(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo() != null;
        }
        return false;
    }
}