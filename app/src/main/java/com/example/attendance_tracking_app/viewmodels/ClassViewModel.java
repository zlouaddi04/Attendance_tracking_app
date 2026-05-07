package com.example.attendance_tracking_app.viewmodels;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.attendance_tracking_app.db.DatabaseHelper;
import com.example.attendance_tracking_app.models.ClassModel;
import java.util.List;
public class ClassViewModel extends AndroidViewModel {
    private final DatabaseHelper db;
    private final MutableLiveData<List<ClassModel>> classes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ClassViewModel(@NonNull Application application) {
        super(application);
        db = DatabaseHelper.getInstance(application);
        loadClasses();
    }

    public LiveData<List<ClassModel>> getClasses() { return classes; }
    public LiveData<String> getError() { return error; }

    public void loadClasses() {
        classes.setValue(db.getAllClasses());
    }

    public void addClass(ClassModel c) {
        long result = db.addClass(c);
        if (result == -1)
            error.setValue("A class with this name already exists");
        else
            loadClasses(); // refresh the list
    }

    public void deleteClass(int classId) {
        db.deleteClass(classId);
        loadClasses();
    }
}
