package org.gluu.super_gluu.app.fragments.PinCodeFragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

//import com.github.simonpercic.rxtime.RxTime;
import com.mhk.android.passcodeview.PasscodeView;

import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.services.GlobalNetworkTime;
import org.gluu.super_gluu.app.settings.Settings;

import SuperGluu.app.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by nazaryavornytskyy on 3/24/16.
 */
public class PinCodeFragment extends Fragment implements View.OnClickListener {

    private View view;
    private boolean isSettings;
    private boolean newPin;
    private boolean isWrongPin;
    private int setNewPinAttempts;
    public PinCodeViewListener pinCodeViewListener;
    private boolean isSetNewPinCode;
    private TextView pinCodeTitle;
    private TextView attemptsLabel;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pin_code, container, false);
        context = getContext();
        view.findViewById(R.id.close_button).setVisibility(View.GONE);
        Button closeButton = (Button) view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(this);
        pinCodeTitle = (TextView) view.findViewById(R.id.pin_code_title);
        attemptsLabel = (TextView) view.findViewById(R.id.attemptsLabel);
        updatePinCodeView();
        setNewPinAttempts = 0;
        return view;
    }

    private void updatePinCodeView(){
        /**
         * find view and add the completion listener
         * */
        final PasscodeView pcView = (PasscodeView) view.findViewById(R.id.pin_view);

        pcView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pcView.requestToShowKeyboard();
            }
        }, 400);
        int attempts = Settings.getCurrentPinCodeAttempts(getContext());
        attemptsLabel.setText("You have " + attempts + " attempts left");
        if (attempts <= 2) {
            attemptsLabel.setTextColor(getResources().getColor(R.color.redColor));
        }
        final String pinCode = Settings.getPinCode(getContext());
        pcView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                if (pinCode.equalsIgnoreCase("null")) {
                    if (setNewPinAttempts == 0){
                        setNewPinAttempts++;
                        pinCodeTitle.setText("Re-enter your pin code.");
                        pcView.clearText();
                        return;
                    } else {
                        attemptsLabel.setVisibility(View.VISIBLE);
                        attemptsLabel.setText("Set new pin success!");
                        setNewPin(passcode);
                    }
                } else if (newPin) {
                    if (passcode.equalsIgnoreCase(pinCode)) {
                        showAlertView("New pin code is the same as old, choose new one.");
                        getActivity().onBackPressed();
                    } else {
                        showAlertView("New Pin changed success!");
                        Settings.savePinCode(getContext(), passcode);
                        getActivity().onBackPressed();
                    }
                    newPin = false;
                } else if (passcode.equalsIgnoreCase(Settings.getPinCode(getContext()))) {
                    if (isSetNewPinCode) {
                        attemptsLabel.setVisibility(View.GONE);
                        pinCodeTitle.setText(R.string.set_new_pin_code);
                        pcView.clearText();
                        newPin = true;
                        resetCurrentPinAttempts();
                        return;
                    } else {
                        attemptsLabel.setTextColor(getResources().getColor(R.color.greenColor));
                        attemptsLabel.setText("Pin code is correct!");
                    }
                    if (pinCodeViewListener != null) {
                        pinCodeViewListener.onCorrectPinCode(true);
                    }
                    isWrongPin = false;
                    resetCurrentPinAttempts();
                } else {
                    wrongPinCode(pcView);
                }
            }
        });
        if (pinCode.equalsIgnoreCase("null")){
            attemptsLabel.setVisibility(View.GONE);
            pinCodeTitle.setText(R.string.set_new_pin_code);
        } else {
//            /**
//             * set PinCodeMode to SET_NEW_PINCODE in order to create the new pin code,
//             * your responsibility is how you handle the new pin code after entering it
//             * */
//            pinCodeView.setPinViewInfoMessageVisable(false);
//            pinCodeView.setPinCodeMode(PinCodeView.PinCodeMode.SET_NEW_PINCODE);
//            if (getIsReset()) {
//                attemptsLabel.setVisibility(View.VISIBLE);
//                if (isSettings) {
//                    view.findViewById(R.id.close_button).setVisibility(View.VISIBLE);
//                } else {
//                    view.findViewById(R.id.close_button).setVisibility(View.GONE);
//                }
//            }
            if (!newPin) {
                pinCodeTitle.setText(R.string.enter_pin_code);
            }
        }

    }

    private void setNewPin(String passcode){
        isWrongPin = false;
        Settings.savePinCode(getContext(), passcode);
//        if (!isSettings) {
            pinCodeViewListener.onCorrectPinCode(true);
//        }
        resetCurrentPinAttempts();
        //Hide keyboard
        ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        getActivity().onBackPressed();
    }

    private void wrongPinCode(PasscodeView pcView){
        isWrongPin = true;
        increaseAttempts();
        int attempts = Settings.getCurrentPinCodeAttempts(getContext());
        String attemptsText = "You have " + attempts + " attempts left";
        if (attempts <= 2) {
            attemptsLabel.setTextColor(getResources().getColor(R.color.redColor));
        }
        attemptsLabel.setText(attemptsText);
        pcView.clearText();
        if (attempts <= 0) {
            resetCurrentPinAttempts();
            if (isSettings) {
                Settings.setAppLocked(getContext(), true);
                setCurrentNetworkTime();
            }
            pinCodeViewListener.onCorrectPinCode(false);
        }
    }

    private void setCurrentNetworkTime() {
        // a singleton
//        RxTime rxTime = new RxTime();
//        rxTime.currentTime()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Long>() {
//                    @Override
//                    public void call(Long time) {
//                        // use time
//                        Settings.setAppLockedTime(getContext(), String.valueOf(time));
//                    }
//                });
        new GlobalNetworkTime().getCurrentNetworkTime(context, new GlobalNetworkTime.GetGlobalTimeCallback() {
            @Override
            public void onGlobalTime(Long time) {
                Settings.setAppLockedTime(getContext(), String.valueOf(time));
            }
        });
    }

    private void showAlertView(String message){
        CustomGluuAlert gluuAlert = new CustomGluuAlert(getActivity());
        gluuAlert.setMessage(message);
        gluuAlert.setYesTitle(getActivity().getApplicationContext().getString(R.string.ok));
        gluuAlert.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PinCodeViewListener) {
            pinCodeViewListener = (PinCodeViewListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PinCodeViewListener");
        }
    }

    public void resetCurrentPinAttempts(){
        Settings.saveIsReset(getContext());
        SharedPreferences preferences = context.getSharedPreferences("PinCodeSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentPinCodeAttempts", String.valueOf(Settings.getPinCodeAttempts(getContext())));
        editor.commit();
    }

    public void increaseAttempts(){
        int attempts = Settings.getCurrentPinCodeAttempts(getContext());
        attempts--;
        Settings.setCurrentPinCodeAttempts(getContext(), attempts);
    }

    @Override
    public void onClick(View v) {
        Settings.saveIsReset(getContext());
        getActivity().onBackPressed();
    }

    public void setIsSettings(Boolean isSettings) {
        this.isSettings = isSettings;
    }

    public void setIsSetNewPinCode(Boolean isSetNewPinCode) {
        this.isSetNewPinCode = isSetNewPinCode;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface PinCodeViewListener{
        public void onNewPinCode(String pinCode);
        public void onCorrectPinCode(boolean isPinCodeCorrect);
    }
}
