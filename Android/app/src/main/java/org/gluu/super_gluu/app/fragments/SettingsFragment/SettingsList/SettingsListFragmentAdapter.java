package org.gluu.super_gluu.app.fragments.SettingsFragment.SettingsList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.gluu.super_gluu.app.FragmentType;
import org.gluu.super_gluu.app.fragments.LicenseFragment.LicenseFragment;
import org.gluu.super_gluu.app.fragments.Purchase.PurchaseFragment;
import org.gluu.super_gluu.app.fragments.SettingsFragment.SettingsFragment;
import org.gluu.super_gluu.app.fragments.SettingsFragment.SettingsPinCode;
import org.gluu.super_gluu.app.purchase.InAppPurchaseService;
import org.gluu.super_gluu.app.settings.Settings;

import java.util.List;
import java.util.Map;

import SuperGluu.app.BuildConfig;
import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 5/17/17.
 */

public class SettingsListFragmentAdapter extends BaseAdapter {

    private List<Map<String, Integer>> list;
    private LayoutInflater mInflater;
    private Context context;
    private Activity activity;
    private SettingsListFragment.SettingsListListener mListener;
    private Typeface face;
    //For purchases
    private InAppPurchaseService inAppPurchaseService = new InAppPurchaseService();
    private Boolean isFingerprintAvailable = Build.VERSION.SDK_INT > 22;
    private int indexAdFree = isFingerprintAvailable ? 6 : 5;

    public SettingsListFragmentAdapter(Activity activity, List<Map<String, Integer>> listContact, SettingsListFragment.SettingsListListener settingsListListener) {
        list = listContact;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        mInflater = LayoutInflater.from(activity);
        mListener = settingsListListener;
        face = Typeface.createFromAsset(activity.getAssets(), "ProximaNova-Regular.otf");
        initIAPurchaseService();
    }

    private void initIAPurchaseService(){
        inAppPurchaseService.initInAppService(context);
        inAppPurchaseService.setCustomEventListener(new InAppPurchaseService.OnInAppServiceListener() {
            @Override
            public void onSubscribed(Boolean isSubscribed) {
                if (isSubscribed){
                    list.remove(indexAdFree);
                    notifyDataSetChanged();
                }
            }
        });
    }

    SettingsFragment createSettingsFragment(String settingsId){
        SettingsFragment sslFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("settingsId", settingsId);
        sslFragment.setArguments(bundle);
        return sslFragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = mInflater;
            view = inflater.inflate(R.layout.fragment_setting_list, null);
        }
        Map<String, Integer> item = list.get(position);
        Map.Entry<String, Integer> entry = item.entrySet().iterator().next();
        view.setTag(entry);
        TextView settingName = (TextView) view.findViewById(R.id.settings_name);

        if (settingName != null) {
            settingName.setText(entry.getKey());
            settingName.setTypeface(face);
        }
        ImageView settingArrow = (ImageView) view.findViewById(R.id.settingArrow);
        TextView info = (TextView) view.findViewById(R.id.textInfo);
        settingArrow.setVisibility(View.VISIBLE);
        settingArrow.setVisibility(View.GONE);
        if (entry.getValue() == FragmentType.SETTINGS_FRAGMENT_TYPE.VERSION_FRAGMENT.ordinal()) {
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            info.setText(versionName + " - " + String.valueOf(versionCode));
            view.setBackgroundColor(Color.WHITE);
            info.setVisibility(View.VISIBLE);
            info.setTypeface(face);
        } else if (entry.getValue() != FragmentType.SETTINGS_FRAGMENT_TYPE.EMPTY_FRAGMENT.ordinal()) {
            info.setVisibility(View.GONE);
            settingArrow.setVisibility(View.VISIBLE);
            view.setBackgroundColor(Color.WHITE);
        } else {
            view.setBackgroundColor(Color.parseColor("#efeff4"));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) v.getTag();
                if (mListener != null && entry.getValue() == FragmentType.SETTINGS_FRAGMENT_TYPE.PIN_CODE_FRAGMENT.ordinal()) {
                    mListener.onSettingsList(new SettingsPinCode());
                } else if (mListener != null && entry.getValue() == FragmentType.SETTINGS_FRAGMENT_TYPE.FINGERPRINT_FRAGMENT.ordinal()) {
                    mListener.onSettingsList(createSettingsFragment("FingerprintSettings"));
                } else if (mListener != null && entry.getValue() == FragmentType.SETTINGS_FRAGMENT_TYPE.SSL_FRAGMENT.ordinal()) {
                    mListener.onSettingsList(createSettingsFragment("SSLConnectionSettings"));
                } else if (entry.getValue() == FragmentType.SETTINGS_FRAGMENT_TYPE.USER_GUIDE_FRAGMENT.ordinal()){
                    Uri uri = Uri.parse("https://gluu.org/docs/supergluu/3.0.0/user-guide/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                } else if (entry.getValue() == FragmentType.SETTINGS_FRAGMENT_TYPE.PRIVACY_POLICY_FRAGMENT.ordinal()) {
                    LicenseFragment licenseFragment = new LicenseFragment();
                    licenseFragment.setForFirstLoading(false);
                    mListener.onSettingsList(licenseFragment);
                } else if (entry.getValue() == FragmentType.SETTINGS_FRAGMENT_TYPE.AD_FREE_FRAGMENT.ordinal()){
                    PurchaseFragment purchaseFragment = new PurchaseFragment();
                    mListener.onSettingsList(purchaseFragment);
                }
            }
        });

        return view;
    }
}
