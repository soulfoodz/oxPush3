package org.gluu.super_gluu.app.fragments.PinCodeFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.gluu.super_gluu.app.fragments.LicenseFragment.LicenseFragment;
import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 3/24/16.
 */
public class PinCodeSettingFragment extends Fragment {

    LicenseFragment.OnMainActivityListener mainActivityListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pin_code_setting, container, false);

        if (!isPin()){
            mainActivityListener.onShowPinFragment();
        }

        TextView textSettingsTitle = (TextView)view.findViewById(R.id.pinCodeTitle);
        TextView textSettingsSubTitle = (TextView)view.findViewById(R.id.pinSubCodeTitle);
        Button yesButton = (Button)view.findViewById(R.id.yes_button_pin);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityListener.onShowPinFragment();
                setPincodeEnabled(true);
            }
        });
        Button noButton = (Button)view.findViewById(R.id.no_button_pin);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityListener.onMainActivity();
                setPincodeEnabled(false);
            }
        });

        Typeface faceLight = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
        textSettingsTitle.setTypeface(faceLight);
        textSettingsSubTitle.setTypeface(faceLight);
        yesButton.setTypeface(faceLight);
        noButton.setTypeface(faceLight);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LicenseFragment.OnMainActivityListener) {
            mainActivityListener = (LicenseFragment.OnMainActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainActivityListener");
        }
    }

    public void setPincodeEnabled(Boolean isEnabled){
        SharedPreferences preferences = getContext().getSharedPreferences("PinCodeSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isPinEnabled", isEnabled);
        editor.commit();
    }

    public Boolean isPin(){
        SharedPreferences preferences = getContext().getSharedPreferences("PinCodeSettings", Context.MODE_PRIVATE);
        String pinCode = preferences.getString("PinCode", "null");
        return pinCode.equalsIgnoreCase("null");
    }
}
