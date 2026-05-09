package com.example.attendance_tracking_app.models;

public class ClassStatsModel {

    private String className;
    private int totalStudents;
    private int totalSessions;
    private int totalPresent;
    private int totalPossible;

    public ClassStatsModel(String className, int totalStudents,
                           int totalSessions, int totalPresent, int totalPossible) {
        this.className     = className;
        this.totalStudents = totalStudents;
        this.totalSessions = totalSessions;
        this.totalPresent  = totalPresent;
        this.totalPossible = totalPossible;
    }

    public String getClassName()    { return className; }
    public int getTotalStudents()   { return totalStudents; }
    public int getTotalSessions()   { return totalSessions; }
    public int getTotalPresent()    { return totalPresent; }
    public int getTotalPossible()   { return totalPossible; }

    public int getAttendanceRate() {
        if (totalPossible == 0) return 0;
        return (int) ((totalPresent / (float) totalPossible) * 100);
    }
}
