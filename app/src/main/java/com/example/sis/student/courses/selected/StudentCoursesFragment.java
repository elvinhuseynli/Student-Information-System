package com.example.sis.student.courses.selected;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.sis.admin.courses.AdminCoursesAdapter;
import com.example.sis.admin.courses.AdminCoursesAdditionFragment;
import com.example.sis.admin.courses.AdminCoursesFragment;
import com.example.sis.admin.courses.AdminCoursesViewerFragment;
import com.example.sis.admin.courses.CourseDetailsConstants;
import com.example.sis.mainact.Constants;
import com.example.sis.student.courses.available.StudentAvailableCoursesFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentCoursesFragment extends Fragment implements StudentCoursesListener {

    RecyclerView recyclerView;
    ImageView warning, selectButton;
    TextView emptyMessage;
    String studentId;
    SearchView searchView;
    ProgressBar progressBar;
    List<CourseDetailsConstants> constantsList;
    StudentCoursesAdapter studentCoursesAdapter;
    FirebaseFirestore database;

    public StudentCoursesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_courses, container, false);

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
        selectButton = (ImageView) view.findViewById(R.id.selectButton);
        emptyMessage = (TextView) view.findViewById(R.id.emptyMessage);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        constantsList = new ArrayList<>();
        searchView = (SearchView) view.findViewById(R.id.search_bar);
        database = FirebaseFirestore.getInstance();
    }

    public void setListeners() {
        selectButton.setOnClickListener(v-> {
            StudentAvailableCoursesFragment fragment = new StudentAvailableCoursesFragment();
            changeFragment(fragment);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!searchView.isIconified()) {
                    List<CourseDetailsConstants> filteredList = new ArrayList<>();
                    if(constantsList.size()>0) {
                        for (int i = 0; i < constantsList.size(); i++) {
                            if ((constantsList.get(i).getCourseCode().toLowerCase().contains(newText.toLowerCase()) ||
                                    constantsList.get(i).getCourseName().toLowerCase().contains(newText.toLowerCase()) ||
                                    constantsList.get(i).getCourseTutor().toLowerCase().contains(newText.toLowerCase()) ||
                                    constantsList.get(i).getCourseDepartment().toLowerCase().contains(newText.toLowerCase()))) {
                                filteredList.add(constantsList.get(i));
                            }
                        }
                        if (filteredList.isEmpty()) {
                            studentCoursesAdapter.setVisibility(filteredList, isResumed());
                            Toast.makeText(getActivity(), "Unable to find any result", Toast.LENGTH_SHORT).show();
                        } else{}
                        studentCoursesAdapter.setVisibility(filteredList, isResumed());
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
                                        constants.setCourseName(qds.getString(CourseDetailsConstants.COURSE_NAME));
                                        constants.setCourseTutor(qds.getString(CourseDetailsConstants.COURSE_TUTOR));
                                        constants.setCourseDepartment(qds.getString(CourseDetailsConstants.COURSE_DEPARTMENT));
                                        constantsList.add(constants);
                                    }
                                }
                                if(constantsList.size()>0) {
                                    warning.setVisibility(View.INVISIBLE);
                                    emptyMessage.setVisibility(View.INVISIBLE);
                                    Collections.sort(constantsList, ((o1, o2) -> o2.getCourseCode().compareTo(o1.getCourseCode())));
                                    studentCoursesAdapter = new StudentCoursesAdapter(constantsList, StudentCoursesFragment.this);
                                    recyclerView.setAdapter(studentCoursesAdapter);
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

        StudentCoursesViewerFragment studentCoursesViewerFragment = new StudentCoursesViewerFragment();
        studentCoursesViewerFragment.setArguments(bundle);
        changeFragment(studentCoursesViewerFragment);
    }
}