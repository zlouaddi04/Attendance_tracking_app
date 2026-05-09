package com.example.attendance_tracking_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendance_tracking_app.R;
import com.example.attendance_tracking_app.adapters.AttendanceAdapter;
import com.example.attendance_tracking_app.models.AttendanceRecord;
import com.example.attendance_tracking_app.models.SessionModel;
import com.example.attendance_tracking_app.viewmodels.AttendanceViewModel;
import java.util.HashMap;
import java.util.Map;

public class SessionActivity extends AppCompatActivity {
    private AttendanceViewModel viewModel;
    private AttendanceAdapter adapter;
    private Button btnEdit, btnSave;
    private int sessionId;
    private int classId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        // 1. Get data passed from ClassDetailActivity
        sessionId = getIntent().getIntExtra("SESSION_ID", -1);
        classId   = getIntent().getIntExtra("CLASS_ID", -1);

        // 2. Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 3. Setup ViewModel
        viewModel = new ViewModelProvider(this).get(AttendanceViewModel.class);
        viewModel.loadForSession(sessionId, classId);

        // 4. Setup header
        TextView tvDate  = findViewById(R.id.tvSessionDate);
        TextView tvTopic = findViewById(R.id.tvSessionTopic);
        SessionModel session = viewModel.getSession(sessionId);
        if (session != null) {
            tvDate.setText(session.getDate());
            tvTopic.setText(session.getTopic());
        }

        // 5. Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.attendanceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter();
        recyclerView.setAdapter(adapter);

        // 6. Observe students and existing records
        viewModel.getStudents().observe(this, students -> {
            Map<Integer, Boolean> existingAttendance = new HashMap<>();
            if (viewModel.getRecords().getValue() != null) {
                viewModel.getRecords().getValue().forEach(record ->
                        existingAttendance.put(record.getStudentId(), record.isPresent()));
            }
            adapter.updateList(students, existingAttendance, isEditMode);
            updatePresentCount();
        });

        // 7. Edit button — switch to edit mode
        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);

        btnEdit.setOnClickListener(v -> {
            isEditMode = true;
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            adapter.setEditMode(true);
        });

        // 8. Save button — update existing records in DB
        btnSave.setOnClickListener(v -> saveAttendance());
    }

    private void saveAttendance() {
        Map<Integer, Boolean> attendanceMap = adapter.getAttendanceMap();
        attendanceMap.forEach((studentId, isPresent) ->
                viewModel.markAttendance(new AttendanceRecord(sessionId, studentId, isPresent)));

        // switch back to view mode
        isEditMode = false;
        btnSave.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
        adapter.setEditMode(false);

        updatePresentCount();
        Toast.makeText(this, "Attendance updated", Toast.LENGTH_SHORT).show();
    }

    private void updatePresentCount() {
        Map<Integer, Boolean> map = adapter.getAttendanceMap();
        int presentCount = (int) map.values().stream().filter(v -> v).count();
        TextView tvPresentCount = findViewById(R.id.tvPresentCount);
        tvPresentCount.setText(presentCount + " / " + map.size() + " present");
    }

}
