package com.example.sis.student.grades;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sis.R;
import com.example.sis.admin.courses.CourseDetailsConstants;
import com.example.sis.student.courses.available.StudentAvailableCoursesFragment;
import com.example.sis.student.courses.selected.StudentCoursesAdapter;
import com.example.sis.student.courses.selected.StudentCoursesFragment;
import com.example.sis.student.courses.selected.StudentCoursesViewerFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StudentGradesFragment extends Fragment implements StudentGradesListener {

    RecyclerView recyclerView;
    ImageView warning;
    TextView emptyMessage;
    String studentId;
    SearchView searchView;
    ProgressBar progressBar;
    List<CourseDetailsConstants> constantsList;
    StudentGradesAdapter studentGradesAdapter;
    FirebaseFirestore database;

    public StudentGradesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_grades, container, false);

        Intent intent = getActivity().getIntent();

        Bundle bundle = intent.getExtras();

        studentId = bundle.getString("idNumber");

        initializeViews(view);

        setListeners();

        retrieveDataFromDatabase();

        return view;
    }


    public void initializeViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        warning = (ImageView) view.findViewById(R.id.warning);
        emptyMessage = (TextView) view.findViewById(R.id.emptyMessage);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        constantsList = new ArrayList<>();
        searchView = (SearchView) view.findViewById(R.id.search_bar);
        database = FirebaseFirestore.getInstance();
    }

    public void setListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!searchView.isIconified()) {
                    List<CourseDetailsConstants> filteredList = new ArrayList<>();
                    if(constantsList.size()>0) {
                        for (int i = 0; i < constantsList.size(); i++) {
                            if (constantsList.get(i).getCourseCode().toLowerCase().contains(newText.toLowerCase())) {
                                filteredList.add(constantsList.get(i));
                            }
                        }
                        if (filteredList.isEmpty()) {
                            studentGradesAdapter.setVisibility(filteredList, isResumed());
                            Toast.makeText(getActivity(), "Unable to find any result", Toast.LENGTH_SHORT).show();
                        } else{}
                        studentGradesAdapter.setVisibility(filteredList, isResumed());
                    }
                }
                return false;
            }
        });
    }

    public void retrieveDataFromDatabase() {
        Query query = database.collection("students")
                .whereEqualTo(FieldPath.documentId(), studentId);

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size() != 0) {
                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    List<String> coursesList = (List<String>) doc.get("courses");
                    CourseDetailsConstants.setCourseList(coursesList);
                    Query query1 = database.collection("courses");
                    query1.get().addOnCompleteListener(task1 -> {
                        if(task.isSuccessful()) {
                            if(task.getResult().size() !=0) {
                                for(QueryDocumentSnapshot qds: task1.getResult()) {
                                    String cc = qds.getString(CourseDetailsConstants.COURSE_CODE);
                                    if(coursesList.contains(cc)) {
                                        CourseDetailsConstants constants = new CourseDetailsConstants();
                                        constants.setCourseCode(qds.getString(CourseDetailsConstants.COURSE_CODE));
                                        Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) qds.get("grades");
                                        for (String key: map.keySet()) {
                                            if(key.equals(studentId)) {
                                                Map<String, String> temp = map.get(key);
                                                constants.setCourseGrade("Grade: "+temp.get("total"));
                                            }
                                        }
                                        constantsList.add(constants);
                                    }
                                }
                                if(constantsList.size()>0) {
                                    warning.setVisibility(View.INVISIBLE);
                                    emptyMessage.setVisibility(View.INVISIBLE);
                                    Collections.sort(constantsList, ((o1, o2) -> o2.getCourseCode().compareTo(o1.getCourseCode())));
                                    studentGradesAdapter = new StudentGradesAdapter(constantsList, StudentGradesFragment.this);
                                    recyclerView.setAdapter(studentGradesAdapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }else {
                                    warning.setVisibility(View.VISIBLE);
                                    emptyMessage.setVisibility(View.VISIBLE);
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                            else {
                                warning.setVisibility(View.VISIBLE);
                                emptyMessage.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    warning.setVisibility(View.VISIBLE);
                    emptyMessage.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.studentFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCoursesClicked(CourseDetailsConstants courseDetailsConstants) {
        Bundle bundle = new Bundle();
        bundle.putString("courseCode", courseDetailsConstants.getCourseCode());

        StudentGradesViewerFragment studentGradesViewerFragment = new StudentGradesViewerFragment();
        studentGradesViewerFragment.setArguments(bundle);
        changeFragment(studentGradesViewerFragment);
    }
}