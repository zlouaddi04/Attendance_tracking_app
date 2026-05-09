package com.example.attendance_tracking_app.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.attendance_tracking_app.db.DatabaseHelper;
import com.example.attendance_tracking_app.models.ClassModel;
import com.example.attendance_tracking_app.models.ClassStatsModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ReportsViewModel extends AndroidViewModel{
    private final DatabaseHelper db;
    private final MutableLiveData<List<ClassStatsModel>> classStats   = new MutableLiveData<>();
    private final MutableLiveData<List<ClassStatsModel>> lowestClasses = new MutableLiveData<>();
    private final MutableLiveData<List<String>>          mostAbsent    = new MutableLiveData<>();

    public ReportsViewModel(@NonNull Application application) {
        super(application);
        db = DatabaseHelper.getInstance(application);
        loadStats();
    }

    public LiveData<List<ClassStatsModel>> getClassStats()    { return classStats; }
    public LiveData<List<ClassStatsModel>> getLowestClasses() { return lowestClasses; }
    public LiveData<List<String>>          getMostAbsent()    { return mostAbsent; }

    public void loadStats() {
        List<ClassModel>      classes  = db.getAllClasses();
        List<ClassStatsModel> statList = new ArrayList<>();

        for (ClassModel c : classes) {
            int totalPresent  = db.getTotalPresentForClass(c.getId());
            int totalPossible = c.getStudentCount() * c.getSessionCount();
            statList.add(new ClassStatsModel(
                    c.getClassName(),
                    c.getStudentCount(),
                    c.getStudentCount(),
                    totalPresent,
                    totalPossible
            ));
        }

        // sort by attendance rate ascending for lowest classes
        List<ClassStatsModel> sorted = new ArrayList<>(statList);
        Collections.sort(sorted, (a, b) -> a.getAttendanceRate() - b.getAttendanceRate());

        classStats.setValue(statList);
        lowestClasses.setValue(sorted.subList(0, Math.min(3, sorted.size()))); // bottom 3
        mostAbsent.setValue(db.getMostAbsentStudents(5)); // top 5 most absent
    }
}
