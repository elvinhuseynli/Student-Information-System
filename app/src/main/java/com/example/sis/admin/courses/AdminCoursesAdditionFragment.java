package com.example.sis.admin.courses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sis.R;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.HashMap;

public class AdminCoursesAdditionFragment extends Fragment {

    EditText courseCode, courseName, courseTutor,courseEcts, courseSyllabus, midtermPercentage, finalPercentage;
    MaskedEditText courseStartTime, courseEndTime;
    AutoCompleteTextView courseDepartment, courseDay;
    ProgressBar progressBar;
    String[] listOfDepartments, listOfDays;
    boolean errorExists=false;
    Button addButton;
    ImageView backButton;
    FirebaseFirestore database;

    public AdminCoursesAdditionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_courses_addition, container, false);

        initializeViews(view);

        setListeners();

        return view;
    }

    public void initializeViews(View view) {
        courseName = (EditText) view.findViewById(R.id.courseName);
        courseCode = (EditText) view.findViewById(R.id.courseCode);
        courseTutor = (EditText) view.findViewById(R.id.courseTutor);
        courseEcts = (EditText) view.findViewById(R.id.courseEcts);
        midtermPercentage = (EditText) view.findViewById(R.id.midtermPercentage);
        finalPercentage = (EditText) view.findViewById(R.id.finalPercentage);
        courseStartTime = (MaskedEditText) view.findViewById(R.id.courseStartTime);
        courseEndTime = (MaskedEditText) view.findViewById(R.id.courseEndTime);
        courseDay = (AutoCompleteTextView) view.findViewById(R.id.courseDay);
        courseSyllabus = (EditText) view.findViewById(R.id.courseSyllabus);
        courseDepartment = (AutoCompleteTextView) view.findViewById(R.id.courseDepartment);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        addButton = (Button) view.findViewById(R.id.addButton);
        backButton = (ImageView) view.findViewById(R.id.backButton);

        database = FirebaseFirestore.getInstance();

        listOfDepartments = getResources().getStringArray(R.array.departments_list);
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listOfDepartments);
        courseDepartment.setAdapter(adapter);

        listOfDays = getResources().getStringArray(R.array.days_list);
        ArrayAdapter adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listOfDays);
        courseDay.setAdapter(adapter1);
    }

    public void setListeners() {
        backButton.setOnClickListener(v-> {
            translateToPreviousPage();
        });

        addButton.setOnClickListener(v-> {
            if(!checkValidity()) {
                addButton.setText("");
                progressBar.setVisibility(View.VISIBLE);
                addDataToDatabase();
            }
        });

        courseStartTime.setOnKeyListener((v, keyCode, event) -> {
            checkTime(courseStartTime);
            return false;
        });

        courseEndTime.setOnKeyListener((v, keyCode, event) -> {
            checkTime(courseEndTime);
            return false;
        });
    }

    public boolean checkValidity() {
        errorExists = false;

        if(courseName.getText().toString().length()==0) {
            courseName.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!courseName.getText().toString().matches(getString(R.string.name))) {
            courseName.setError("Course name should contain only letters!");
            errorExists = true;
        }

        if(courseTutor.getText().toString().length()==0) {
            courseTutor.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!courseTutor.getText().toString().matches(getString(R.string.name))) {
            courseTutor.setError("Course name should contain only letters!");
            errorExists = true;
        }

        if(midtermPercentage.getText().length()==0) {
            midtermPercentage.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(finalPercentage.getText().length()==0) {
            finalPercentage.setError("This field cannot be left blank!");
            errorExists = true;
        }

        int mid = Integer.parseInt(midtermPercentage.getText().toString());
        int finalG = Integer.parseInt(finalPercentage.getText().toString());

        if(mid+finalG!=100) {
            showAlertDialog("Total of midterm and final percentages should be 100!");
            errorExists = true;
        }

        if(courseCode.getText().toString().length()==0) {
            courseCode.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(courseEcts.getText().length()==0) {
            courseEcts.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(courseStartTime.getText().length()==0) {
            courseStartTime.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(courseEndTime.getText().length()==0) {
            courseEndTime.setError("This field cannot be left blank!");
            errorExists = true;
        }

        if(courseDepartment.getText().toString().length() == 0) {
            courseDepartment.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!ArrayUtils.contains(listOfDepartments, courseDepartment.getText().toString())) {
            courseDepartment.setError("Please select a valid department");
            errorExists = true;
        }

        if(courseSyllabus.getText().toString().length() == 0) {
            courseSyllabus.setText("");
        }

        if(courseDay.getText().toString().length() == 0) {
            courseDay.setError("This field cannot be left blank!");
            errorExists = true;
        } else if(!ArrayUtils.contains(listOfDays, courseDay.getText().toString())) {
            courseDay.setError("Please select a valid day");
            errorExists = true;
        }

        return errorExists;
    }

    public void checkTime(MaskedEditText text) {
        String time = text.getText().toString();
        if(time.length()>4) {
            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(3,5));

            hour = hour>23 ? hour%24 : hour;
            minute = minute>59 ? minute%60 : minute;

            String formatted = String.format("%02d:%02d", hour, minute);

            text.setText(formatted);
        }
    }

    public void addDataToDatabase() {
        HashMap<String, String> data = new HashMap<>();

        data.put(CourseDetailsConstants.COURSE_CODE, courseCode.getText().toString());
        data.put(CourseDetailsConstants.COURSE_NAME, courseName.getText().toString());
        data.put(CourseDetailsConstants.COURSE_DEPARTMENT, courseDepartment.getText().toString());
        data.put(CourseDetailsConstants.COURSE_TUTOR, courseTutor.getText().toString());
        data.put(CourseDetailsConstants.COURSE_DAY, courseDay.getText().toString());
        data.put(CourseDetailsConstants.COURSE_ECTS, courseEcts.getText().toString());
        data.put(CourseDetailsConstants.COURSE_MIDTERM, midtermPercentage.getText().toString());
        data.put(CourseDetailsConstants.COURSE_FINAL, finalPercentage.getText().toString());
        data.put(CourseDetailsConstants.COURSE_SYLLABUS, courseSyllabus.getText().toString());
        data.put(CourseDetailsConstants.COURSE_START_TIME, courseStartTime.getText().toString());
        data.put(CourseDetailsConstants.COURSE_END_TIME, courseEndTime.getText().toString());

        Query query = database.collection("courses")
                .whereEqualTo(FieldPath.documentId(), courseCode.getText().toString());

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size() == 0) {
                    database.collection("courses")
                            .document(courseCode.getText().toString())
                            .set(data).addOnSuccessListener(unused -> {
                                addButton.setText("Add course");
                                progressBar.setVisibility(View.INVISIBLE);
                                translateToPreviousPage();
                                Toast.makeText(getActivity(), "Course was successfully added!", Toast.LENGTH_LONG).show();
                            });
                } else {
                    addButton.setText("add course");
                    progressBar.setVisibility(View.INVISIBLE);
                    showAlertDialog("Course already exists!");
                }
            }
        });
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder regDialog = new AlertDialog.Builder(getActivity());
        regDialog.setMessage(message);
        regDialog.setTitle("Alert");
        regDialog.setCancelable(false);
        regDialog.setPositiveButton("OK",
                (dialog, nom) -> dialog.cancel());
        AlertDialog dialog = regDialog.create();
        dialog.show();
    }

    public void translateToPreviousPage() {
        AdminCoursesFragment fragment = new AdminCoursesFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.adminFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null) return;

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                translateToPreviousPage();
                return true;
            }return false;
        });
    }
}