package com.example.attendance_tracking_app.models;

public class SessionModel {
    private int id;
    private int classId;
    private String date;
    private String topic;

    public SessionModel(int classId, String date, String topic) {
        this.classId = classId;
        this.date = date;
        this.topic = topic;
    }

    public SessionModel(int id, int classId, String date, String topic) {
        this.id = id;
        this.classId = classId;
        this.date = date;
        this.topic = topic;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

}
