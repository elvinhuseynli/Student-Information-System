package com.example.sis.admin.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sis.R;

public class AdminSettingsFragment extends Fragment {

    public AdminSettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_settings, container, false);

        initializeViews(view);

        setListeners();

        return view;
    }

    public void initializeViews(View view) {

    }

    public void setListeners() {

    }
}