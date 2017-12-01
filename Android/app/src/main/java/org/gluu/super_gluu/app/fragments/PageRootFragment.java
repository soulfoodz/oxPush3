package org.gluu.super_gluu.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gluu.super_gluu.app.FragmentType;
import org.gluu.super_gluu.app.fragments.KeysFragment.KeyFragmentListFragment;
import org.gluu.super_gluu.app.fragments.LogsFragment.LogsFragment;
import org.gluu.super_gluu.app.fragments.SettingsFragment.SettingsList.SettingsListFragment;
import org.gluu.super_gluu.app.MainActivityFragment;

import SuperGluu.app.R;

/**
 * Created by nazaryavornytskyy on 3/25/16.
 */

public class PageRootFragment extends Fragment {

    private FragmentType.FRAGMENT_TYPE fragmentType;

    public PageRootFragment(){}

    public PageRootFragment newInstance(FragmentType.FRAGMENT_TYPE fragmentType) {
        PageRootFragment rootFragment = new PageRootFragment();

        Bundle args = new Bundle();
        args.putSerializable("fragmentType", fragmentType);
        rootFragment.setArguments(args);

        return rootFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentType.FRAGMENT_TYPE type = (FragmentType.FRAGMENT_TYPE) getArguments().getSerializable("fragmentType");
        setFragmentType(type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
        View view = null;// = inflater.inflate(R.layout.page_root_fragment, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */

        switch (this.fragmentType){
            case MAIN_FRAGMENT:
                view = inflater.inflate(R.layout.main_root_fragment, container, false);
                transaction.replace(R.id.main_root_frame, new MainActivityFragment());
                break;
            case LOGS_FRAGMENT:
                view = inflater.inflate(R.layout.logs_root_fragment, container, false);
                transaction.replace(R.id.logs_root_frame, new LogsFragment());
                break;
            case KEYS_FRAGMENT:
                view = inflater.inflate(R.layout.keys_root_fragment, container, false);
                transaction.replace(R.id.keys_root_frame, new KeyFragmentListFragment());
                break;
            case SETTINGS_FRAGMENT:
                view = inflater.inflate(R.layout.page_root_fragment, container, false);
                transaction.replace(R.id.root_frame, new SettingsListFragment());
                break;
            default:
                break;

        }

//        transaction.replace(R.id.root_frame, new SettingsListFragment());

        transaction.commit();

        return view;
    }

    private void setFragmentType(FragmentType.FRAGMENT_TYPE fragmentType) {
        this.fragmentType = fragmentType;
    }
}
