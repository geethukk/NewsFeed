package com.moengage.newsfeed.NetworkManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.moengage.newsfeed.Activity.MainActivity;
import com.moengage.newsfeed.Utils.AndyUtils;
import com.moengage.newsfeed.Utils.NetworkUtil;

//Broadcast Receiver for Network
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        try {
            if (context instanceof MainActivity) {
                MainActivity activity = (MainActivity) context;
                if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                    Log.e("NetworkChangeReceiver", "Internet not  available");
                    if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                        //no internet
                        activity.checkforInternetValidation();
                    } else {
                        // internet
                        activity.fetchData();
                    }
                } else {
                    String action = intent.getAction();
                    switch (action) {
                        case "msg":
                            String mess = intent.getStringExtra("message");
                            AndyUtils.showAlertdialog(context, mess);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
