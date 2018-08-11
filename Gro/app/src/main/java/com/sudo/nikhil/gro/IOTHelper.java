package com.sudo.nikhil.gro;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class IOTHelper {

    // ESP8266 LED is active low, requires logic 0 to turn on
    private static final String ESP8266_IOT_URL = "http://blynk-cloud.com/3e95bb6b78eb460e924e937aed3fdd15/update/D2?value=";
    private static final String TURN_ON = ESP8266_IOT_URL + "0";
    private static final String TURN_OFF = ESP8266_IOT_URL + "1";

    public void waterPlants(Context context) {
        // Send a GET request to the Blynk servers to trigger the ESP8266's LED
        // Initialize request queue
        final RequestQueue queue = Volley.newRequestQueue(context);

        // Initialize a JSONObjectRequest to perform a GET request to turn on the LED
        JsonObjectRequest JOROne = new JsonObjectRequest(Request.Method.GET, TURN_ON, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                // Notify log if successful
                Log.i("SUC", "GET REQUEST SUCCESSFUL");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error
                Log.e("ERR", "ERROR SENDING GET REQUEST");
            }
        });

        // Initialize a JSONObjectRequest to perform a GET request to turn off the LED
        final JsonObjectRequest JORTwo = new JsonObjectRequest(Request.Method.GET, TURN_OFF, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                // Notify log if successful
                Log.i("SUC", "LED TURNED OFF");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error
                Log.e("ERR", "ERROR TURNING OFF LED");
            }
        });

        // Invoke the API in the constructor to cache the data
        queue.add(JOROne);
        // Define a new handler object
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Turn off LED after 5 seconds
                queue.add(JORTwo);
            }
        }, 5000);
    }
}
