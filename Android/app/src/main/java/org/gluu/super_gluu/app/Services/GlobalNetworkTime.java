package org.gluu.super_gluu.app.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

/**
 * Created by nazaryavornytskyy on 9/28/17.
 */

public class GlobalNetworkTime {

    private GetGlobalTimeCallback timeCallback;

    public void getCurrentNetworkTime(Context context, final GetGlobalTimeCallback timeCallback){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://api.geonames.org/timezoneJSON?formatted=true&lat=47.01&lng=10.2&username=nazar.y&style=full";//"http://www.timeapi.org/utc/now";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            try {
                                JSONObject fieldsJson = new JSONObject(response);
                                String time = fieldsJson.getString("time");
                                Date date = simpleDateFormat.parse(time);
                                Long currentTime = date.getTime();
                                if (currentTime != null) {
                                    timeCallback.onGlobalTime(currentTime);
                                } else {
                                    timeCallback.onGlobalTime(System.currentTimeMillis());
                                }
                                Log.d(TAG, "current time: " + time);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                timeCallback.onGlobalTime(System.currentTimeMillis());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            timeCallback.onGlobalTime(System.currentTimeMillis());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "onErrorResponse: "+ error.getMessage());
            }
        });
        queue.add(stringRequest);
    }

    public interface GetGlobalTimeCallback {
        void onGlobalTime(Long time);
    }
}
