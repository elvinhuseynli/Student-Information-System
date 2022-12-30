package com.example.sis.teacher.courses;

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
import com.example.sis.admin.courses.AdminCoursesAdapter;
import com.example.sis.admin.courses.AdminCoursesAdditionFragment;
import com.example.sis.admin.courses.AdminCoursesFragment;
import com.example.sis.admin.courses.CourseDetailsConstants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeacherCoursesFragment extends Fragment implements TeacherCoursesListener {

    RecyclerView recyclerView;
    ImageView warning;
    TextView emptyMessage;
    String tutorId;
    SearchView searchView;
    ProgressBar progressBar;
    List<CourseDetailsConstants> constantsList;
    TeacherCoursesAdapter teacherCoursesAdapter;
    FirebaseFirestore database;

    public TeacherCoursesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_courses, container, false);

        Intent intent = getActivity().getIntent();

        Bundle bundle = intent.getExtras();
        tutorId = bundle.getString("idNumber");

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
                            if ((constantsList.get(i).getCourseCode().toLowerCase().contains(newText.toLowerCase()) ||
                                    constantsList.get(i).getCourseName().toLowerCase().contains(newText.toLowerCase()) ||
                                    constantsList.get(i).getCourseDepartment().toLowerCase().contains(newText.toLowerCase()))) {
                                filteredList.add(constantsList.get(i));
                            }
                        }
                        if (filteredList.isEmpty()) {
                            teacherCoursesAdapter.setVisibility(filteredList, isResumed());
                            Toast.makeText(getActivity(), "Unable to find any result", Toast.LENGTH_SHORT).show();
                        } else{}
                        teacherCoursesAdapter.setVisibility(filteredList, isResumed());
                    }
                }
                return false;
            }
        });
    }

    public void retrieveDataFromDatabase() {
        Query query = database.collection("courses");
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size()>0) {
                    for(DocumentSnapshot doc: task.getResult()) {
                        if(doc.getString("tutorId").equals(tutorId)) {
                            CourseDetailsConstants constants = new CourseDetailsConstants();
                            constants.setCourseCode(doc.getString(CourseDetailsConstants.COURSE_CODE));
                            constants.setCourseName(doc.getString(CourseDetailsConstants.COURSE_NAME));
                            constants.setCourseDepartment(doc.getString(CourseDetailsConstants.COURSE_DEPARTMENT));
                            constantsList.add(constants);
                        }
                    }
                    if(constantsList.size()>0) {
                        warning.setVisibility(View.INVISIBLE);
                        emptyMessage.setVisibility(View.INVISIBLE);
                        Collections.sort(constantsList, ((o1, o2) -> o2.getCourseCode().compareTo(o1.getCourseCode())));
                        teacherCoursesAdapter = new TeacherCoursesAdapter(constantsList, TeacherCoursesFragment.this);
                        recyclerView.setAdapter(teacherCoursesAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        warning.setVisibility(View.VISIBLE);
                        emptyMessage.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
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
        fragmentTransaction.replace(R.id.teacherFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onEditButtonClicked(CourseDetailsConstants courseDetailsConstants) {
        Bundle bundle = new Bundle();
        bundle.putString("courseCode", courseDetailsConstants.getCourseCode());

        TeacherCoursesViewerFragment teacherCoursesViewerFragment = new TeacherCoursesViewerFragment();
        teacherCoursesViewerFragment.setArguments(bundle);
        changeFragment(teacherCoursesViewerFragment);
    }
}