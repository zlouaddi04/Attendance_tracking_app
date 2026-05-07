package com.example.attendance_tracking_app.models;

public class AttendanceRecord {
    private int id;
    private int sessionId;
    private int studentId;
    private boolean isPresent;

    public AttendanceRecord(int sessionId, int studentId, boolean isPresent) {
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.isPresent = isPresent;
    }

    public AttendanceRecord(int id, int sessionId, int studentId, boolean isPresent) {
        this.id = id;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.isPresent = isPresent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }

}
