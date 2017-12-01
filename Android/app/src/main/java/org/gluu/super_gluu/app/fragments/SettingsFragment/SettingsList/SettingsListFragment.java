package org.gluu.super_gluu.app.fragments.SettingsFragment.SettingsList;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.gluu.super_gluu.app.FragmentType;
import org.gluu.super_gluu.app.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 3/23/16.
 */
public class SettingsListFragment extends Fragment {

    private Context context;
    private LayoutInflater inflater;

    private SettingsListFragmentAdapter listAdapter;
    private SettingsListListener mListener;
    private List<Map<String, Integer>> listSettings = new ArrayList<>();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getContext();
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.settings_list, container, false);

        View actionBarView = (View) rootView.findViewById(R.id.actionBarSettings);
        actionBarView.findViewById(R.id.action_left_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.action_right_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.actionbar_icon).setVisibility(View.GONE);
        TextView title = (TextView) actionBarView.findViewById(R.id.actionbar_textview);
        title.setVisibility(View.VISIBLE);
        title.setText("MENU");

        listSettings = new ArrayList<>();
        Map<String, Integer> item = new HashMap<>();
        item.put("Pin code", FragmentType.SETTINGS_FRAGMENT_TYPE.PIN_CODE_FRAGMENT.ordinal());
        listSettings.add(item);
        //Check if device api version supports fingerprint functionality
        int version_api = Build.VERSION.SDK_INT;
        if (version_api > 22){
            Map<String, Integer> item2 = new HashMap<>();
            item2.put("Fingerprint", FragmentType.SETTINGS_FRAGMENT_TYPE.FINGERPRINT_FRAGMENT.ordinal());
            listSettings.add(item2);
        }
//        listSettings.add("U2F BLE device(s)");
        Map<String, Integer> item3 = new HashMap<>();
        item3.put("Trust all (SSL)", FragmentType.SETTINGS_FRAGMENT_TYPE.SSL_FRAGMENT.ordinal());
        listSettings.add(item3);
        Map<String, Integer> item4 = new HashMap<>();
        item4.put("", FragmentType.SETTINGS_FRAGMENT_TYPE.EMPTY_FRAGMENT.ordinal());
        listSettings.add(item4);
        Map<String, Integer> item5 = new HashMap<>();
        item5.put("User guide", FragmentType.SETTINGS_FRAGMENT_TYPE.USER_GUIDE_FRAGMENT.ordinal());
        listSettings.add(item5);
        Map<String, Integer> item6 = new HashMap<>();
        item6.put("Privacy policy", FragmentType.SETTINGS_FRAGMENT_TYPE.PRIVACY_POLICY_FRAGMENT.ordinal());
        listSettings.add(item6);
        Boolean isAdFree = Settings.getPurchase(context);
        if (!isAdFree){
            Map<String, Integer> item7 = new HashMap<>();
            item7.put("Upgrade to Ad-Free", FragmentType.SETTINGS_FRAGMENT_TYPE.AD_FREE_FRAGMENT.ordinal());
            listSettings.add(item7);
        }
        Map<String, Integer> item8 = new HashMap<>();
        item8.put("", FragmentType.SETTINGS_FRAGMENT_TYPE.EMPTY_FRAGMENT.ordinal());
        listSettings.add(item8);
        Map<String, Integer> item9 = new HashMap<>();
        item9.put("Version", FragmentType.SETTINGS_FRAGMENT_TYPE.VERSION_FRAGMENT.ordinal());
        listSettings.add(item9);

        mListener = new SettingsListListener() {
            @Override
            public void onSettingsList(Fragment settingsFragment) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Settings.setIsSettingsMenuVisible(context, true);
                transaction.replace(R.id.root_frame, settingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                getActivity().invalidateOptionsMenu();
            }
        };

        listAdapter = new SettingsListFragmentAdapter(getActivity(), listSettings, mListener);
        ListView lv = (ListView) rootView.findViewById(R.id.settingsListView);
        lv.setAdapter(listAdapter);

        TextView info = (TextView) rootView.findViewById(R.id.textView3);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
        info.setTypeface(face);

        return rootView;
    }

    public interface SettingsListListener {
        void onSettingsList(Fragment settingsFragment);
    }

}