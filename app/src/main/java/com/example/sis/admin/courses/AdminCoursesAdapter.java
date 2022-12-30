package com.example.sis.admin.courses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sis.R;
import com.example.sis.admin.requests.AdminRequestsAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class AdminCoursesAdapter extends RecyclerView.Adapter<AdminCoursesAdapter.CoursesHolder> {

    List<CourseDetailsConstants> constantsList;
    AdminCoursesListener adminCoursesListener;

    public AdminCoursesAdapter(List<CourseDetailsConstants> constantsList, AdminCoursesListener adminCoursesListener) {
        this.constantsList = constantsList;
        this.adminCoursesListener = adminCoursesListener;
    }

    @androidx.annotation.NonNull
    @Override
    public CoursesHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.admin_courses_adapter, parent, false);
        return new CoursesHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull CoursesHolder holder, int position) {
        holder.setData(constantsList.get(position));
    }

    public void setVisibility(List<CourseDetailsConstants> filteredList, boolean isVisible) {
        if(!isVisible)
            constantsList.clear();
        constantsList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return constantsList.size();
    }


    class CoursesHolder extends RecyclerView.ViewHolder {

        TextView courseCode, courseName, courseTutor, courseDepartment;
        CardView cardView;

        public CoursesHolder(@NonNull View view) {
            super(view);
            courseCode = (TextView) view.findViewById(R.id.courseCode);
            courseName = (TextView) view.findViewById(R.id.courseName);
            courseTutor = (TextView) view.findViewById(R.id.courseTutor);
            courseDepartment = (TextView) view.findViewById(R.id.courseDepartment);
            cardView = (CardView) view.findViewById(R.id.admin_courses_adapter);
        }

        public void setData(CourseDetailsConstants courseDetailsConstants) {
            courseCode.setText(courseDetailsConstants.getCourseCode());
            courseName.setText(courseDetailsConstants.getCourseName());
            courseDepartment.setText(courseDetailsConstants.getCourseDepartment());
            courseTutor.setText(courseDetailsConstants.getCourseTutor());
            cardView.setOnClickListener(v-> {
                adminCoursesListener.onCoursesClicked(courseDetailsConstants);
            });
        }
    }



}
