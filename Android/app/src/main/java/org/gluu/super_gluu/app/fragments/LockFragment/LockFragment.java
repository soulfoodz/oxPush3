package org.gluu.super_gluu.app.fragments.LockFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.github.simonpercic.rxtime.RxTime;
import org.gluu.super_gluu.app.activities.MainActivity;
import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.services.GlobalNetworkTime;
import org.gluu.super_gluu.app.settings.Settings;
import SuperGluu.app.R;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.content.ContentValues.TAG;

/**
 * Created by nazaryavornytskyy on 4/20/16.
 */
public class LockFragment extends Fragment {

    final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private Timer clock;
    private Handler handler;
    private MainActivity.OnLockAppTimerOver listener;
    private Boolean isRecover;

    private Context context;

    private TextView txtTime;

    int min = 10;
    int sec = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        context = getContext();
        final View rootView = inflater.inflate(R.layout.fragment_lock_app, container, false);

        handler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if (min == 0 && sec == 0){
                    stopClocking();
                    resetCurrentPinAttempts();
                    if (listener != null){
                        listener.onTimerOver();
                    }
                }
                TextView minutes = (TextView) rootView.findViewById(R.id.locked_screen_minutes);
                TextView seconds = (TextView) rootView.findViewById(R.id.locked_screen_seconds);
                String minStr = min < 10 ? "0" + min + ":" : min + ":";
                String secStr = sec < 10 ? "0" + sec : String.valueOf(sec);
                minutes.setText(minStr);
                seconds.setText(secStr);
            }
        };
//        if (isRecover) {
            min = 10;
            calculateTimeLeft();
//        } else {
//            startClockTick();
//        }

        return rootView;
    }

    private void startClockTick() {
        if (!Settings.isAppLocked(context) && min == 0 && sec == 0) {
            if (listener != null) {
                listener.onTimerOver();
            }
        } else {
            showAlertView("You entered wrong Pin code many times, application is locked");
            clock = new Timer();
            clock.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (sec == 0) {
                        min = min - 1;
                        min = min < 0 ? 0 : min;
                        sec = 60;
                    }
                    sec--;
                    //send message to update UI
                    handler.sendEmptyMessage(0);
                }
            }, 0, 1000);//put here time 1000 milliseconds=1 second
        }
    }

    private void stopClocking(){
        if (clock != null) {
            clock.cancel();
            clock = null;
        }
    }

    public MainActivity.OnLockAppTimerOver getListener() {
        return listener;
    }

    public void setListener(MainActivity.OnLockAppTimerOver listener) {
        this.listener = listener;
    }

    private void calculateTimeLeft(){
        new GlobalNetworkTime().getCurrentNetworkTime(context, new GlobalNetworkTime.GetGlobalTimeCallback() {
            @Override
            public void onGlobalTime(Long time) {
                currentNetworkTime(time);
                startClockTick();
            }
        });
    }

    private String getAppLockedTime(){
        SharedPreferences preferences = context.getSharedPreferences("PinCodeSettings", Context.MODE_PRIVATE);
        return preferences.getString("appLockedTime", "");
    }

    private void currentNetworkTime(Long time){
//        Date lockedDate;
//        Date currentDate;
//        lockedDate = new Date(Long.parseLong(getAppLockedTime()));// isoDateTimeFormat.parse(getAppLockedTime());
//        currentDate = new Date(time);//isoDateTimeFormat.parse(String.valueOf(time));
        String lockedTimeStr = getAppLockedTime();
        if (lockedTimeStr.equalsIgnoreCase("")){
            Settings.setAppLocked(context, false);
        } else {
            long lockedTime = Long.parseLong(getAppLockedTime());
            long diff = time - lockedTime;
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long elapsedMinutes = diff / minutesInMilli;
            diff = diff % minutesInMilli;
            long elapsedSeconds = diff / secondsInMilli;
            min = (int) (min - elapsedMinutes);
            sec = (min > 11 || min < 0) ? 0 : (int) elapsedSeconds;
            min = min < 0 ? 0 : min;
            min = min > 10 ? 0 : min;
            if (min == 0 && sec == 0) {
                Settings.setAppLocked(context, false);
            }
        }
    }

    public void resetCurrentPinAttempts(){
        SharedPreferences preferences = context.getSharedPreferences("PinCodeSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentPinCodeAttempts", String.valueOf(getPinCodeAttempts()));
        editor.commit();
    }

    public int getPinCodeAttempts(){
        SharedPreferences preferences = context.getSharedPreferences("PinCodeSettings", Context.MODE_PRIVATE);
        String pinCode = preferences.getString("pinCodeAttempts", "5");
        return Integer.parseInt(pinCode);
    }

    public Boolean getIsRecover() {
        return isRecover;
    }

    public void setIsRecover(Boolean isRecover) {
        this.isRecover = isRecover;
    }

    private void showAlertView(String message){
        CustomGluuAlert gluuAlert = new CustomGluuAlert(getActivity());
        gluuAlert.setMessage(message);
        gluuAlert.show();
    }
}
