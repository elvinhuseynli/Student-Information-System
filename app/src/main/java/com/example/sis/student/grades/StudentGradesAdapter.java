package com.example.sis.student.grades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sis.R;
import com.example.sis.admin.courses.CourseDetailsConstants;
import com.example.sis.student.courses.selected.StudentCoursesAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class StudentGradesAdapter extends RecyclerView.Adapter<StudentGradesAdapter.CoursesHolder> {

    List<CourseDetailsConstants> constantsList;
    StudentGradesListener studentGradesListener;

    public StudentGradesAdapter(List<CourseDetailsConstants> constantsList, StudentGradesListener studentGradesListener) {
        this.constantsList = constantsList;
        this.studentGradesListener = studentGradesListener;
    }

    @androidx.annotation.NonNull
    @Override
    public CoursesHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_grades_adapter, parent, false);
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

        TextView courseCode, courseGrade;
        CardView cardView;

        public CoursesHolder(@NonNull View view) {
            super(view);
            courseCode = (TextView) view.findViewById(R.id.courseCode);
            courseGrade = (TextView) view.findViewById(R.id.courseGrade);
            cardView = (CardView) view.findViewById(R.id.student_grades_adapter);
        }

        public void setData(CourseDetailsConstants courseDetailsConstants) {
            courseCode.setText(courseDetailsConstants.getCourseCode());
            courseGrade.setText(courseDetailsConstants.getCourseGrade());
            cardView.setOnClickListener(v-> {
                studentGradesListener.onCoursesClicked(courseDetailsConstants);
            });
        }
    }
}

