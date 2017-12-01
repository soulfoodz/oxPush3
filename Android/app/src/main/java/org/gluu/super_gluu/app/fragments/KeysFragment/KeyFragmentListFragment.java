package org.gluu.super_gluu.app.fragments.KeysFragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;

import SuperGluu.app.R;
import org.gluu.super_gluu.store.AndroidKeyDataStore;
import org.gluu.super_gluu.u2f.v2.model.TokenEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by nazaryavornytskyy on 3/1/16.
 */
public class KeyFragmentListFragment extends Fragment {

    private KeyFragmentListAdapter listAdapter;
    private KeyHandleInfo mListener;
    private KeyHandleChangeName cListener;
    private AndroidKeyDataStore dataStore;
    private List<TokenEntry> listToken;
    private RelativeLayout keyMainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_key_list, container, false);
        View actionBarView = (View) rootView.findViewById(R.id.actionBarSettings);
        actionBarView.findViewById(R.id.action_left_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.action_right_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.actionbar_icon).setVisibility(View.VISIBLE);
        actionBarView.findViewById(R.id.actionbar_textview).setVisibility(View.GONE);

        listToken = getListToken(rootView);
        keyMainView = (RelativeLayout) rootView.findViewById(R.id.keyMainView);
        keyMainView.setVisibility(listToken.size() > 0 ? View.VISIBLE : View.GONE);
        ListView lv = (ListView) rootView.findViewById(R.id.keyHandleListView);
        mListener = new KeyHandleInfo() {
            @Override
            public void onKeyHandleInfo(KeyHandleInfoFragment infoFragment) {
                getActivity().invalidateOptionsMenu();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.keys_root_frame, infoFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onUpdateList(Boolean isEmtryList) {
                keyMainView.setVisibility(isEmtryList ? View.GONE : View.VISIBLE);
            }
        };

        cListener = new KeyHandleChangeName() {
            @Override
            public void onKeyNameChanged(String keyID) {
                CustomGluuAlert gluuAlert = new CustomGluuAlert(getActivity());
                gluuAlert.setMessage(getActivity().getApplicationContext().getString(R.string.enter_new_key_name));
                gluuAlert.setYesTitle(getActivity().getApplicationContext().getString(R.string.yes));
                gluuAlert.setNoTitle(getActivity().getApplicationContext().getString(R.string.no));
//                gluuAlert.setmListener(listener);
                gluuAlert.show();
            }
        };
        listAdapter = new KeyFragmentListAdapter(getActivity(), listToken, mListener);
        lv.setAdapter(listAdapter);
        lv.setEmptyView(rootView.findViewById(R.id.empty_keyList));

        TextView availableKeys = (TextView) rootView.findViewById(R.id.availableKeys_textView);
        TextView keys_textView = (TextView) rootView.findViewById(R.id.keys_textView);
//        TextView rename_textView = (TextView) rootView.findViewById(R.id.rename_textView);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Semibold.otf");
        Typeface faceLight = Typeface.createFromAsset(getActivity().getAssets(), "ProximaNova-Regular.otf");
        availableKeys.setTypeface(face);
        keys_textView.setTypeface(faceLight);
//        rename_textView.setTypeface(faceLight);
//        availableKeys.setText(rootView.getContext().getString(R.string.available_keys, String.valueOf(listAdapter.getCount())));
//        renameText = (TextView) rootView.findViewById(R.id.rename_textView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkTokenList();
    }

    void checkTokenList(){
//        if (listToken.size() == 0) {
//            renameText.setVisibility(View.GONE);
//        }
    }

    List<TokenEntry> getListToken(View view){
        Context context = view.getContext();
        dataStore = new AndroidKeyDataStore(context);
        List<String> tokensString = dataStore.getTokenEntries();
        List<TokenEntry> tokens = new ArrayList<TokenEntry>();
        for (String tokenString : tokensString){
            TokenEntry token = new Gson().fromJson(tokenString, TokenEntry.class);
            tokens.add(token);
        }
        //Sort keys by created date
        List<TokenEntry> tokensFromDB = new ArrayList<TokenEntry>(tokens);
        Collections.sort(tokensFromDB, new Comparator<TokenEntry>(){
            public int compare(TokenEntry key1, TokenEntry key2) {
                SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                try {
                    Date date1 = isoDateTimeFormat.parse(key1.getCreatedDate());
                    Date date2 = isoDateTimeFormat.parse(key2.getCreatedDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        Collections.reverse(tokensFromDB);

        return tokensFromDB;
    }

    public interface KeyHandleInfo {
        void onKeyHandleInfo(KeyHandleInfoFragment infoFragment);
        void onUpdateList(Boolean isEmtryList);
    }

    public interface KeyHandleChangeName {
        void onKeyNameChanged(String keyID);
    }

}
