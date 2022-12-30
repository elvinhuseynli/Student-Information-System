package com.example.sis.teacher.courses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sis.R;
import com.example.sis.admin.courses.CourseDetailsConstants;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class TeacherCoursesAdapter extends RecyclerView.Adapter<TeacherCoursesAdapter.CoursesHolder> {

    List<CourseDetailsConstants> constantsList;
    TeacherCoursesListener teacherCoursesListener;

    public TeacherCoursesAdapter(List<CourseDetailsConstants> constantsList, TeacherCoursesListener teacherCoursesListener) {
        this.constantsList = constantsList;
        this.teacherCoursesListener = teacherCoursesListener;
    }

    @androidx.annotation.NonNull
    @Override
    public CoursesHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.teacher_courses_adapter, parent, false);
        return new CoursesHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull CoursesHolder holder, int position) {
        holder.setData(constantsList.get(position));
    }

    @Override
    public int getItemCount() {
        return constantsList.size();
    }

    public void setVisibility(List<CourseDetailsConstants> filteredList, boolean isVisible) {
        if(!isVisible)
            constantsList.clear();
        constantsList = filteredList;
        notifyDataSetChanged();
    }

    class CoursesHolder extends RecyclerView.ViewHolder {

        TextView courseCode, courseName, courseDepartment;
        ImageView editButton;

        public CoursesHolder(@NonNull View view) {
            super(view);
            courseCode = (TextView) view.findViewById(R.id.courseCode);
            courseName = (TextView) view.findViewById(R.id.courseName);
            courseDepartment = (TextView) view.findViewById(R.id.courseDepartment);
            editButton = (ImageView) view.findViewById(R.id.editButton);
        }

        public void setData(CourseDetailsConstants courseDetailsConstants) {
            courseCode.setText(courseDetailsConstants.getCourseCode());
            courseName.setText(courseDetailsConstants.getCourseName());
            courseDepartment.setText(courseDetailsConstants.getCourseDepartment());
            editButton.setOnClickListener(v-> {
                teacherCoursesListener.onEditButtonClicked(courseDetailsConstants);
            });
        }
    }
}
