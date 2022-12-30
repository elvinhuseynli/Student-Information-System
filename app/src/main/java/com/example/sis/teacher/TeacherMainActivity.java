package com.example.sis.teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.sis.R;
import com.example.sis.teacher.courses.TeacherCoursesFragment;
import com.example.sis.teacher.policy.TeacherPolicyFragment;
import com.example.sis.teacher.settings.TeacherSettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class TeacherMainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    TeacherCoursesFragment teacherCoursesFragment;
    TeacherSettingsFragment teacherSettingsFragment;
    TeacherPolicyFragment teacherRequestsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        initializeViews();
    }

    public void initializeViews() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        teacherCoursesFragment = new TeacherCoursesFragment();
        teacherRequestsFragment = new TeacherPolicyFragment();
        teacherSettingsFragment = new TeacherSettingsFragment();

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.courses);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.courses:
                getSupportFragmentManager().beginTransaction().replace(R.id.teacherFragment, teacherCoursesFragment).commit();
                return true;
            case R.id.requests:
                getSupportFragmentManager().beginTransaction().replace(R.id.teacherFragment, teacherRequestsFragment).commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.teacherFragment, teacherSettingsFragment).commit();
                return true;
        }

        return false;
    }
}