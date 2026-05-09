package com.example.attendance_tracking_app.viewmodels;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.attendance_tracking_app.db.DatabaseHelper;
import com.example.attendance_tracking_app.models.*;
import java.util.List;

public class AttendanceViewModel extends AndroidViewModel{

    private final DatabaseHelper db;
    private final MutableLiveData<List<StudentModel>> students = new MutableLiveData<>();
    private final MutableLiveData<List<AttendanceRecord>> records = new MutableLiveData<>();

    private int sessionId;
    private int classId;

    public AttendanceViewModel(@NonNull Application application) {
        super(application);
        db = DatabaseHelper.getInstance(application);
    }

    public LiveData<List<StudentModel>> getStudents() { return students; }
    public LiveData<List<AttendanceRecord>> getRecords() { return records; }

    public void loadForSession(int sessionId, int classId) {
        this.sessionId = sessionId;
        this.classId   = classId;
        students.setValue(db.getStudentsByClass(classId));
        records.setValue(db.getAttendanceBySession(sessionId));
    }
    // in AttendanceViewModel.java
    public SessionModel getSession(int sessionId) {
        return db.getSessionById(sessionId);
    }

    public void markAttendance(AttendanceRecord record) {
        db.markAttendance(record);
        records.setValue(db.getAttendanceBySession(sessionId));
    }

    public boolean isPresent(int studentId) {
        AttendanceRecord record = db.getAttendanceRecord(sessionId, studentId);
        return record != null && record.isPresent();
    }
}
