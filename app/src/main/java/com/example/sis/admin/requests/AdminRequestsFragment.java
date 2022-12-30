package com.example.sis.admin.requests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.example.sis.mainact.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminRequestsFragment extends Fragment implements AdminRequestsListener {

    RecyclerView recyclerView;
    ImageView warning;
    TextView emptyMessage;
    ProgressBar progressBar;
    List<AuthenticationConstants> authenticationConstantsList;
    AdminRequestsAdapter adminRequestsAdapter;
    SearchView searchView;
    FirebaseFirestore database;

    public AdminRequestsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_requests, container, false);

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
        searchView = (SearchView) view.findViewById(R.id.search_bar);
        authenticationConstantsList = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
    }

    public void setListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!searchView.isIconified()) {
                    List<AuthenticationConstants> filteredList = new ArrayList<>();
                    if(authenticationConstantsList.size()>0) {
                        for (int i = 0; i < authenticationConstantsList.size(); i++) {
                            if ((authenticationConstantsList.get(i).getFullName().toLowerCase().contains(newText.toLowerCase()) ||
                                    authenticationConstantsList.get(i).getStudentId().toLowerCase().contains(newText.toLowerCase()) ||
                                    authenticationConstantsList.get(i).getDepartment().toLowerCase().contains(newText.toLowerCase()))) {
                                filteredList.add(authenticationConstantsList.get(i));
                            }
                        }
                        if (filteredList.isEmpty()) {
                            adminRequestsAdapter.setVisibility(filteredList, isResumed());
                            Toast.makeText(getActivity(), "Unable to find any result", Toast.LENGTH_SHORT).show();
                        } else{}
                        adminRequestsAdapter.setVisibility(filteredList, isResumed());
                    }
                }
                return false;
            }
        });
    }

    public void retrieveDataFromDatabase() {
        Query query = database.collection("students")
                .whereEqualTo(Constants.REGISTRATION_ACCEPTED, "false");
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                if(task.getResult().size() > 0) {
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        AuthenticationConstants authenticationConstants = new AuthenticationConstants();
                        authenticationConstants.setStudentId(doc.getString(Constants.STUDENT_ID));
                        authenticationConstants.setFullName(doc.getString(Constants.FIRST_NAME)+" "+doc.getString(Constants.LAST_NAME));
                        authenticationConstants.setDepartment(doc.getString(Constants.DEPARTMENT));
                        authenticationConstants.setTimestamp(doc.getDate(Constants.TIMESTAMP));
                        authenticationConstants.setRegistrationDate(doc.getString(Constants.REGISTRATION_DATE));
                        authenticationConstantsList.add(authenticationConstants);
                    }
                    warning.setVisibility(View.INVISIBLE);
                    emptyMessage.setVisibility(View.INVISIBLE);
                    Collections.sort(authenticationConstantsList, ((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp())));
                    adminRequestsAdapter = new AdminRequestsAdapter(authenticationConstantsList, AdminRequestsFragment.this);
                    recyclerView.setAdapter(adminRequestsAdapter);
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
    public void onRequestClicked(AuthenticationConstants authenticationConstants) {
        Bundle bundle = new Bundle();
        bundle.putString("studentId", authenticationConstants.getStudentId());

        AdminRequestViewerFragment adminRequestViewerFragment = new AdminRequestViewerFragment();
        adminRequestViewerFragment.setArguments(bundle);
        changeFragment(adminRequestViewerFragment);
    }
}