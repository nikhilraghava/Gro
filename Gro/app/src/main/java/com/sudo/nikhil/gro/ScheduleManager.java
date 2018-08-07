package com.sudo.nikhil.gro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class ScheduleManager {

    // The alarm manager is a global variable so all methods can access it
    AlarmManager schAlarm;
    // Global variable for the context the alarm is set in
    Context schContext;
    // Global variable for the pending intent object
    private PendingIntent pendingIntent;

    // Constructor that takes in alarm manager as a parameter from the MainActivity
    public ScheduleManager(AlarmManager schAlarm, Context schContext) {
        this.schAlarm = schAlarm;
        this.schContext = schContext;
    }

    // Set the alarm at the specified timing
    public void setSchedule(int hour, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        Intent myIntent = new Intent(schContext.getApplicationContext(), ScheduleReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(schContext, 0, myIntent, 0);
        schAlarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // Cancel the set schedules
    public void cancelSchedule() {
        schAlarm.cancel(pendingIntent);
    }
}
