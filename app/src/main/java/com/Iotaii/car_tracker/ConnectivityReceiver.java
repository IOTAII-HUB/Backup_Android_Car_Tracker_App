package com.Iotaii.car_tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static boolean isConnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean currentlyConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (currentlyConnected && !isConnected) {
            isConnected = true;
            Toast.makeText(context, "Internet is turned on", Toast.LENGTH_SHORT).show();
        } else if (!currentlyConnected && isConnected) {
            isConnected = false;
            Toast.makeText(context, "Please Turn on Your Internet!", Toast.LENGTH_SHORT).show();
        }
    }
}
