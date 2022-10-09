package com.hellomet.pharmacy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hellomet.pharmacy.Model.FCM;
import com.hellomet.pharmacy.View.DashboardActivity;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "onMessageReceived: "+remoteMessage.getData().get("title"));
        String title = "title";
        title = remoteMessage.getData().get("title");
        String body = "body";
        body = remoteMessage.getNotification().getBody();
        sendNotification(title, body);
        //mNotificationManager.notify(0, notification);
    }


    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        SharedPreferences sp_fcm = getSharedPreferences("FCM", Context.MODE_PRIVATE);
        SharedPreferences.Editor token_editor = sp_fcm.edit();
        token_editor.putString("TOKEN", token);
        //Toast.makeText(this, "New Token: "+token, Toast.LENGTH_SHORT).show();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationTokenToServer(token);

    }


//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
////        if (Build.VERSION.SDK_INT >= 26) {
////
////            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
////                    "Channel human readable title",
////                    NotificationManager.IMPORTANCE_HIGH);
////
////            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
////
////            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
////                    .setContentTitle("tttt")
////                    .setContentText("cccccccc").build();
////
////            startForeground(1, notification);
////        }
//
//
//
//
//
//
//        NotificationCompat.Builder mBuilder =
////                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
//                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
//        Intent ii = new Intent(getApplicationContext(), MyFirebaseMessagingService.class);
//        ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//        bigText.bigText("When Order available you will inform by notification!");
//        bigText.setBigContentTitle("New Order Found!");
//        bigText.setSummaryText("Check out new order!");
//
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
//        mBuilder.setContentTitle("title");
//        mBuilder.setContentText("message");
//        mBuilder.setAutoCancel(false);
//        mBuilder.setPriority(Notification.PRIORITY_HIGH);
//        mBuilder.setStyle(bigText);
//
//        mNotificationManager =
//                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//// === Removed some obsoletes
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "fcm";
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "FCM",
//                    NotificationManager.IMPORTANCE_HIGH);
//            assert mNotificationManager != null;
//            mBuilder.setChannelId(channelId);
//            mNotificationManager.createNotificationChannel(channel);
//
//        }
//        assert mNotificationManager != null;
//        // mNotificationManager.notify(0, mBuilder.build());
//        notification = mBuilder.build();
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
//            startForeground(1, notification);
//        }
//    }

    private void sendRegistrationTokenToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("PHARMACY_ID",null);
        Log.d(TAG, "id: "+id);
        if (id!=null){
            FCM fcm = new FCM(id, token);
            ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);
            apiRequests.updateFcmToken(id, fcm).enqueue(new Callback<FCM>() {
                @Override
                public void onResponse(Call<FCM> call, Response<FCM> response) {
                    if (response.isSuccessful()){
                        Log.d(TAG, "onResponse: "+response.body().getToken());
                    }else {
                        Log.d(TAG, "onResponse: "+response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<FCM> call, Throwable t) {
                    Log.d(TAG, "onFailure: "+t.getMessage());
                }
            });
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                      //  .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground();
        }
        else{
            startForeground(1, new Notification());
        }
}


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_NONE)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .build();
        startForeground(1, notification);
    }
}
