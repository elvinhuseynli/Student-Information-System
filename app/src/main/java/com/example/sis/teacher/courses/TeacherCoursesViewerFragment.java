package com.example.sis.teacher.courses;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.example.sis.admin.courses.AdminCoursesFragment;
import com.example.sis.admin.courses.CourseDetailsConstants;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.HashMap;
import java.util.Map;

public class TeacherCoursesViewerFragment extends Fragment {

    EditText courseCode, courseName, courseTutor,courseEcts, courseSyllabus, midtermPercentage, finalPercentage;
    MaskedEditText courseStartTime, courseEndTime;
    AutoCompleteTextView courseDepartment, courseDay;
    ProgressBar progressBar, progressBar1;
    boolean errorExists=false;
    Button saveButton;
    String courseCodeText;
    ConstraintLayout layout;
    ImageView backButton;
    FirebaseFirestore database;

    public TeacherCoursesViewerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_courses_viewer, container, false);

        Bundle bundle = this.getArguments();

        courseCodeText = bundle.getString("courseCode");

        initializeViews(view);

        setListeners();

        retrieveDataFromDatabase();

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
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        layout = (ConstraintLayout) view.findViewById(R.id.constraintLayout);
        backButton = (ImageView) view.findViewById(R.id.backButton);

        database = FirebaseFirestore.getInstance();
    }

    public void setListeners() {
        backButton.setOnClickListener(v-> {
            translateToPreviousPage();
        });

        saveButton.setOnClickListener(v-> {
            if(!checkValidity()) {
                saveButton.setText("");
                progressBar.setVisibility(View.VISIBLE);
                addDataToDatabase();
            }
        });
    }


    public boolean checkValidity() {
        errorExists = false;

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

        if(courseSyllabus.getText().toString().length() == 0) {
            courseSyllabus.setText("");
        }

        return errorExists;
    }

    public void retrieveDataFromDatabase() {
        Query query = database.collection("courses")
                .whereEqualTo(FieldPath.documentId(), courseCodeText);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size() != 0) {
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    courseCode.setText(doc.getString(CourseDetailsConstants.COURSE_CODE));
                    courseName.setText(doc.getString(CourseDetailsConstants.COURSE_NAME));
                    midtermPercentage.setText(doc.getString(CourseDetailsConstants.COURSE_MIDTERM));
                    finalPercentage.setText(doc.getString(CourseDetailsConstants.COURSE_FINAL));
                    courseEcts.setText(doc.getString(CourseDetailsConstants.COURSE_ECTS));
                    courseTutor.setText(doc.getString(CourseDetailsConstants.COURSE_TUTOR));
                    courseDepartment.setText(doc.getString(CourseDetailsConstants.COURSE_DEPARTMENT));
                    courseDay.setText(doc.getString(CourseDetailsConstants.COURSE_DAY));
                    courseStartTime.setText(doc.getString(CourseDetailsConstants.COURSE_START_TIME));
                    courseEndTime.setText(doc.getString(CourseDetailsConstants.COURSE_END_TIME));
                    courseSyllabus.setText(doc.getString(CourseDetailsConstants.COURSE_SYLLABUS));
                } else {Toast.makeText(getActivity(), "Unable to load data", Toast.LENGTH_LONG).show();}
                progressBar1.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addDataToDatabase() {
        Map<String, Object> data = new HashMap<>();

        data.put("midtermPercentage", midtermPercentage.getText().toString());
        data.put("finalPercentage", finalPercentage.getText().toString());
        data.put("courseSyllabus", courseSyllabus.getText().toString());

        database.collection("courses")
                .document(courseCodeText).update(data)
                .addOnSuccessListener(t->{
                    Toast.makeText(getActivity(), "Course has been updated!", Toast.LENGTH_LONG).show();
                    translateToPreviousPage();
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
        TeacherCoursesFragment fragment = new TeacherCoursesFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.teacherFragment, fragment);
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