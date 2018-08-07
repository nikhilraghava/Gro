package com.sudo.nikhil.gro;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notify extends Application {

    // Notification channel
    public static final String CHANNEL_ID = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();
        // Create notification channel
        createNotificationChannel();
    }

    // Create notification channel
    private void createNotificationChannel() {
        // Check if the device is running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a new channel with importance high in the notification panel
            NotificationChannel channelOne = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            // Set the description for the channel
            channelOne.setDescription("Notification channel dedicated for the Gro App");
            // Create the notification channel
            NotificationManager manager = getSystemService(NotificationManager.class);
            // Prevent null pointer exception
            if (manager != null) {
                manager.createNotificationChannel(channelOne);
            }
        }
    }
}
