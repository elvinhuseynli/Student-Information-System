package com.example.sis.student.courses.selected;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sis.R;
import com.example.sis.admin.courses.AdminCoursesFragment;
import com.example.sis.admin.courses.CourseDetailsConstants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

public class StudentCoursesViewerFragment extends Fragment {

    TextView courseCode, courseName, courseTutor, courseDepartment;
    TextView courseDay, courseHours, courseSyllabus, courseEcts, courseGrade;
    ImageView backButton;
    ProgressBar progressBar;
    ConstraintLayout layout;
    String courseCodeText;
    FirebaseFirestore database;

    public StudentCoursesViewerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_courses_viewer, container, false);

        Bundle bundle = this.getArguments();

        courseCodeText = bundle.getString("courseCode");

        initializeViews(view);
        setListeners();

        retrieveDataFromDatabase();

        return view;
    }


    public void initializeViews(View view) {
        courseCode = (TextView) view.findViewById(R.id.courseCode);
        courseName = (TextView) view.findViewById(R.id.courseName);
        courseTutor = (TextView) view.findViewById(R.id.courseTutor);
        courseDepartment = (TextView) view.findViewById(R.id.courseDepartment);
        courseDay = (TextView) view.findViewById(R.id.courseDay);
        courseHours = (TextView) view.findViewById(R.id.courseHours);
        courseSyllabus = (TextView) view.findViewById(R.id.courseSyllabus);
        courseEcts = (TextView) view.findViewById(R.id.courseEcts);
        courseGrade = (TextView) view.findViewById(R.id.courseGrade);
        backButton = (ImageView) view.findViewById(R.id.backButton);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        layout = (ConstraintLayout) view.findViewById(R.id.layout);

        database = FirebaseFirestore.getInstance();
    }

    public void setListeners() {
        backButton.setOnClickListener(v -> translateToPreviousPage());
    }

    @SuppressLint("SetTextI18n")
    public void retrieveDataFromDatabase() {
        Query query = database.collection("courses")
                .whereEqualTo(FieldPath.documentId(), courseCodeText);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size() != 0) {
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    courseCode.setText(doc.getString(CourseDetailsConstants.COURSE_CODE));
                    courseName.setText(doc.getString(CourseDetailsConstants.COURSE_NAME));
                    courseEcts.setText(doc.getString(CourseDetailsConstants.COURSE_ECTS));
                    courseGrade.setText(doc.getString(CourseDetailsConstants.COURSE_MIDTERM)+", "+ doc.getString(CourseDetailsConstants.COURSE_FINAL));
                    courseTutor.setText(doc.getString(CourseDetailsConstants.COURSE_TUTOR));
                    courseDepartment.setText(doc.getString(CourseDetailsConstants.COURSE_DEPARTMENT));
                    courseDay.setText(doc.getString(CourseDetailsConstants.COURSE_DAY));
                    courseHours.setText(doc.getString(CourseDetailsConstants.COURSE_START_TIME)+" - "+doc.getString(CourseDetailsConstants.COURSE_END_TIME));
                    courseSyllabus.setText(doc.getString(CourseDetailsConstants.COURSE_SYLLABUS));
                } else {
                    Toast.makeText(getActivity(), "Unable to load data", Toast.LENGTH_LONG).show();}
                progressBar.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void translateToPreviousPage() {
        StudentCoursesFragment fragment = new StudentCoursesFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.studentFragment, fragment);
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