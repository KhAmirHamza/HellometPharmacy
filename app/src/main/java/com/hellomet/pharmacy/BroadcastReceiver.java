package com.hellomet.pharmacy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MyFirebaseMessagingService.class));
        } else {
            context.startService(new Intent(context, MyFirebaseMessagingService.class));
        }
    }
}
