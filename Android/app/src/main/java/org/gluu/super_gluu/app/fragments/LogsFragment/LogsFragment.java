package org.gluu.super_gluu.app.fragments.LogsFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.gluu.super_gluu.app.ApproveDenyFragment;
import SuperGluu.app.R;

import org.gluu.super_gluu.app.GluuMainActivity;
import org.gluu.super_gluu.app.customGluuAlertView.CustomGluuAlert;
import org.gluu.super_gluu.app.model.LogInfo;
import org.gluu.super_gluu.store.AndroidKeyDataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by nazaryavornytskyy on 3/22/16.
 */
public class LogsFragment extends Fragment {

    private LogsFragmentListAdapter listAdapter;
    private AndroidKeyDataStore dataStore;
    private LogInfoListener mListener;
    public ApproveDenyFragment.OnDeleteLogInfoListener deleteLogListener;
    private LinearLayout selectAllView;
    private Button selectAllButton;
    private ListView listView;
    private List<LogInfo> logs;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
//            String message = intent.getStringExtra("message");
            reloadLogs();
            // fire the event
            listAdapter.updateResults(logs);
        }
    };

    private BroadcastReceiver mEditingModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Boolean editMode = intent.getBooleanExtra("isEditingMode", false);
            // fire the event
            listAdapter.isEditingMode = editMode;
            listAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver mDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAlertView();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_logs_list, container, false);
        dataStore = new AndroidKeyDataStore(getActivity().getApplicationContext());
        reloadLogs();
        listView = (ListView) rootView.findViewById(R.id.logs_listView);
        mListener = new LogInfoListener() {
            @Override
            public void onKeyHandleInfo(ApproveDenyFragment approveDenyFragment) {
                setIsButtonVisible(false);
                getActivity().invalidateOptionsMenu();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.logs_root_frame, approveDenyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onDeleteLogEvent() {
                showAlertView();
            }
        };
        View actionBarView = (View) rootView.findViewById(R.id.actionBarSettings);
        actionBarView.findViewById(R.id.action_right_button).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.actionbar_icon).setVisibility(View.VISIBLE);
        actionBarView.findViewById(R.id.actionbar_textview).setVisibility(View.GONE);
        selectAllView = (LinearLayout) rootView.findViewById(R.id.selectAllView);
        selectAllView.setVisibility(View.GONE);
        LinearLayout leftButton = (LinearLayout) actionBarView.findViewById(R.id.action_left_button);
        final Button rightButton = (Button) actionBarView.findViewById(R.id.action_right_button);
        selectAllButton = (Button) selectAllView.findViewById(R.id.selectAllButton);
        selectAllButton.setTag(1);// 1- no selected all, 2- selected all
        rightButton.setVisibility(View.GONE);
        int visible = logs.size() > 0 ? View.VISIBLE : View.GONE;
        leftButton.setVisibility(visible);
        leftButton.findViewById(R.id.actionBarBackArrow).setVisibility(View.GONE);
        final Button backButton = (Button) leftButton.findViewById(R.id.actionBarBackButton);
        backButton.setText("Edit");
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) backButton.getLayoutParams();
        params.leftMargin = 25;
        backButton.requestLayout();
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listAdapter.isEditingMode){
                    backButton.setText("Edit");
                    listAdapter.isEditingMode = false;
                    listAdapter.notifyDataSetChanged();
                    rightButton.setVisibility(View.GONE);
                    selectAllView.setVisibility(View.GONE);
                } else {
                    backButton.setText("Cancel");
                    selectAllButton.setText("Select All");
                    deselectAll();
                    listAdapter.isEditingMode = true;
                    listAdapter.notifyDataSetChanged();
                    rightButton.setVisibility(View.VISIBLE);
                    selectAllView.setVisibility(View.VISIBLE);
                }
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertView();
            }
        });
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int)v.getTag();
                if (tag == 1) {
                    selectAll();
                } else {
                    deselectAll();
                }
            }
        });

        listAdapter = new LogsFragmentListAdapter(getActivity(), logs, mListener);
        listView.setAdapter(listAdapter);
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(0);//logs.size() - 1);
            }
        });
        TextView noLogs = (TextView) rootView.findViewById(R.id.noLogs_textView);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) rootView.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        noLogs.setY(metrics.heightPixels/3);
        listView.setEmptyView(noLogs);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("reload-logs"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mEditingModeReceiver,
                new IntentFilter("editing-mode-logs"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDeleteReceiver,
                new IntentFilter("on-delete-logs"));

        return rootView;
    }

    private void deselectAll() {
        //Deselect all records
        selectAllButton.setTag(1);// 1- no selected all, 2- selected all
        listAdapter.isSelectAllMode = false;
        selectAllButton.setText("Select All");
        listAdapter.notifyDataSetChanged();
        listAdapter.deSelectAllLogs();
    }

    private void selectAll() {
        //Select all records
        selectAllButton.setTag(2);// 1- no selected all, 2- selected all
        selectAllButton.setText("Deselect All");
        listAdapter.isSelectAllMode = true;
        listAdapter.selectAllLogs();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDeleteReceiver);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mEditingModeReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ApproveDenyFragment.OnDeleteLogInfoListener) {
            deleteLogListener = (ApproveDenyFragment.OnDeleteLogInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeleteKeyHandleListener");
        }
    }

    private void reloadLogs(){
        List<LogInfo> logsFromDB = new ArrayList<LogInfo>(dataStore.getLogs());
        Collections.sort(logsFromDB, new Comparator<LogInfo>(){
            public int compare(LogInfo log1, LogInfo log2) {
                Date date1 = new Date(Long.valueOf(log1.getCreatedDate()));
                Date date2 = new Date(Long.valueOf(log2.getCreatedDate()));
                return date1.compareTo(date2);
            }
        });
        Collections.reverse(logsFromDB);
        logs = logsFromDB;
    }

    void showAlertView(){
        final Fragment frg = this;
        GluuMainActivity.GluuAlertCallback listener = new GluuMainActivity.GluuAlertCallback(){
            @Override
            public void onPositiveButton() {
                if (!listAdapter.getSelectedLogList().isEmpty() && deleteLogListener != null){
                    deleteLogListener.onDeleteLogInfo(listAdapter.getSelectedLogList());
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(frg).attach(frg).commit();
                }
            }

            @Override
            public void onNegativeButton() {
                //Skip here
            }
        };
        CustomGluuAlert gluuAlert = new CustomGluuAlert(getActivity());
        if (listAdapter.getSelectedLogList().isEmpty()){
//            gluuAlert.setMessage(getActivity().getApplicationContext().getString(R.string.clear_log_empty_title));
            gluuAlert.setSub_title(getActivity().getApplicationContext().getString(R.string.clear_log_empty_title));
            gluuAlert.setYesTitle(getActivity().getApplicationContext().getString(R.string.ok));
        } else {
//            gluuAlert.setMessage(getActivity().getApplicationContext().getString(R.string.clear_log_title));
            gluuAlert.setSub_title(getActivity().getApplicationContext().getString(R.string.clear_log_title));
            gluuAlert.setYesTitle(getActivity().getApplicationContext().getString(R.string.yes));
            gluuAlert.setNoTitle(getActivity().getApplicationContext().getString(R.string.no));
        }
        gluuAlert.setmListener(listener);
        gluuAlert.show();
    }

    public void setIsButtonVisible(Boolean isVsible){
        SharedPreferences preferences = getContext().getSharedPreferences("CleanLogsSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCleanButtonVisible", isVsible);
        editor.commit();
    }

    public interface LogInfoListener {
        void onKeyHandleInfo(ApproveDenyFragment approveDenyFragment);
        void onDeleteLogEvent();
    }

}
