package com.sudo.nikhil.gro;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static com.sudo.nikhil.gro.Notify.CHANNEL_ID;

public class ScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        IOTHelper iotHelper = new IOTHelper();
        iotHelper.waterPlants(context);
        displayNotification(context);
    }

    public void displayNotification(Context context) {
        // Display a notification when the scheduled time is reached
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Gro Schedules")
                .setContentText("I have watered the plants!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        // Send notification
        notificationManager.notify(1, notification);
    }
}
