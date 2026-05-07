package com.example.attendance_tracking_app.models;

public class ClassModel {
    private int id;
    private String className;
    private int studentCount;
    private int sessionCount;

    public ClassModel(String className, int studentCount, int sessionCount) {
        this.className = className;
        this.studentCount = studentCount;
        this.sessionCount = sessionCount;
    }
    public ClassModel(int id, String name, int totalStudents, int totalSessions) {
        this.id = id;
        this.className = name;
        this.studentCount = totalStudents;
        this.studentCount = totalSessions;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getClassName() { return className; }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getStudentCount() { return studentCount; }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public int getSessionCount() { return sessionCount; }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }
}
