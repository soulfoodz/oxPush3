package org.gluu.super_gluu.app.fragments.LogsFragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import org.gluu.super_gluu.app.ApproveDenyFragment;
import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.LogState;
import SuperGluu.app.R;
import org.gluu.super_gluu.app.model.LogInfo;
import org.gluu.super_gluu.app.settings.Settings;
import org.gluu.super_gluu.model.OxPush2Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nazaryavornytskyy on 4/5/16.
 */
public class LogsFragmentListAdapter extends BaseAdapter {

    private List<LogInfo> list;
    private List<LogInfo> selectedLogList = new ArrayList<>();
    private LayoutInflater mInflater;
    private LogsFragment.LogInfoListener mListener;
    private Activity activity;

    private final ViewBinderHelper binderHelper;

    public Boolean isEditingMode = false;
    public Boolean isSelectAllMode = false;

    public LogsFragmentListAdapter(Activity activity, List<LogInfo> logs, LogsFragment.LogInfoListener listener){
        this.list = logs;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        mListener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
//        final LogInfo current_log = list.get(position);
        if (view == null) {
            view = mInflater.inflate(R.layout.fragment_log, parent, false);
            holder = new ViewHolder();
//            holder.textView = (TextView) convertView.findViewById(R.id.text);
            View swipeView = view.findViewById(R.id.delete_layout);
            swipeView.setTag(position);
            holder.deleteButton = (RelativeLayout) swipeView.findViewById(R.id.swipe_delete_button);
            holder.showButton = (RelativeLayout) swipeView.findViewById(R.id.swipe_show_button);
            holder.deleteButton.setTag(position);
            holder.showButton.setTag(position);
//            holder.showView = view.findViewById(R.id.show_layout);
            holder.swipeLayout = (SwipeRevealLayout) view.findViewById(R.id.swipe_layout);
            holder.index = position;
            final View finalView = view;
            holder.swipeLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
                @Override
                public void onClosed(SwipeRevealLayout view) {
                    ViewHolder tag = (ViewHolder) finalView.getTag();
                    int position = tag.index;
                    LogInfo checkedLogInfo = list.get(position);
                    Iterator<LogInfo> iter = selectedLogList.iterator();
                    while (iter.hasNext()) {
                        LogInfo logInf = iter.next();
                        if (checkedLogInfo.getCreatedDate().equalsIgnoreCase(logInf.getCreatedDate())){
                            iter.remove();
                        }
                    }
                }

                @Override
                public void onOpened(SwipeRevealLayout view) {
                    ViewHolder tag = (ViewHolder) finalView.getTag();
                    int position = tag.index;
                    selectedLogList.add(list.get(position));
                }

                @Override
                public void onSlide(SwipeRevealLayout view, float slideOffset) {

                }
            });
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        LogInfo log = list.get(position);
//        view.setTag(position);
        TextView contentView = (TextView) view.findViewById(R.id.content);
        if (log.getLogState() == LogState.ENROL_FAILED || log.getLogState() == LogState.LOGIN_FAILED
                || log.getLogState() == LogState.UNKNOWN_ERROR || log.getLogState() == LogState.LOGIN_DECLINED || log.getLogState() == LogState.ENROL_DECLINED){
            ImageView logo = (ImageView) view.findViewById(R.id.logLogo);
            contentView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.redColor));
            logo.setImageResource(R.drawable.gluu_icon_red);
        } else {
            ImageView logo = (ImageView) view.findViewById(R.id.logLogo);
            contentView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.blackColor));
            logo.setImageResource(R.drawable.gluu_icon);
        }
        LinearLayout log_main_view = (LinearLayout) view.findViewById(R.id.log_main_view);
        log_main_view.setTag(position);
        log_main_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                ApproveDenyFragment approveDenyFragment = new ApproveDenyFragment();
                approveDenyFragment.setIsUserInfo(true);
                OxPush2Request request = oxPush2Adapter(list.get(position));
                approveDenyFragment.setPush2Request(request);
                if (mListener != null) {
//                        Settings.setIsBackButtonVisible(activity.getApplicationContext(), true);
                    Settings.setIsBackButtonVisibleForLog(activity.getApplicationContext(), true);
                    mListener.onKeyHandleInfo(approveDenyFragment);
                }
            }
        });
        String title = log.getIssuer();
            String timeAgo = (String) DateUtils.getRelativeTimeSpanString(Long.valueOf(log.getCreatedDate()), System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS);
            TextView createdTime = (TextView) view.findViewById(R.id.created_date);
            createdTime.setText(timeAgo);
        switch (log.getLogState()){
            case LOGIN_SUCCESS:
                title = "Logged in " + log.getIssuer();
                break;
            case ENROL_SUCCESS:
                title = "Enrol to " + log.getIssuer();
                break;
            case ENROL_DECLINED:
                title = "Declined enrol to " + log.getIssuer();
                break;
            case LOGIN_DECLINED:
                title = "Declined login to " + log.getIssuer();
                break;

            default:
                break;
        }
        contentView.setText(title);

        final String item = log.getUserName();
        if (item != null) {
            binderHelper.bind(holder.swipeLayout, item);

//            holder.textView.setText(item);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onDeleteLogEvent();
                    }
                }
            });
            final View finalView = view;
            holder.showButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder tag = (ViewHolder) finalView.getTag();
                    int position = (int) tag.deleteButton.getTag();
                    ApproveDenyFragment approveDenyFragment = new ApproveDenyFragment();
                    approveDenyFragment.setIsUserInfo(true);
                    OxPush2Request request = oxPush2Adapter(list.get(position));
                    approveDenyFragment.setPush2Request(request);
                    if (mListener != null) {
//                        Settings.setIsBackButtonVisible(activity.getApplicationContext(), true);
                        Settings.setIsBackButtonVisibleForLog(activity.getApplicationContext(), true);
                        mListener.onKeyHandleInfo(approveDenyFragment);
                    }
                }
            });
        }

        //Setup fonts
        Typeface face = Typeface.createFromAsset(activity.getAssets(), "ProximaNova-Regular.otf");
        Typeface faceTitle = Typeface.createFromAsset(activity.getAssets(), "ProximaNova-Semibold.otf");
        contentView.setTypeface(faceTitle);
        createdTime.setTypeface(face);

        //Show hide checkboxes depends on editing mode
        final CheckBox check = (CheckBox) view.findViewById(R.id.logCheckBox);
        check.setTag(position);
        check.setChecked(selectedLogList.size() > 0);
        LogInfo checkedLogInfo = list.get((Integer) check.getTag());
        for (LogInfo logInf : new ArrayList<>(selectedLogList)){
            if (isSelectAllMode){
                check.setChecked(isSelectAllMode);
            } else {
                check.setChecked(checkedLogInfo.getCreatedDate().equalsIgnoreCase(logInf.getCreatedDate()));
            }
        }
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    selectedLogList.add(list.get((Integer) check.getTag()));
                } else {
                    LogInfo checkedLogInfo = list.get((Integer) check.getTag());
                    Iterator<LogInfo> iter = selectedLogList.iterator();
                    while (iter.hasNext()) {
                        LogInfo logInf = iter.next();
                        if (checkedLogInfo.getCreatedDate().equalsIgnoreCase(logInf.getCreatedDate())){
                            iter.remove();
                        }
                    }
                }
            }
        });
        if (isEditingMode){
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.GONE);
        }

        return view;
    }

    private OxPush2Request oxPush2Adapter(LogInfo logInfo) {
        OxPush2Request request = new OxPush2Request();
        request.setUserName(logInfo.getUserName());
        request.setIssuer(logInfo.getIssuer());
        request.setLocationCity(logInfo.getLocationAddress());
        request.setLocationIP(logInfo.getLocationIP());
        request.setCreated(logInfo.getCreatedDate());
        request.setMethod(logInfo.getMethod());
        return request;
    }

    public void updateResults(List<LogInfo> results) {
        list = results;
        //Triggers the list update
        notifyDataSetChanged();
//        Settings.setIsButtonVisible(activity.getApplicationContext(), list.size() != 0);
    }

    public List<LogInfo> getSelectedLogList(){
        return selectedLogList;
    }

    public void selectAllLogs(){
        selectedLogList.clear();
        selectedLogList.addAll(list);
    }

    public void deSelectAllLogs(){
        selectedLogList.clear();
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }

    private class ViewHolder {
        int index;
        TextView textView;
        RelativeLayout deleteButton;
        RelativeLayout showButton;
        SwipeRevealLayout swipeLayout;
    }
}
