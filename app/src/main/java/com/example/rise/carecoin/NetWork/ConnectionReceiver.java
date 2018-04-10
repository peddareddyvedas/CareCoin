package com.example.rise.carecoin.NetWork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.rise.carecoin.Controllers.CurrencyController;
import com.example.rise.carecoin.R;

/**
 * Created by WAVE on 12/7/2017.
 */

public class ConnectionReceiver extends BroadcastReceiver{

    public static ConnectionReceiverListener connectionReceiverListener;

    public ConnectionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.e("BroadcastReceiver","call");
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectionReceiverListener != null) {
            Log.e("notnull","call");
            connectionReceiverListener.onNetworkConnectionChanged(isConnected);
        }
        if (isConnected){
            Log.e("ConnectionReceiver","call"+isConnected);
            //ApisController.getInstance().currentbalanceApiExecution();
            CurrencyController.getInstance().executeCurrencyInfoServerAPI();
        }else {
            Toast toast = Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_SHORT);
            toast.show();
    }
    }
    public interface ConnectionReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}

