package com.sudo.nikhil.gro;

import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.ALARM_SERVICE;

import java.util.regex.*;

public class RegexEngine {

    // Global variable for accessing the app's context
    Context appContext;
    // Request queue for requests
    RequestQueue queue;
    // OpenWeatherMap API key
    private static final String API_KEY = "b39faff683fa71a4d461a7e62fbf42d1";
    // OpenWeatherMap API URL
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=Singapore&appid=" + API_KEY;
    // Schedule manager object to handle schedules
    private ScheduleManager scheduleManager;
    // Current temperature and humidity
    private String currentTemp, currentHum;

    RegexEngine(Context appContext) {
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(ALARM_SERVICE);
        scheduleManager = new ScheduleManager(alarmManager, appContext);
        queue =  Volley.newRequestQueue(appContext);
        this.appContext = appContext;
    }

    // Check if a specific word is present in the utterance
    private boolean wordInUtt(String word, String utt) {
        Pattern argPattern = Pattern.compile(word);
        Matcher match = argPattern.matcher(utt);
        return match.find();
    }

    // Check for and return time in utterance
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
                return " ";
        }
    }

    // Process all utterances spoken by the user
    public String coreUttProcessor(String utt) {
        if (wordInUtt("water", utt) && wordInUtt("now", utt)) {
            return  "Ok, watering plants now";
        } else if (wordInUtt("temperature", utt)) {
            return "Its" + getWeatherInfo("temp") + "degrees celsius";
        } else if (wordInUtt("humidity", utt)) {
            return "The humidity is at" + getWeatherInfo("hum") + "%";
        } else if (wordInUtt("water", utt) && wordInUtt("at", utt)) {
            if (!getTime(utt).equals(" ")) {
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

    // Request a string response from the provided URL.
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, WEATHER_URL, null,
            new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    // Display the first 500 characters of the response string.
                    try {
                        JSONObject main = new JSONObject(response.getString("main"));
                        currentTemp = main.getString("temp");
                        currentHum = main.getString("humidity");
                        Log.i("RES", "Temp: " + main.getString("temp") + " Hum: " + main.getString("humidity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(appContext, "An error occurred while retrieving weather info", Toast.LENGTH_LONG).show();
            Log.e("ERR", "ERROR RET WEATHER DATA");
        }
    });


    // Call the OpenWeatherMap API and get data such as temperature and humidity
    private String getWeatherInfo(String key) {
        // Add the request to the RequestQueue to invoke the API
        queue.add(jsonObjectRequest);
        // TODO: Make Volley request synchronous
        switch (key) {
            case "temp":
                return String.valueOf(Float.parseFloat(currentTemp) - 273.15);
            case "hum":
                return currentHum;
            default:
                return " ";
        }
    }

}
