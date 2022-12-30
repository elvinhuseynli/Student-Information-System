package com.example.sis.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.sis.R;
import com.example.sis.student.courses.selected.StudentCoursesFragment;
import com.example.sis.student.grades.StudentGradesFragment;
import com.example.sis.student.settings.StudentSettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StudentMainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    StudentCoursesFragment studentCoursesFragment;
    StudentGradesFragment studentGradesFragment;
    StudentSettingsFragment studentSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        initializeViews();
    }

    public void initializeViews() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        studentCoursesFragment = new StudentCoursesFragment();
        studentGradesFragment = new StudentGradesFragment();
        studentSettingsFragment = new StudentSettingsFragment();

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.courses);
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
            case R.id.courses:
                getSupportFragmentManager().beginTransaction().replace(R.id.studentFragment, studentCoursesFragment).commit();
                return true;
            case R.id.grades:
                getSupportFragmentManager().beginTransaction().replace(R.id.studentFragment, studentGradesFragment).commit();
                return true;
            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.studentFragment, studentSettingsFragment).commit();
                return true;
        }

        return false;
    }
}