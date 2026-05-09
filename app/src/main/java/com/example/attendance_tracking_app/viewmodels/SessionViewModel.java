package com.example.attendance_tracking_app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.attendance_tracking_app.db.DatabaseHelper;
import com.example.attendance_tracking_app.models.AttendanceRecord;
import com.example.attendance_tracking_app.models.SessionModel;
import com.example.attendance_tracking_app.models.StudentModel;
import java.util.List;
public class SessionViewModel extends AndroidViewModel{

    private final DatabaseHelper db;
    private final MutableLiveData<List<SessionModel>> sessions = new MutableLiveData<>();
    private final MutableLiveData<List<StudentModel>> students = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private int classId;

    public SessionViewModel(@NonNull Application application) {
        super(application);
        db = DatabaseHelper.getInstance(application);
    }

    public LiveData<List<SessionModel>> getSessions() { return sessions; }
    public LiveData<List<StudentModel>> getStudents() { return students; }
    public LiveData<String> getError() { return error; }

    public void loadForClass(int classId) {
        this.classId = classId;
        sessions.setValue(db.getSessionsByClass(classId));
        students.setValue(db.getStudentsByClass(classId));
    }

    // in SessionViewModel.java
    public boolean isStudentPresent(int studentId, int sessionId) {
        AttendanceRecord record = db.getAttendanceRecord(sessionId, studentId);
        return record != null && record.isPresent();
    }

    public void addSession(SessionModel s) {
        db.addSession(s);
        sessions.setValue(db.getSessionsByClass(classId));
    }

    public void addStudent(StudentModel s) {
        long result = db.addStudent(s);
        if (result == -1)
            error.setValue("A student with this number already exists in this class");
        else
            students.setValue(db.getStudentsByClass(classId));
    }

    public void deleteSession(int sessionId) {
        db.deleteSession(sessionId);
        sessions.setValue(db.getSessionsByClass(classId));
    }

    public void deleteStudent(int studentId) {
        db.deleteStudent(studentId);
        students.setValue(db.getStudentsByClass(classId));
    }
}
