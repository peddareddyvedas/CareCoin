package com.example.rise.carecoin.PushNotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.rise.carecoin.Controllers.ContactsDataController;
import com.example.rise.carecoin.Controllers.LoginServerDataController;
import com.example.rise.carecoin.DataBase.TransactionDataController;
import com.example.rise.carecoin.HomeModule.ConfirmSendViewController;
import com.example.rise.carecoin.HomeModule.HomeActivityViewController;
import com.example.rise.carecoin.HomeModule.ReceiveFragment;
import com.example.rise.carecoin.LoginModule.SplashScreenViewController;
import com.example.rise.carecoin.SideMenu.ContactsViewController;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rise on 08/01/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("FCMMESSAGE", "MESSAGE RECEIVED!!" + remoteMessage);
        Log.e("FCMMESSAGE1 ", "call" + remoteMessage.getData());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
       /* if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body:" + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }*/

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> data = remoteMessage.getData();
                Log.e("FCMMESSAGEData", "" + data.keySet());
                String title = data.get("title");
                String message = data.get("message");
                Log.e("title", "" + title + "" + message);
                handleDataMessage(title, message);
            }

        }

    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            //   NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //  notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(String title, String message) {
        Handler handler=null;
        Log.e(TAG, "push json: " + message);
        Log.e(TAG, "push jsonTitle: " + title);
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoginServerDataController.getInstance().executeTransactionsRefreshServerAPI();
            }

        }, 1000 * 1);

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Log.e("AppStatus" ,"notterminated");
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            /*handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginServerDataController.getInstance().executeTransactionsRefreshServerAPI();
                }

            }, 1000 * 1);*/

        } else {
            Log.e("AppStatus" ,"terminated");
            // app is in background, show the notification in notification tray
            gettingNotification(message, title);
        }
    }

    private void gettingNotification(String message, String title) {
        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreenViewController.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("message", message);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(SplashScreenViewController.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.e("background", "" + title + "" + message);
        showNotificationMessage(getApplicationContext(), message, notificationIntent, pendingIntent,title);
    }
    /**
     * Showing notification with text only
     */

    private void showNotificationMessage(Context context, String message, Intent intent, PendingIntent pendinIntent,String title) {
        notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage(message, intent, pendinIntent,title);

    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
    }


}







