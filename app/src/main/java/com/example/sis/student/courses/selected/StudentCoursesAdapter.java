package com.example.sis.student.courses.selected;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sis.R;
import com.example.sis.admin.courses.AdminCoursesAdapter;
import com.example.sis.admin.courses.CourseDetailsConstants;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class StudentCoursesAdapter extends RecyclerView.Adapter<StudentCoursesAdapter.CoursesHolder> {

    List<CourseDetailsConstants> constantsList;
    StudentCoursesListener studentCoursesListener;

    public StudentCoursesAdapter(List<CourseDetailsConstants> constantsList, StudentCoursesListener studentCoursesListener) {
        this.constantsList = constantsList;
        this.studentCoursesListener = studentCoursesListener;
    }


    @androidx.annotation.NonNull
    @Override
    public CoursesHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_courses_adapter, parent, false);
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
            cardView = (CardView) view.findViewById(R.id.student_courses_adapter);
        }

        public void setData(CourseDetailsConstants courseDetailsConstants) {
            courseCode.setText(courseDetailsConstants.getCourseCode());
            courseName.setText(courseDetailsConstants.getCourseName());
            courseDepartment.setText(courseDetailsConstants.getCourseDepartment());
            courseTutor.setText(courseDetailsConstants.getCourseTutor());
            cardView.setOnClickListener(v-> {
                studentCoursesListener.onCoursesClicked(courseDetailsConstants);
            });
        }
    }
}
