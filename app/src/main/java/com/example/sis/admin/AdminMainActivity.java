package com.example.sis.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sis.R;
import com.example.sis.admin.courses.AdminCoursesFragment;
import com.example.sis.admin.requests.AdminRequestsFragment;
import com.example.sis.admin.settings.AdminSettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminMainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    AdminRequestsFragment adminRequestsFragment;
    AdminCoursesFragment adminCoursesFragment;
    AdminSettingsFragment adminSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initializeViews();
    }

    public void initializeViews() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        adminCoursesFragment = new AdminCoursesFragment();
        adminRequestsFragment = new AdminRequestsFragment();
        adminSettingsFragment = new AdminSettingsFragment();

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.requests);
    }

    @Override
    public void onBackPressed() {
        if (true) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.requests:
                getSupportFragmentManager().beginTransaction().replace(R.id.adminFragment, adminRequestsFragment).commit();
                return true;
            case R.id.courses:
                getSupportFragmentManager().beginTransaction().replace(R.id.adminFragment, adminCoursesFragment).commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.adminFragment, adminSettingsFragment).commit();
                return true;
        }

        return false;
    }
}