package com.example.sis.admin.courses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sis.R;
import com.example.sis.admin.requests.AdminRequestsAdapter;
import com.example.sis.admin.requests.AdminRequestsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AdminCoursesFragment extends Fragment implements AdminCoursesListener {

    RecyclerView recyclerView;
    ImageView warning, addButton;
    TextView emptyMessage;
    SearchView searchView;
    ProgressBar progressBar;
    List<CourseDetailsConstants> constantsList;
    AdminCoursesAdapter adminCoursesAdapter;
    FirebaseFirestore database;

    public AdminCoursesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_courses, container, false);

        initializeViews(view);
        setListeners();

        retrieveDataFromDatabase();

        return view;
    }

    public void initializeViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        warning = (ImageView) view.findViewById(R.id.warning);
        addButton = (ImageView) view.findViewById(R.id.addButton);
        emptyMessage = (TextView) view.findViewById(R.id.emptyMessage);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        constantsList = new ArrayList<>();
        searchView = (SearchView) view.findViewById(R.id.search_bar);
        database = FirebaseFirestore.getInstance();
    }

    public void setListeners() {
        addButton.setOnClickListener(v-> {
            AdminCoursesAdditionFragment fragment = new AdminCoursesAdditionFragment();
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
                            adminCoursesAdapter.setVisibility(filteredList, isResumed());
                            Toast.makeText(getActivity(), "Unable to find any result", Toast.LENGTH_SHORT).show();
                        } else{}
                            adminCoursesAdapter.setVisibility(filteredList, isResumed());
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
                    for(QueryDocumentSnapshot doc: task.getResult()) {
                        CourseDetailsConstants constants = new CourseDetailsConstants();
                        constants.setCourseCode(doc.getString(CourseDetailsConstants.COURSE_CODE));
                        constants.setCourseName(doc.getString(CourseDetailsConstants.COURSE_NAME));
                        constants.setCourseTutor(doc.getString(CourseDetailsConstants.COURSE_TUTOR));
                        constants.setCourseDepartment(doc.getString(CourseDetailsConstants.COURSE_DEPARTMENT));
                        constantsList.add(constants);
                    }
                    warning.setVisibility(View.INVISIBLE);
                    emptyMessage.setVisibility(View.INVISIBLE);
                    Collections.sort(constantsList, ((o1, o2) -> o2.getCourseCode().compareTo(o1.getCourseCode())));
                    adminCoursesAdapter = new AdminCoursesAdapter(constantsList, AdminCoursesFragment.this);
                    recyclerView.setAdapter(adminCoursesAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
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
        fragmentTransaction.replace(R.id.adminFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCoursesClicked(CourseDetailsConstants courseDetailsConstants) {
        Bundle bundle = new Bundle();
        bundle.putString("courseCode", courseDetailsConstants.getCourseCode());

        AdminCoursesViewerFragment adminCoursesViewerFragment = new AdminCoursesViewerFragment();
        adminCoursesViewerFragment.setArguments(bundle);
        changeFragment(adminCoursesViewerFragment);
    }
}