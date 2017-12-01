package org.gluu.super_gluu.app.fragments.KeysFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.Gson;

import org.gluu.super_gluu.app.ApproveDenyFragment;
import org.gluu.super_gluu.app.NotificationType;
import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.GluuMainActivity;

import SuperGluu.app.R;

import org.gluu.super_gluu.app.fragments.LogsFragment.LogsFragmentListAdapter;
import org.gluu.super_gluu.app.model.LogInfo;
import org.gluu.super_gluu.app.settings.Settings;
import org.gluu.super_gluu.model.OxPush2Request;
import org.gluu.super_gluu.store.AndroidKeyDataStore;
import org.gluu.super_gluu.u2f.v2.model.TokenEntry;
import org.gluu.super_gluu.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nazaryavornytskyy on 3/1/16.
 */
public class KeyFragmentListAdapter extends BaseAdapter {

    final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    final SimpleDateFormat userDateTimeFormat = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");

    private List<TokenEntry> list;
    private LayoutInflater mInflater;
    private Activity activity;
    private KeyFragmentListFragment.KeyHandleInfo mListener;
    private final ViewBinderHelper binderHelper;

    public KeyFragmentListAdapter(Activity activity, List<TokenEntry> listContact, KeyFragmentListFragment.KeyHandleInfo keyHandleInfo) {
        list = listContact;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        mListener = keyHandleInfo;
        binderHelper = new ViewBinderHelper();
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
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = mInflater;
            view = inflater.inflate(R.layout.fragment_key, null);
            holder = new ViewHolder();
//            holder.textView = (TextView) convertView.findViewById(R.id.text);
            View swipeView = view.findViewById(R.id.swipe_menu_layout);
            swipeView.setTag(position);
            holder.deleteButton = (RelativeLayout) swipeView.findViewById(R.id.swipe_delete_button);
            holder.showButton = (RelativeLayout) swipeView.findViewById(R.id.swipe_show_button);
            holder.renameButton = (RelativeLayout) swipeView.findViewById(R.id.swipe_rename_button);
            holder.swipeLayout = (SwipeRevealLayout) view.findViewById(R.id.swipe_layout);
            holder.deleteButton.setTag(position);
            holder.showButton.setTag(position);
            holder.renameButton.setTag(position);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
//        view.setTag(position);
        final TokenEntry token = (TokenEntry) list.get(position);
        if (token != null) {
            TextView createdDate = (TextView) view.findViewById(R.id.created_dateKey);
            TextView contentView = (TextView) view.findViewById(R.id.contentKey);

            if (contentView != null) {
                contentView.setText(token.getKeyName());
            }
            String date = token.getCreatedDate();
            if (createdDate != null && date != null) {
                Date createDate = null;
                if (Utils.isNotEmpty(date)) {
                    try {
                        createDate = isoDateTimeFormat.parse(date);
                    } catch (ParseException ex) {
                        Log.e(this.getClass().getName(), "Failed to parse ISO date/time: " + date, ex);
                    }
                }

                String createdString = "";
                if (createdDate != null) {
                    createdString = userDateTimeFormat.format(createDate);
                }
                createdDate.setText(createdString);
            } else {
                createdDate.setText(R.string.no_date);
            }
            //Setup fonts
            Typeface face = Typeface.createFromAsset(activity.getAssets(), "ProximaNova-Regular.otf");
            Typeface faceTitle = Typeface.createFromAsset(activity.getAssets(), "ProximaNova-Semibold.otf");
            contentView.setTypeface(faceTitle);
            createdDate.setTypeface(face);
        }
        LinearLayout main_layout = (LinearLayout)view.findViewById(R.id.key_main_view);
        main_layout.setTag(position);
        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                String tokenString = new Gson().toJson(list.get(position));
                KeyHandleInfoFragment infoFragment = new KeyHandleInfoFragment().newInstance(tokenString);
                if (mListener != null) {
//                    Settings.setIsBackButtonVisible(activity.getApplicationContext(), true);
                    Settings.setIsBackButtonVisibleForKey(activity.getApplicationContext(), true);
                    mListener.onKeyHandleInfo(infoFragment);
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = (int) v.getTag();
                String token = new Gson().toJson(list.get(position));
                final TokenEntry tokenEntry = new Gson().fromJson(token, TokenEntry.class);
                CustomGluuAlert gluuAlert = new CustomGluuAlert(activity);
//                gluuAlert.setMessage(activity.getApplicationContext().getString(R.string.rename_key_name));
                gluuAlert.setSub_title(activity.getApplicationContext().getString(R.string.rename_key_name));
                gluuAlert.setYesTitle(activity.getApplicationContext().getString(R.string.yes));
                gluuAlert.setNoTitle(activity.getApplicationContext().getString(R.string.no));
                gluuAlert.type = NotificationType.RENAME_KEY;
                gluuAlert.setmListener(new GluuMainActivity.GluuAlertCallback() {
                    @Override
                    public void onPositiveButton() {
                        showRenameDialog(tokenEntry);
                    }

                    @Override
                    public void onNegativeButton() {
                        //Skip here
                    }
                });
                gluuAlert.show();
                return true;
            }
        });

        final String item = token.getUserName();
        if (item != null) {
            binderHelper.bind(holder.swipeLayout, item);

//            holder.textView.setText(item);
            final View finalView = view;
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        ViewHolder tag = (ViewHolder) finalView.getTag();
                        int position = (int) tag.deleteButton.getTag();
                        String token = new Gson().toJson(list.get(position));
                        final TokenEntry tokenEntry = new Gson().fromJson(token, TokenEntry.class);
                        showDeleteKeyAlertView(tokenEntry);
                    }
                }
            });
            holder.renameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        ViewHolder tag = (ViewHolder) finalView.getTag();
                        int position = (int) tag.deleteButton.getTag();
                        String token = new Gson().toJson(list.get(position));
                        final TokenEntry tokenEntry = new Gson().fromJson(token, TokenEntry.class);
                        showRenameDialog(tokenEntry);
                    }
                }
            });
            holder.showButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder tag = (ViewHolder) finalView.getTag();
                    int position = (int) tag.deleteButton.getTag();
                    String tokenString = new Gson().toJson(list.get(position));
                    KeyHandleInfoFragment infoFragment = new KeyHandleInfoFragment().newInstance(tokenString);
                    if (mListener != null) {
//                    Settings.setIsBackButtonVisible(activity.getApplicationContext(), true);
                        Settings.setIsBackButtonVisibleForKey(activity.getApplicationContext(), true);
                        mListener.onKeyHandleInfo(infoFragment);
                    }
                }
            });
        }

        return view;
    }

    private void showRenameDialog(final TokenEntry tokenEntry){
        final CustomGluuAlert gluuAlert = new CustomGluuAlert(activity);
        gluuAlert.setMessage(activity.getApplicationContext().getString(R.string.new_key_name_title));
        gluuAlert.setSub_title(activity.getApplicationContext().getString(R.string.enter_new_key_name));
        gluuAlert.setYesTitle(activity.getApplicationContext().getString(R.string.save));
        gluuAlert.setNoTitle(activity.getApplicationContext().getString(R.string.cancel));
        gluuAlert.setIsTextView(true);
        gluuAlert.type = NotificationType.RENAME_KEY;
        gluuAlert.setmListener(new GluuMainActivity.GluuAlertCallback() {
            @Override
            public void onPositiveButton() {
                Context context = activity.getApplicationContext();
                AndroidKeyDataStore dataStore = new AndroidKeyDataStore(context);
                dataStore.changeKeyHandleName(tokenEntry, gluuAlert.getText());
                updateResults(dataStore);
            }

            @Override
            public void onNegativeButton() {
                //Skip here
            }
        });
        gluuAlert.show();
    }

    void showDeleteKeyAlertView(final TokenEntry tokenEntry){
        GluuMainActivity.GluuAlertCallback listener = new GluuMainActivity.GluuAlertCallback(){
            @Override
            public void onPositiveButton() {
                Context context = activity.getApplicationContext();
                AndroidKeyDataStore dataStore = new AndroidKeyDataStore(context);
                dataStore.deleteKeyHandle(tokenEntry);
                updateResults(dataStore);
            }

            @Override
            public void onNegativeButton() {
                //Skip here
            }
        };
        CustomGluuAlert gluuAlert = new CustomGluuAlert(activity);
        gluuAlert.setMessage(activity.getApplicationContext().getString(R.string.approve_delete));
        gluuAlert.setSub_title(activity.getApplicationContext().getString(R.string.delete_key_sub_title));
        gluuAlert.setYesTitle(activity.getApplicationContext().getString(R.string.yes));
        gluuAlert.setNoTitle(activity.getApplicationContext().getString(R.string.no));
        gluuAlert.setmListener(listener);
        gluuAlert.show();
    }

    public void updateResults(AndroidKeyDataStore dataStore) {
        List<String> tokensString = dataStore.getTokenEntries();
        List<TokenEntry> tokens = new ArrayList<TokenEntry>();
        for (String tokenString : tokensString){
            TokenEntry token = new Gson().fromJson(tokenString, TokenEntry.class);
            tokens.add(token);
        }
        list = tokens;
        //Triggers the list update
        notifyDataSetChanged();
        if (mListener != null){
            mListener.onUpdateList(list.size() == 0);
        }
    }

    private class ViewHolder {
        TextView textView;
        RelativeLayout deleteButton;
        RelativeLayout showButton;
        RelativeLayout renameButton;
        SwipeRevealLayout swipeLayout;
    }
}
