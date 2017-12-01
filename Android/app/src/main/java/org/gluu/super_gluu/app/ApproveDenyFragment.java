package org.gluu.super_gluu.app;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import org.apache.commons.codec.binary.StringUtils;
import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.model.LogInfo;
import org.gluu.super_gluu.app.settings.Settings;
import org.gluu.super_gluu.model.OxPush2Request;
import org.gluu.super_gluu.store.AndroidKeyDataStore;
import org.gluu.super_gluu.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import SuperGluu.app.R;

import static org.bouncycastle.asn1.ua.DSTU4145NamedCurves.params;

public class ApproveDenyFragment extends Fragment implements View.OnClickListener{

    SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    private Boolean isUserInfo = false;
    private LogInfo logInfo;
    private OxPush2Request push2Request;
    private GluuMainActivity.RequestProcessListener listener;
    public OnDeleteLogInfoListener deleteLogListener;

    private CircleProgressBar mLineProgressBar;

    private Timer clock;
    private Handler handler;

    private ImageView logo_imageView;

    int sec = 40;

    private BroadcastReceiver mDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAlertView();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_approve_deny, container, false);
        handler = initHandler(rootView);
        mLineProgressBar = (CircleProgressBar) rootView.findViewById(R.id.line_progress);
        mLineProgressBar.setMax(sec);
        Button approveButton = (Button) rootView.findViewById(R.id.button_approve);
        Button denyButton = (Button) rootView.findViewById(R.id.button_deny);

        View actionBarView = (View) rootView.findViewById(R.id.actionBarView);
        actionBarView.findViewById(R.id.action_right_button).setVisibility(View.GONE);
        ImageView titleIcon = (ImageView) actionBarView.findViewById(R.id.actionbar_icon);
        TextView title = (TextView) actionBarView.findViewById(R.id.actionbar_textview);
        LinearLayout leftButton = (LinearLayout) actionBarView.findViewById(R.id.action_left_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_textView);
        //Setup fonts
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Semibold.otf");
        titleTextView.setTypeface(face);
        if (isUserInfo){
            View timerView = (View) rootView.findViewById(R.id.timer_view);

            TextView timerTextView = (TextView) rootView.findViewById(R.id.timer_textView);
//            Button closeButton = (Button) rootView.findViewById(R.id.approve_deny_close_button);
            timerView.setVisibility(View.GONE);
            timerTextView.setVisibility(View.GONE);
            titleTextView.setVisibility(View.GONE);//setText(R.string.info);
            approveButton.setVisibility(View.GONE);
            denyButton.setVisibility(View.GONE);
//            closeButton.setVisibility(View.GONE);
            mLineProgressBar.setVisibility(View.GONE);
            leftButton.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            title.setText("LOG");
            titleIcon.setVisibility(View.GONE);
        } else {
            actionBarView.setVisibility(View.GONE);
            RelativeLayout topRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.topRelativeLayout);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topRelativeLayout.getLayoutParams();
            params.topMargin = 10;
            topRelativeLayout.requestLayout();
//            rootView.findViewById(R.id.approve_deny_close_button).setVisibility(View.GONE);
            startClockTick(rootView);
            leftButton.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            titleIcon.setVisibility(View.VISIBLE);
        }

        updateLogInfo(rootView);
//        rootView.findViewById(R.id.approve_deny_close_button).setOnClickListener(this);
        approveButton.setOnClickListener(this);
        denyButton.setOnClickListener(this);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDeleteReceiver,
                new IntentFilter("on-delete-log-event"));

        return rootView;
    }

    public void getBitmapFromURL(final String src) {
        final ApproveDenyFragment mainFragmentObj = this;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        URL url = new URL(src);
                        String host = url.getHost();
                        URL mainUrl = new URL("https://www.google.com/s2/favicons?domain=" + host);
                        HttpURLConnection connection = (HttpURLConnection) mainUrl.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        mainFragmentObj.updateLogo(myBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
//                        return null;

//                        https://www.google.com/s2/favicons?domain=football.ua
//                        https://www.google.com/s2/favicons?domain=ce-release.gluu.org
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sec == 0){
            listener.onDeny();
            closeView();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDeleteReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeleteLogInfoListener) {
            deleteLogListener = (OnDeleteLogInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeleteKeyHandleListener");
        }
    }

    private void updateLogInfo(View rootView){
        if (push2Request != null){
            logo_imageView = (ImageView)rootView.findViewById(R.id.logo_imageView);
            TextView application = (TextView) rootView.findViewById(R.id.text_application_label);
            URL url = null;
            try {
                url = new URL(push2Request.getIssuer());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String baseUrl = "Gluu Server " + url.getHost();
            application.setText(baseUrl);
            TextView applicationUrl = (TextView) rootView.findViewById(R.id.text_application_value);
            applicationUrl.setText(push2Request.getIssuer());
            TextView userName = (TextView) rootView.findViewById(R.id.text_user_name_label);
            userName.setText(push2Request.getUserName());
            TextView locationIP = (TextView) rootView.findViewById(R.id.location_ip);
            locationIP.setText(push2Request.getLocationIP());
            TextView locationAddress = (TextView) rootView.findViewById(R.id.location_address);
            if (push2Request.getLocationCity() != null) {
                try {
                    locationAddress.setText(java.net.URLDecoder.decode(push2Request.getLocationCity(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.d(this.getClass().getName(), e.getLocalizedMessage());
                }
            }
            TextView type = (TextView) rootView.findViewById(R.id.text_type);
            AndroidKeyDataStore dataStore = new AndroidKeyDataStore(getContext());
            final List<byte[]> keyHandles = dataStore.getKeyHandlesByIssuerAndAppId(push2Request.getIssuer(), push2Request.getApp());
            final boolean isEnroll = (keyHandles.size() == 0) || StringUtils.equals(push2Request.getMethod(), "enroll");
            type.setText(capitalize(push2Request.getMethod()));
            TextView time = (TextView) rootView.findViewById(R.id.text_application_created_label);
            time.setText(getTimeFromString(push2Request.getCreated()));
            TextView date = (TextView) rootView.findViewById(R.id.text_created_value);
            date.setText(getDateFromString(push2Request.getCreated()));
            //Setup fonts
            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
            application.setTypeface(face);
            applicationUrl.setTypeface(face);
            userName.setTypeface(face);
            locationIP.setTypeface(face);
            locationAddress.setTypeface(face);
            type.setTypeface(face);
            time.setTypeface(face);
            date.setTypeface(face);

            //Load favicon by url
//            getBitmapFromURL(push2Request.getIssuer());
        }
    }

    private void updateLogo(final Bitmap logo){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //stuff that updates ui
                logo_imageView.setImageBitmap(logo);
            }
        });

    }

    private String getDateFromString(String dateString){
        SimpleDateFormat userDateTimeFormat = new SimpleDateFormat("MMM d, yyyy");
        Date createdDate = null;
        if (Utils.isNotEmpty(dateString)) {
            if (isUserInfo){
                Date resultdate = new Date(Long.valueOf(dateString));
                return userDateTimeFormat.format(resultdate);
            } else {
                try {
                    createdDate = isoDateTimeFormat.parse(dateString);
                } catch (ParseException ex) {
                    Log.e(this.getClass().getName(), "Failed to parse ISO date/time: " + dateString, ex);
                }
            }
        }

        String createdString = "";
        if (createdDate != null) {
            createdString = userDateTimeFormat.format(createdDate);
        }
        return createdString;
    }

    private String getTimeFromString(String dateString){
        SimpleDateFormat userDateTimeFormat = new SimpleDateFormat("HH:mm:ss");
        Date createdDate = null;
        if (Utils.isNotEmpty(dateString)) {
            if (isUserInfo){
                Date resultdate = new Date(Long.valueOf(dateString));
                return userDateTimeFormat.format(resultdate);
            } else {
                try {
                    createdDate = isoDateTimeFormat.parse(dateString);
                } catch (ParseException ex) {
                    Log.e(this.getClass().getName(), "Failed to parse ISO date/time: " + dateString, ex);
                }
            }
        }

        String createdString = "";
        if (createdDate != null) {
            createdString = userDateTimeFormat.format(createdDate);
        }
        return createdString;
    }

    public static String capitalize(String text){
        String c = (text != null)? text.trim() : "";
        String[] words = c.split(" ");
        String result = "";
        for(String w : words){
            result += (w.length() > 1? w.substring(0, 1).toUpperCase(Locale.US) + w.substring(1, w.length()).toLowerCase(Locale.US) : w) + " ";
        }
        return result.trim();
    }

    void showAlertView(){
        GluuMainActivity.GluuAlertCallback listener = new GluuMainActivity.GluuAlertCallback(){
            @Override
            public void onPositiveButton() {
                if (deleteLogListener != null){
                    deleteLogListener.onDeleteLogInfo(push2Request);
                }
//                android.support.v4.app.FragmentManager fm = getFragmentManager();
//                fm.popBackStack();
            }

            @Override
            public void onNegativeButton() {
                //Skip here
            }
        };
        CustomGluuAlert gluuAlert = new CustomGluuAlert(getActivity());
        gluuAlert.setMessage(getActivity().getApplicationContext().getString(R.string.clear_log_title));
        gluuAlert.setYesTitle(getActivity().getApplicationContext().getString(R.string.yes));
        gluuAlert.setNoTitle(getActivity().getApplicationContext().getString(R.string.no));
        gluuAlert.setmListener(listener);
        gluuAlert.show();
    }

    public Boolean getIsUserInfo() {
        return isUserInfo;
    }

    public void setIsUserInfo(Boolean isUserInfo) {
        this.isUserInfo = isUserInfo;
    }

    public LogInfo getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(LogInfo logInfo) {
        this.logInfo = logInfo;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_approve){
            if (listener != null){
                listener.onApprove();
            }
        } else if (v.getId() == R.id.button_deny){
            if (listener != null){
                listener.onDeny();
            }
        }
        stopClocking();
        closeView();
    }

    private void startClockTick(final View rootView){
        clock = new Timer();
        clock.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sec--;
                //send message to update UI
                if (handler == null){
                    handler = initHandler(rootView);
                }
                handler.sendEmptyMessage(0);
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second
    }

    private void stopClocking(){
        if (clock != null) {
            clock.cancel();
        }
        clock = null;
    }

    private void closeView(){
        setIsBackButtonVisible(false);
        if (isUserInfo) {
            setIsButtonVisible(true);
            getActivity().invalidateOptionsMenu();
        }
        try {
            getFragmentManager().popBackStack();
        }
        catch (RuntimeException ex){
            //ignore there
        }
    }

    public void setIsButtonVisible(Boolean isVsible){
        SharedPreferences preferences = getContext().getSharedPreferences("CleanLogsSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCleanButtonVisible", isVsible);
        editor.commit();
    }

    public void setIsBackButtonVisible(Boolean isVsible){
        SharedPreferences preferences = getContext().getSharedPreferences("CleanLogsSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isBackButtonVisible", isVsible);
        editor.commit();
    }

    private Handler initHandler(final View rootView){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler = new Handler(){
                    public void handleMessage(android.os.Message msg){
                        if (sec == 0){
                            stopClocking();
                            closeView();
                        }
                        mLineProgressBar.setProgress(sec);
                        TextView seconds = (TextView) rootView.findViewById(R.id.timer_textView);
                        String secStr = sec < 10 ? "0" + sec : String.valueOf(sec);
                        seconds.setText(secStr);
                    }
                };
            }
        });
        return handler;
    }

    public OxPush2Request getPush2Request() {
        return push2Request;
    }

    public void setPush2Request(OxPush2Request push2Request) {
        this.push2Request = push2Request;
    }

    public GluuMainActivity.RequestProcessListener getListener() {
        return listener;
    }

    public void setListener(GluuMainActivity.RequestProcessListener listener) {
        this.listener = listener;
    }

    public interface OnDeleteLogInfoListener {
        void onDeleteLogInfo(OxPush2Request oxPush2Request);
        void onDeleteLogInfo(List<LogInfo> logInfos);
    }
}
