package org.gluu.super_gluu.app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by nazaryavornytskyy on 4/3/17.
 */

public class SuperGluuFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        //current one - dr3_ZGrvyQ8:APA91bGp0SloR735q4q9chrjslWSTxBWi2HlJFqxBi7OJWNzL5yxTX4uwrIyv7Ghjs0UpLCl5f0a6Rsk7AF4aLUo-FOcnTgvBafya4pQdrXuGkG2PyFlCLjr04nsh3GJ7ACNmb-7VGa4
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        savePushRegistrationId(refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }

    private void savePushRegistrationId(String pushNotificationToken){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pushNotificationToken", pushNotificationToken);
        editor.commit();
    }

    public String getPushRegistrationId(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("pushNotificationToken", "");
    }

}
