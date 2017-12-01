package org.gluu.super_gluu.app.customGluuAlertView;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gluu.super_gluu.app.GluuMainActivity;
import org.gluu.super_gluu.app.NotificationType;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 4/15/16.
 */
public class CustomGluuAlert extends Dialog implements android.view.View.OnClickListener {

    private String sub_title, message, yesTitle, noTitle;
    private Activity activity;
    private Button yes, no;
    private GluuMainActivity.GluuAlertCallback mListener;
    private Boolean isTextView = false;
    private String text;

    public NotificationType type;

    public CustomGluuAlert(Activity a) {
        super(a);
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_gluu_alert);
        TextView sub_title = (TextView) findViewById(R.id.alert_message_subText);
        TextView message = (TextView) findViewById(R.id.alert_message_textView);
        final EditText textField = (EditText) findViewById(R.id.alert_textField);
        Typeface faceLight = Typeface.createFromAsset(activity.getAssets(), "ProximaNova-Regular.otf");
        message.setTypeface(faceLight);
        sub_title.setTypeface(faceLight);
        if (this.sub_title != null && !this.sub_title.isEmpty()){
            sub_title.setText(this.sub_title);
        }
        if (this.message != null && !this.message.isEmpty()){
            message.setText(this.message);
            if (sub_title.getText().length() > 0){
                message.setTextColor(Color.parseColor("#1ab26b"));
                if (type == NotificationType.RENAME_KEY || type == NotificationType.DEFAULT){
                    Typeface face = Typeface.createFromAsset(activity.getAssets(), "ProximaNova-Semibold.otf");
                    message.setTypeface(face);
                    message.setTextSize(24);
                }
            } else {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) sub_title.getLayoutParams();
                params.topMargin = 0;
                params.bottomMargin = 0;
                sub_title.requestLayout();
            }
        }
        yes = (Button) findViewById(R.id.yes_button);
        if (this.yesTitle != null && !this.yesTitle.isEmpty()){
            yes.setText(this.yesTitle);
        } else {
            yes.setVisibility(View.GONE);
        }
        no = (Button) findViewById(R.id.no_button);
        if (this.noTitle != null && !this.noTitle.isEmpty()){
            no.setText(this.noTitle);
        } else {
            no.setVisibility(View.GONE);
        }
        if (yes.getVisibility() == View.GONE && no.getVisibility() == View.GONE){
            yes.setVisibility(View.VISIBLE);
            yes.setText("OK");
        }
        if (isTextView){
            textField.setVisibility(View.VISIBLE);
            textField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            textField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text = String.valueOf(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            textField.setVisibility(View.GONE);
            LinearLayout alert_buttons_view = (LinearLayout) findViewById(R.id.alert_buttons_view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) alert_buttons_view.getLayoutParams();
            params.topMargin = 0;
            alert_buttons_view.requestLayout();
        }
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        yes.setTypeface(faceLight);
        no.setTypeface(faceLight);

        //Setup title icons
        ImageView actionbar_icon = (ImageView) findViewById(R.id.actionbar_icon);
        if (type == NotificationType.RENAME_KEY){
            actionbar_icon.setImageResource(R.drawable.edit_key_icon);
        } else if (type == NotificationType.DEFAULT){
            actionbar_icon.setImageResource(R.drawable.default_alert_icon);
        }

        super.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes_button:
                if (mListener != null){
                     mListener.onPositiveButton();
                }
                dismiss();
                break;

            case R.id.no_button:
                if (mListener != null){
                    mListener.onNegativeButton();
                }
                dismiss();
                break;

        default:
            break;
        }
        dismiss();
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getYesTitle() {
        return yesTitle;
    }

    public void setYesTitle(String yesTitle) {
        this.yesTitle = yesTitle;
    }

    public String getNoTitle() {
        return noTitle;
    }

    public void setNoTitle(String noTitle) {
        this.noTitle = noTitle;
    }

    public Object getmListener() {
        return mListener;
    }

    public void setmListener(GluuMainActivity.GluuAlertCallback mListener) {
        this.mListener = mListener;
    }

    public Boolean getIsTextView() {
        return isTextView;
    }

    public void setIsTextView(Boolean isTextView) {
        this.isTextView = isTextView;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
