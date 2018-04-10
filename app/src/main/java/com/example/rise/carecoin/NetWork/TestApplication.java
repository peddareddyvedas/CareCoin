package com.example.rise.carecoin.NetWork;

import android.app.Application;
import android.util.Log;

/**
 * Created by WAVE on 12/7/2017.
 */

public class TestApplication extends Application {

    private static TestApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized TestApplication getInstance() {
        return mInstance;
    }

    public void setConnectionListener(ConnectionReceiver.ConnectionReceiverListener listener)
    {
        Log.e("setConnectionListener","call");
        ConnectionReceiver.connectionReceiverListener = listener;
    }
}
