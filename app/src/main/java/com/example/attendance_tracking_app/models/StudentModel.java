package com.example.attendance_tracking_app.models;

public class StudentModel {
    private int id;
    private int classId;
    private String name;
    private String studentNumber;

    public StudentModel(int classId, String name, String studentNumber) {
        this.classId = classId;
        this.name = name;
        this.studentNumber = studentNumber;
    }

    public StudentModel(int id, int classId, String name, String studentNumber) {
        this.id = id;
        this.classId = classId;
        this.name = name;
        this.studentNumber = studentNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String s) { this.studentNumber = s; }

}
