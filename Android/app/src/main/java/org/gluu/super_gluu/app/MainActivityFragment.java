/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package org.gluu.super_gluu.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.gluuToast.GluuToast;
import org.gluu.super_gluu.app.listener.OxPush2RequestListener;
import org.gluu.super_gluu.app.services.FingerPrintManager;
import org.gluu.super_gluu.app.settings.Settings;
import org.gluu.super_gluu.model.OxPush2Request;
import org.gluu.super_gluu.store.AndroidKeyDataStore;
import org.gluu.super_gluu.u2f.v2.SoftwareDevice;
import org.gluu.super_gluu.u2f.v2.exception.U2FException;
import org.gluu.super_gluu.u2f.v2.model.TokenResponse;
import org.gluu.super_gluu.u2f.v2.store.DataStore;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import SuperGluu.app.BuildConfig;
import SuperGluu.app.R;

/**
 * Main activity fragment
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class MainActivityFragment extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener {

    private static final String TAG = "main-activity-fragment";

    private OxPush2RequestListener oxPush2RequestListener;

    private LayoutInflater inflater;

    private Context context;

    private LinearLayout adView;
    private InterstitialAd mInterstitialAd;

    private Button scanButton;

    private SoftwareDevice u2f;
    private AndroidKeyDataStore dataStore;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            if (context != null && !message.isEmpty()) {
//                showToastWithText(message);
                showDialog(message);
            }
            Boolean isAdFree = Settings.getPurchase(context);
            if (mInterstitialAd.isLoaded() && !isAdFree) {
                if (mInterstitialAd == null){
                    initGoogleInterstitialAd();
                }
                mInterstitialAd.show();
            }
        }
    };

    private BroadcastReceiver mAdFreeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Boolean isAdFree = intent.getBooleanExtra("isAdFree", false);
            if (context != null && adView != null) {
                if (isAdFree) {
                    adView.setVisibility(View.GONE);
                }
            }
        }
    };

    private BroadcastReceiver mPushMessageReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!getActivity().isDestroyed()) {
                // Get extra data included in the Intent
                String message = intent.getStringExtra(GluuMainActivity.QR_CODE_PUSH_NOTIFICATION_MESSAGE);
                final OxPush2Request oxPush2Request = new Gson().fromJson(message, OxPush2Request.class);
                onQrRequest(oxPush2Request);
                //play sound and vibrate
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(context, notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(800);
            }
        }
    };

    private BroadcastReceiver onAdFree = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runSubscribeFlow();
        }
    };

    private BroadcastReceiver onRestorePurchase = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runRestorePurchaseFlow();
        }
    };

    public MainActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        this.inflater = inflater;
        context = view.getContext();
        this.dataStore = new AndroidKeyDataStore(context);
        this.u2f = new SoftwareDevice(getActivity(), dataStore);
        adView = (LinearLayout)view.findViewById(R.id.view_ad_free);
//        adView.setVisibility(View.GONE);
        View actionBarView = (View) view.findViewById(R.id.actionBarView);
        actionBarView.findViewById(R.id.action_left_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.action_right_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.actionbar_icon).setVisibility(View.VISIBLE);
        actionBarView.findViewById(R.id.actionbar_textview).setVisibility(View.GONE);
        Boolean isAdFree = Settings.getPurchase(context);
        if (isAdFree){
            adView.setVisibility(View.GONE);
        }
        Button adFreeButton = (Button) view.findViewById(R.id.button_ad_free);
        adFreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runSubscribeFlow();
            }
        });
        scanButton = (Button) view.findViewById(R.id.button_scan);
        scanButton.setOnClickListener(this);

        //Setup message receiver
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
//                new IntentFilter("ox_request-precess-event"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAdFreeReceiver,
                new IntentFilter("on-ad-free-event"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onAdFree,
                new IntentFilter("on-ad-free-flow"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onRestorePurchase,
                new IntentFilter("on-restore-purchase-flow"));
        //Init GoogleMobile AD
        initGoogleInterstitialAd();

        //Setup fonts
        TextView welcomeTextView = (TextView) view.findViewById(R.id.textView);
        TextView subTextView = (TextView) view.findViewById(R.id.textView2);

        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Semibold.otf");
        Typeface faceLight = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
        scanButton.setTypeface(face);
        adFreeButton.setTypeface(face);
        welcomeTextView.setTypeface(face);
        subTextView.setTypeface(faceLight);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConnected(context)) {
//            showToastWithText("Your device is currently unable to establish a network connection. You will need a connection to approve or deny authentication requests with Super Gluu.");
            scanButton.setEnabled(false);
        } else {
            scanButton.setEnabled(true);
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("ox_request-precess-event"));
        //Check push data
        checkIsPush();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mPushMessageReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OxPush2RequestListener) {
            oxPush2RequestListener = (OxPush2RequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        oxPush2RequestListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Toast.makeText(getActivity(), R.string.process_qr_code, Toast.LENGTH_SHORT).show();

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
//                    showToastWithText(context.getString(R.string.process_qr_code));
                    // Parsing bar code reader result
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (BuildConfig.DEBUG) Log.d(TAG, "Parsing QR code result: " + result.toString());
                    OxPush2Request oxPush2Request = null;
                    try {
                        oxPush2Request = new Gson().fromJson(result.getContents(), OxPush2Request.class);
                    }
                    catch (Exception ex){
                        //skip exception there
                    }
                    onQrRequest(oxPush2Request);
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    showToastWithText(context.getString(R.string.canceled_process_qr_code));
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        submit();
    }

    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            submit();
            return true;
        }

        return false;
    }

    private  void runSubscribeFlow(){
        if (oxPush2RequestListener != null) {
            oxPush2RequestListener.onAdFreeButtonClick();
        }
    }

    private  void runRestorePurchaseFlow(){
        if (oxPush2RequestListener != null) {
            oxPush2RequestListener.onPurchaseRestored();
        }
    }

    private void initGoogleInterstitialAd(){
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-3326465223655655/1731023230");

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void onQrRequest(OxPush2Request oxPush2Request){
        if (oxPush2Request == null){
            showToastWithText("You scanned wrong QR code");
        } else {
            oxPush2RequestListener.onQrRequest(oxPush2Request);
        }
    }

    private void showToastWithText(String text){
        GluuToast gluuToast = new GluuToast(context);
        View view = inflater.inflate(R.layout.gluu_toast, null);
        gluuToast.showGluuToastWithText(view, text);
    }

    private void submit() {
//        if (oxPush2RequestListener != null) {

//        String message = "{\"app\":\"https://ce-release.gluu.org/identity/authentication/authcode\",\"method\":\"authenticate\",\"req_ip\":\"77.123.160.182\",\"created\":\"2017-07-30T20:19:27.307000\",\"issuer\":\"https://ce-release.gluu.org\",\"req_loc\":\"Ukraine%2C%20L%27vivs%27ka%20Oblast%27%2C%20Lviv\",\"state\":\"9e8fe200-5bab-4be1-857f-cc9a805a4738\",\"username\":\"qwerty2\"}";
//            OxPush2Request oxPush2Request = null;
//            try {
//                oxPush2Request = new Gson().fromJson(message, OxPush2Request.class);
//            }
//            catch (Exception ex){
//                //skip exception there
//            }
//            onQrRequest(oxPush2Request);
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt(getString(R.string.scan_oxpush2_prompt));
            integrator.initiateScan();
//        }
    }

    private void showDialog(String message){
        Activity activity = getActivity();
        String textSuccess = getActivity().getApplicationContext().getString(R.string.auth_result_success);
        String textDeny = getActivity().getApplicationContext().getString(R.string.deny_result_success);
        String finalMessage = message;
        String finalTitle = "";
        if (message.equalsIgnoreCase(textSuccess)){
            finalMessage = activity.getApplicationContext().getString(R.string.auth_result_success);
            finalTitle = activity.getApplicationContext().getString(R.string.success);
        } else if (message.equalsIgnoreCase(textDeny)){
            finalMessage = activity.getApplicationContext().getString(R.string.deny_result_success);
            finalTitle = activity.getApplicationContext().getString(R.string.failed);
        }
        final CustomGluuAlert gluuAlert = new CustomGluuAlert(activity);
        gluuAlert.setMessage(finalTitle);
        gluuAlert.setSub_title(finalMessage);
        gluuAlert.setYesTitle(activity.getApplicationContext().getString(R.string.ok));
        gluuAlert.type = NotificationType.DEFAULT;
        gluuAlert.setmListener(new GluuMainActivity.GluuAlertCallback() {
            @Override
            public void onPositiveButton() {
                //Skip here
            }

            @Override
            public void onNegativeButton() {
                //Skip here
            }
        });
        gluuAlert.show();
    }

    public void checkIsPush(){
        final SharedPreferences preferences = context.getSharedPreferences("oxPushSettings", Context.MODE_PRIVATE);
        final String requestString = preferences.getString("oxRequest", "null");
        if (!requestString.equalsIgnoreCase("null")) {
            //First need to check is app protected by Fingerprint
            Boolean isFingerprint = Settings.getFingerprintEnabled(context);
            if (isFingerprint){
                FingerPrintManager fingerPrintManager = new FingerPrintManager(getActivity());
                fingerPrintManager.onFingerPrint(new FingerPrintManager.FingerPrintManagerCallback() {
                    @Override
                    public void fingerprintResult(Boolean isSuccess) {
                        makeOxRequest(preferences, requestString);
                    }
                });
            } else {
                makeOxRequest(preferences, requestString);
            }
        }
    }

    private void makeOxRequest(SharedPreferences preferences, String requestString){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("oxRequest", null);
        editor.apply();
        Settings.setPushDataEmpty(getContext());
        final OxPush2Request oxPush2Request = new Gson().fromJson(requestString, OxPush2Request.class);
        final ProcessManager processManager = createProcessManager(oxPush2Request);
        if (preferences.getString("userChoose", "null").equalsIgnoreCase("deny")) {
            showToastWithText(context.getString(R.string.process_deny_start));
            processManager.onOxPushRequest(true);
            return;
        }
        if (preferences.getString("userChoose", "null").equalsIgnoreCase("approve")) {
            showToastWithText(context.getString(R.string.process_authentication_start));
            processManager.onOxPushRequest(false);
            return;
        }
    }

    private ProcessManager createProcessManager(OxPush2Request oxPush2Request){
        ProcessManager processManager = new ProcessManager();
        processManager.setOxPush2Request(oxPush2Request);
        processManager.setDataStore(dataStore);
        processManager.setActivity(getActivity());
        processManager.setOxPush2RequestListener(new OxPush2RequestListener() {
            @Override
            public void onQrRequest(OxPush2Request oxPush2Request) {
                //skip code there
            }

            @Override
            public TokenResponse onSign(String jsonRequest, String origin, Boolean isDeny) throws JSONException, IOException, U2FException {
                return u2f.sign(jsonRequest, origin, isDeny);
            }

            @Override
            public TokenResponse onEnroll(String jsonRequest, OxPush2Request oxPush2Request, Boolean isDeny) throws JSONException, IOException, U2FException {
                return u2f.enroll(jsonRequest, oxPush2Request, isDeny);
            }

            @Override
            public DataStore onGetDataStore() {
                return dataStore;
            }

            @Override
            public void onAdFreeButtonClick(){}

            @Override
            public void onPurchaseRestored() {}
        });

        return processManager;
    }

    private static boolean isConnected(final Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
