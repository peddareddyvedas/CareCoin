package com.example.rise.carecoin.PushNotification;

import android.util.Log;

import com.example.rise.carecoin.LoginModule.SplashScreenViewController;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Rise on 08/01/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
      String  refreshedToken,deviceToken;


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.  carecoin-195304
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Refreshedtoken", " " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);

        SplashScreenViewController.sharedPreferencesTOkenEditor.putString("tokenid",deviceToken);
        SplashScreenViewController.sharedPreferencesTOkenEditor.commit();
    }



    public  void sendRegistrationToServer(String token) {

        deviceToken=token;
        Log.e("token",""+deviceToken);

    }

}