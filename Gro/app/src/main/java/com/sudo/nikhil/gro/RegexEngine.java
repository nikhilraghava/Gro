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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.ALARM_SERVICE;

import java.util.regex.*;

public class RegexEngine {

    // OpenWeatherMap API key
    private static final String OWM_API_KEY = "b39faff683fa71a4d461a7e62fbf42d1";
    // OpenWeatherMap current weather API URL
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=Singapore&appid=" + OWM_API_KEY;
    // OpenWeatherMap weather forecast API URL
    private static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast?q=Singapore&appid=" + OWM_API_KEY;
    // Schedule manager object to handle schedules
    private ScheduleManager scheduleManager;
    // Current temperature and humidity
    private String currentTemp, currentHum;
    // Weather forecast
    private boolean rainIsForecast;
    // App context
    private Context appContext;

    RegexEngine(final Context appContext) {
        // Set app context to global variable
        this.appContext = appContext;
        // Initialize alarm manager
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(ALARM_SERVICE);
        // Initialize schedule manager
        scheduleManager = new ScheduleManager(alarmManager, appContext);
        // Initialize request queue
        RequestQueue queue = Volley.newRequestQueue(appContext);

        // Check current weather
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, WEATHER_URL, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
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

        // Check forecast
        JsonObjectRequest JORTwo = new JsonObjectRequest(Request.Method.GET, FORECAST_URL, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                // Check forecast for rain
                rainIsForecast = checkForecastForRain(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, "An error occurred while retrieving weather forecast info", Toast.LENGTH_LONG).show();
                Log.e("ERR", "ERROR RET WEATHER FORECAST DATA");
            }
        });

        // Invoke the API and get the current weather data
        queue.add(jsonObjectRequest);
        // Invoke the API and get the weather forecast data
        queue.add(JORTwo);
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
            IOTHelper iotHelper = new IOTHelper();
            iotHelper.waterPlants(appContext);
            return  "Ok, watering plants now";
        } else if (wordInUtt("temperature", utt)) {
            // Get the current temperature using OpenWeatherMap API
            return "Its" + getWeatherInfo("temp") + "degrees celsius";
        } else if (wordInUtt("humidity", utt)) {
            // Get the current humidity using OpenWeatherMap API
            return "The humidity is at" + getWeatherInfo("hum") + "%";
        } else if (wordInUtt("water", utt) && wordInUtt("at", utt)) {
            if (!getTime(utt).equals(" ")) {
                // Check if rain is forecast for the day
                if (rainIsForecast) {
                    return "The weather forecast says that it is likely to rain today. It might be unnecessary to water the plants today.";
                } else {
                    // Get the time specified in the utterance
                    String time = getTime(utt);
                    // Set the schedule at the specified time
                    setScheduleAtTime(time);
                    // Inform the user of the set schedule
                    return "Ok, I will water the plants at " + time;
                }
            } else
                // Tell the user there was a problem retrieving the time from utterance
                return "Sorry I didn't catch that. Try speaking again.";
        } else if (wordInUtt("cancel", utt) && wordInUtt("schedule", utt)) {
            try {
                // Cancel previously set schedules
                scheduleManager.cancelSchedule();
                return "Ok I have cancelled your schedules";
            } catch (RuntimeException e) {
                return "You have not set a schedule yet...";
            }
        } else
            return "Sorry, I can't do that";
    }

    // Set schedule at specified time
    private void setScheduleAtTime(String time) {
        int hours, minutes;
        if (time.contains("a.m.")) {
            if (time.contains(":")) {
                String[] splitByColon = time.replace("a.m.", "").split(":");
                hours = Integer.parseInt(splitByColon[0].replace(" ", ""));
                minutes = Integer.parseInt(splitByColon[1].replace(" ", ""));
                // Set schedule and notify user when time is up
                scheduleManager.setSchedule(hours, minutes);
            } else {
                time = time.replace("a.m.", "").replace(" ", "");
                hours = Integer.parseInt(time);
                scheduleManager.setSchedule(hours, 0);
            }
        } else if (time.contains("p.m.")) {
            if (time.contains(":")) {
                String[] splitByColon = time.replace("p.m.", "").split(":");
                hours = Integer.parseInt(splitByColon[0].replace(" ", "")) + 12;
                minutes = Integer.parseInt(splitByColon[1].replace(" ", ""));
                // Set schedule and notify user when time is up
                scheduleManager.setSchedule(hours, minutes);
            } else {
                time = time.replace("p.m.", "").replace(" ", "");
                hours = Integer.parseInt(time) + 12;
                scheduleManager.setSchedule(hours, 0);
            }
        }
    }


    // Call the OpenWeatherMap API and get data such as temperature and humidity
    private String getWeatherInfo(String key) {
        switch (key) {
            case "temp":
                return String.valueOf(Math.round(Float.parseFloat(currentTemp) - 273.15));
            case "hum":
                return currentHum;
            default:
                return " ";
        }
    }

    // Check if rain is forecast for the day
    private boolean checkForecastForRain(JSONObject response) {
        boolean rain = false;
        try {
            JSONArray tempArray;
            String forecast;
            for (int i = 0; i < 8; i++) {
                tempArray = response.getJSONArray("list").getJSONObject(i).getJSONArray("weather");
                forecast = tempArray.getJSONObject(0).getString("main");
                if (forecast.equals("Rain")) {
                    rain = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rain;
    }

}
