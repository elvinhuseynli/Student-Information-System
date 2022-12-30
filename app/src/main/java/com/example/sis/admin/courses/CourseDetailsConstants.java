package com.example.sis.admin.courses;

import java.util.List;

public class CourseDetailsConstants {
    public static String COURSE_NAME="courseName", COURSE_CODE="courseCode", COURSE_TUTOR="courseTutor", COURSE_DEPARTMENT="courseDepartment";
    public static String COURSE_DAY="courseDay", COURSE_START_TIME="courseStartTime", COURSE_END_TIME="courseEndTime";
    public static String COURSE_SYLLABUS="courseSyllabus", COURSE_ECTS="courseEcts", COURSE_MIDTERM="midtermPercentage", COURSE_FINAL="finalPercentage";

    public String courseName;
    public String courseCode;
    public String courseTutor;
    public String courseDepartment;
    public String courseGrade;

    public String getCourseGrade() {
        return courseGrade;
    }

    public void setCourseGrade(String courseGrade) {
        this.courseGrade = courseGrade;
    }

    public static List<String> courseList;

    public static List<String> getCourseList() {
        return courseList;
    }

    public static void setCourseList(List<String> courseList1) {
        courseList = courseList1;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTutor() {
        return courseTutor;
    }

    public void setCourseTutor(String courseTutor) {
        this.courseTutor = courseTutor;
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }
}
