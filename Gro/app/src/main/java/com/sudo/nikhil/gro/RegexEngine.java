package com.sudo.nikhil.gro;

import android.app.AlarmManager;
import android.content.Context;
import android.support.annotation.Nullable;

import java.util.regex.*;

import static android.content.Context.ALARM_SERVICE;

public class RegexEngine {

    // Global variable that allows us to access's the app's context
    private Context appContext;

    // Alarm manager that handles schedules for the app
    // Initialize the alarm manager to manage scheduled services
    private AlarmManager alarmManager;
    private ScheduleManager scheduleManager;

    RegexEngine(Context appContext) {
        alarmManager = (AlarmManager) appContext.getSystemService(ALARM_SERVICE);
        scheduleManager = new ScheduleManager(alarmManager, appContext);
        this.appContext = appContext;
    }

    // Check if a specific word is present in the utterance
    private boolean wordInUtt(String word, String utt) {
        Pattern argPattern = Pattern.compile(word);
        Matcher match = argPattern.matcher(utt);
        return match.find();
    }

    // Check for and return time in utterance
    @Nullable
    private String getTime(String utt) {
        Pattern pattern = Pattern.compile(".*?(\\d{1,2}:\\d{1,2} (a.m.|p.m.)).*");
        Matcher matcher = pattern.matcher(utt);
        if (matcher.find())
            return matcher.group(1);
        else {
            pattern = Pattern.compile(".*?(\\d{1,2} (a.m.|p.m.)).*");
            matcher = pattern.matcher(utt);
            if (matcher.find())
                return matcher.group(1);
            else
                return null;
        }
    }

    // Process all utterances spoken by the user
    public String coreUttProcessor(String utt) {
        if (wordInUtt("water", utt) && wordInUtt("now", utt)) {
            return  "Ok, watering plants now";
        } else if (wordInUtt("temperature", utt)) {
            // TODO: Call OpenWeatherMap API and get the current temperature
            return "Its 32 degree celsius";
        } else if (wordInUtt("humidity", utt)) {
            // TODO: Call OpenWeatherMap API and get the current humidity
            return "The humidity is at 65%";
        } else if (wordInUtt("water", utt) && wordInUtt("at", utt)) {
            if (getTime(utt) != null) {
                // Get the time specified in the utterance
                String time = getTime(utt);
                // Set the schedule at the specified time
                setScheduleAtTime(time);
                // Inform the user of the set schedule
                return "Ok, I will water the plants at " + time;
            } else
                // Tell the user there was a problem retrieving the time
                return "Sorry I didn't catch that. Try speaking again.";
        } else
            return "Sorry, I can't do that";
    }

    // Set schedule at specified time
    private void setScheduleAtTime(String time) {
        int hours, minutes;
        if (time.contains("a.m.")) {
            String[] splitByColon = time.replace("a.m.", "").split(":");
            hours = Integer.parseInt(splitByColon[0].replace(" ", ""));
            minutes = Integer.parseInt(splitByColon[1].replace(" ", ""));
            // Set schedule and notify user when time is up
            scheduleManager.setSchedule(hours, minutes);
        } else if (time.contains("p.m.")) {
            String[] splitByColon = time.replace("p.m.", "").split(":");
            hours = Integer.parseInt(splitByColon[0].replace(" ", "")) + 12;
            minutes = Integer.parseInt(splitByColon[1].replace(" ", ""));
            // Set schedule and notify user when time is up
            scheduleManager.setSchedule(hours, minutes);
        }
    }

}
