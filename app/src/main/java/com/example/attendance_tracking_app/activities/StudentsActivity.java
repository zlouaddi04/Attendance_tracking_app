package com.example.attendance_tracking_app.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendance_tracking_app.R;
import com.example.attendance_tracking_app.adapters.StudentsAdapter;
import com.example.attendance_tracking_app.models.StudentModel;
import com.example.attendance_tracking_app.viewmodels.SessionViewModel;
public class StudentsActivity extends AppCompatActivity {

    private SessionViewModel viewModel;
    private StudentsAdapter adapter;
    private int classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        // 1. Get data passed from ClassDetailActivity
        classId   = getIntent().getIntExtra("CLASS_ID", -1);
        className = getIntent().getStringExtra("CLASS_NAME");

        // 2. Setup header
        TextView tvClassName = findViewById(R.id.tvClassName);
        tvClassName.setText(className);

        // 3. Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 4. Setup ViewModel — same one as ClassDetailActivity
        viewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        viewModel.loadForClass(classId);

        // 5. Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.studentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 6. Setup adapter with click listeners
        adapter = new StudentsAdapter(
                this,
                classId,
                student -> showAttendanceDialog(student),  // tap → show attendance history
                student -> {
                    // long press → delete confirmation
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Student")
                            .setMessage("Are you sure you want to delete " + student.getName() + "?")
                            .setPositiveButton("Delete", (d, w) -> viewModel.deleteStudent(student.getId()))
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
        );
        recyclerView.setAdapter(adapter);

        // 7. Observe students LiveData
        viewModel.getStudents().observe(this, students -> {
            adapter.updateList(students);
            TextView tvStats = findViewById(R.id.tvStudentCount);
            tvStats.setText(students.size() + " students");
        });

        // 8. Update adapter when sessions change so attendance rate stays accurate
        viewModel.getSessions().observe(this, sessions ->
                adapter.setTotalSessions(sessions.size()));

        // 9. Observe errors
        viewModel.getError().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show());

        // 10. New student button
        Button btnNewStudent = findViewById(R.id.btnNewStudent);
        btnNewStudent.setOnClickListener(v -> showAddStudentDialog());
    }

    private void showAttendanceDialog(StudentModel student) {
        // Get attendance records for this student
        StringBuilder sb = new StringBuilder();
        viewModel.getSessions().getValue().forEach(session -> {
            boolean present = viewModel.isStudentPresent(student.getId(), session.getId());
            sb.append(session.getDate())
                    .append(" — ")
                    .append(present ? "✓ Present" : "✗ Absent")
                    .append("\n");
        });

        String records = sb.length() > 0 ? sb.toString() : "No sessions yet";

        new AlertDialog.Builder(this)
                .setTitle(student.getName())
                .setMessage(records)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showAddStudentDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_student, null);
        EditText etName   = dialogView.findViewById(R.id.etStudentName);
        EditText etNumber = dialogView.findViewById(R.id.etStudentNumber);

        new AlertDialog.Builder(this)
                .setTitle("New Student")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String name   = etName.getText().toString().trim();
                    String number = etNumber.getText().toString().trim();
                    if (name.isEmpty() || number.isEmpty()) {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    viewModel.addStudent(new StudentModel(classId, name, number));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
