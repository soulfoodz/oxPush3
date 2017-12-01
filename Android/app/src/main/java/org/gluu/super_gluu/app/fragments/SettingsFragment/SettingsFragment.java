package org.gluu.super_gluu.app.fragments.SettingsFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.gluu.super_gluu.app.activities.GluuApplication;
import org.gluu.super_gluu.app.fingerprint.Fingerprint;
import org.gluu.super_gluu.app.gluuToast.GluuToast;
import org.gluu.super_gluu.app.settings.Settings;
import org.gluu.super_gluu.net.CommunicationService;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 5/17/17.
 */

public class SettingsFragment extends Fragment {

    private Context context;
    private LayoutInflater inflater;
    private Fingerprint fingerprint;
    private Switch switchSettings;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            boolean success = intent.getBooleanExtra("message", false);
            switchSettings.setChecked(success);
            String message = success ? "You've success authenticated by fingerprint" : "You've failed authenticated by fingerprint";
            showToast(message);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getContext();
        this.inflater = inflater;
        fingerprint = new Fingerprint(context);

        View actionBarView = (View) view.findViewById(R.id.actionBarView);
        actionBarView.findViewById(R.id.action_right_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.actionbar_icon).setVisibility(View.GONE);
        TextView title = (TextView) actionBarView.findViewById(R.id.actionbar_textview);
        title.setVisibility(View.VISIBLE);
        title.setText("MENU");
        LinearLayout leftButton = (LinearLayout) actionBarView.findViewById(R.id.action_left_button);
        leftButton.setVisibility(View.VISIBLE);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        final String settingsId = this.getArguments().getString("settingsId");

        TextView textSettings = (TextView)view.findViewById(R.id.textViewSettings);
        final TextView textSettingsSubTitle = (TextView)view.findViewById(R.id.textViewSubTitle);

        switchSettings = (Switch) view.findViewById(R.id.switch_setting);
        textSettings.setText(settingsId.equalsIgnoreCase("SSLConnectionSettings") ? getString(R.string.trust_all_certificate) : getString(R.string.fingerprint_title));
        textSettingsSubTitle.setText(settingsId.equalsIgnoreCase("SSLConnectionSettings") ? getString(R.string.warning_trust_all_certificate) : getString(R.string.pin_code_text));
        switchSettings.setChecked(Settings.getSettingsValueEnabled(context, settingsId));
//        textSettingsSubTitle.setVisibility(switchSettings.isChecked() ? View.VISIBLE : View.GONE);
        switchSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingsId.equalsIgnoreCase("SSLConnectionSettings")) {
                    GluuApplication.isTrustAllCertificates = switchSettings.isChecked();
//                    if (switchSettings.isChecked()) {
//                        String message = context.getString(R.string.warning_trust_all_certificate);
//                        showToastWithText(message);
//                    }
                } else {
                    if (switchSettings.isChecked() && fingerprint.startFingerprintService()) {
                        Log.v("TAG", "Fingerprint Settings enable: " + switchSettings.isChecked());
                    } else {
                        switchSettings.setChecked(false);
                        showToastWithText("Fingerprint is turned off for this device");
                    }
                }
//                textSettingsSubTitle.setVisibility(switchSettings.isChecked() ? View.VISIBLE : View.GONE);
                Settings.setSettingsValueEnabled(context, settingsId, switchSettings.isChecked());
                // Init network layer
                CommunicationService.init();
            }
        });

        view.findViewById(R.id.button_ad_free).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("on-ad-free-flow");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        //Setup message receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("fingerprint_authentication_result"));

        //Setup fonts
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
        textSettings.setTypeface(face);
        textSettingsSubTitle.setTypeface(face);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private void showToastWithText(String text){
        GluuToast gluuToast = new GluuToast(context);
        View view = inflater.inflate(R.layout.gluu_toast, null);
        gluuToast.showGluuToastWithText(view, text);
    }

    private void showWarning(int statusId) {
        String message = context.getString(statusId);
        Intent intent = new Intent("ox_request-precess-event");
        // You can also include some extra data.
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void showToast(String text){
        GluuToast gluuToast = new GluuToast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gluu_toast, null);
        gluuToast.showGluuToastWithText(view, text);
    }
}
