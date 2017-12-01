package org.gluu.super_gluu.app.fragments.KeysFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.gluu.super_gluu.app.GluuMainActivity;
import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.u2f.v2.model.TokenEntry;
import org.gluu.super_gluu.util.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 3/1/16.
 */
public class KeyHandleInfoFragment extends Fragment implements View.OnClickListener{

    final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    final SimpleDateFormat userDateTimeFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");

    private static final String ARG_PARAM1 = "tokenEntry";
    private TokenEntry tokenEntry;
    private OnDeleteKeyHandleListener mDeleteListener;

    private Activity mActivity;

    private BroadcastReceiver mDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
//            Boolean isAdFree = intent.getBooleanExtra("isAdFree", false);
            showAlertView();
        }
    };

    public static KeyHandleInfoFragment newInstance(String tokenEntity) {
        KeyHandleInfoFragment fragment = new KeyHandleInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tokenEntity);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String tokenString = getArguments().getString(ARG_PARAM1);
            tokenEntry = new Gson().fromJson(tokenString, TokenEntry.class);
        }
        //Setup message receiver\
        if (mActivity == null){
            mActivity = getActivity();
        }
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mDeleteReceiver,
                new IntentFilter("on-delete-key-event"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_keyhandle_info, container, false);
        updateKeyHandleDetails(rootView);

        View actionBarView = (View) rootView.findViewById(R.id.actionBarView);
        actionBarView.findViewById(R.id.actionbar_icon).setVisibility(View.GONE);
        TextView title = (TextView) actionBarView.findViewById(R.id.actionbar_textview);
        title.setVisibility(View.VISIBLE);
        title.setText("KEY INFORMATION");
        LinearLayout leftButton = (LinearLayout) actionBarView.findViewById(R.id.action_left_button);
        leftButton.setVisibility(View.VISIBLE);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        Button rightButton = (Button) actionBarView.findViewById(R.id.action_right_button);
        rightButton.setVisibility(View.VISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertView();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeleteKeyHandleListener) {
            mDeleteListener = (OnDeleteKeyHandleListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeleteKeyHandleListener");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mDeleteReceiver);
    }

    private void updateKeyHandleDetails(View view) {
        TextView keyHandle_Title = ((TextView) view.findViewById(R.id.textView5));
        TextView keyHandle_user_nameTitle = ((TextView) view.findViewById(R.id.keyHandle_user_name_label));
        TextView keyHandle_user_name = ((TextView) view.findViewById(R.id.keyHandle_user_name_label_value));
        keyHandle_user_name.setText(tokenEntry.getUserName());
        TextView keyHandle_created = (TextView) view.findViewById(R.id.keyHandle_created_value);
        TextView keyHandle_createdTitle = (TextView) view.findViewById(R.id.keyHandle_created);
        setupPairingDateByFormat(keyHandle_created);
        TextView keyHandle_issuer = ((TextView) view.findViewById(R.id.keyHandle_issuer_value));
        TextView keyHandle_issuerTitle = ((TextView) view.findViewById(R.id.keyHandle_issuer_label));
        try {
            URI uri = new URI(tokenEntry.getIssuer());
            String path = uri.getHost();
            keyHandle_issuer.setText(path);
        } catch (URISyntaxException e) {
            keyHandle_issuer.setText(tokenEntry.getIssuer());
            e.printStackTrace();
        }

        String keyStr = Utils.encodeHexString(tokenEntry.getKeyHandle());
        String keyHandleString = keyStr.substring(0, 6) + "..." + keyStr.substring(keyStr.length()-6);
        TextView keyHandle_id = ((TextView) view.findViewById(R.id.keyHandle_id));
        TextView keyHandle_idTitle = ((TextView) view.findViewById(R.id.keyHandle_label));
        keyHandle_id.setText(keyHandleString);
        //Setup fonts
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Semibold.otf");
        Typeface faceLight = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
        keyHandle_Title.setTypeface(face);
        keyHandle_user_name.setTypeface(faceLight);
        keyHandle_user_nameTitle.setTypeface(faceLight);
        keyHandle_created.setTypeface(faceLight);
        keyHandle_createdTitle.setTypeface(faceLight);
        keyHandle_issuer.setTypeface(faceLight);
        keyHandle_issuerTitle.setTypeface(faceLight);
        keyHandle_id.setTypeface(faceLight);
        keyHandle_idTitle.setTypeface(faceLight);
    }

    @Override
    public void onClick(View v) {
//        if(v.getId() == R.id.delete_button){
//            showAlertView();
//        } else {
//            getFragmentManager().popBackStack();
//        }
    }

    void showAlertView(){
        GluuMainActivity.GluuAlertCallback listener = new GluuMainActivity.GluuAlertCallback(){
            @Override
            public void onPositiveButton() {
                mDeleteListener.onDeleteKeyHandle(tokenEntry);
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }

            @Override
            public void onNegativeButton() {
                //Skip here
            }
        };
        CustomGluuAlert gluuAlert = new CustomGluuAlert(mActivity);
        gluuAlert.setMessage(mActivity.getApplicationContext().getString(R.string.approve_delete));
        gluuAlert.setSub_title(mActivity.getApplicationContext().getString(R.string.delete_key_sub_title));
        gluuAlert.setYesTitle(mActivity.getApplicationContext().getString(R.string.yes));
        gluuAlert.setNoTitle(mActivity.getApplicationContext().getString(R.string.no));
        gluuAlert.setmListener(listener);
        gluuAlert.show();
    }

    void setupPairingDateByFormat(TextView textView){

        if (textView != null && tokenEntry.getCreatedDate() != null) {
            Date date = null;
            try {
                date = isoDateTimeFormat.parse(tokenEntry.getCreatedDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                String pairingDate = userDateTimeFormat.format(date);
                textView.setText(pairingDate);
            }
        } else {
            textView.setText(R.string.no_date);
        }
    }

    public interface OnDeleteKeyHandleListener {
        void onDeleteKeyHandle(TokenEntry key);
    }
}
