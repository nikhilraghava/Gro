package com.sudo.nikhil.gro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class ScheduleManager {

    // The alarm manager is a global variable so all methods can access it
    private AlarmManager schAlarm;
    // Global variable for the context the alarm is set in
    private Context schContext;

    // Constructor that takes in alarm manager as a parameter from the MainActivity
    ScheduleManager(AlarmManager schAlarm, Context schContext) {
        this.schAlarm = schAlarm;
        this.schContext = schContext;
    }

    // Set the alarm at the specified timing
    public void setSchedule(int hour, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        Intent myIntent = new Intent(schContext.getApplicationContext(), ScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(schContext, 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        schAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // Cancel the set schedules
    public void cancelSchedule() {
        Intent myIntent = new Intent(schContext.getApplicationContext(), ScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(schContext.getApplicationContext(), 100, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        schAlarm.cancel(pendingIntent);
    }
}
