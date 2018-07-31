package com.example.ajibade.myreddit.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

public class NetworkUtils {

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param context Context used to get the ConnectivityManager
     * @return true if network is available
     */
    static public boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }
}
