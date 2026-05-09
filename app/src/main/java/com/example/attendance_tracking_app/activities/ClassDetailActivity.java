package com.example.attendance_tracking_app.activities;

import android.content.Intent;
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
import com.example.attendance_tracking_app.adapters.SessionsAdapter;
import com.example.attendance_tracking_app.models.SessionModel;
import com.example.attendance_tracking_app.viewmodels.SessionViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class ClassDetailActivity extends AppCompatActivity {
    private SessionViewModel viewModel;
    private SessionsAdapter adapter;
    private int classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        // 1. Get data passed from MainActivity
        classId   = getIntent().getIntExtra("CLASS_ID", -1);
        className = getIntent().getStringExtra("CLASS_NAME");

        // 2. Setup header
        TextView tvClassName = findViewById(R.id.tvClassName);
        tvClassName.setText(className);

        // 3. Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 4. Setup ViewModel
        viewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        viewModel.loadForClass(classId);

        // 5. Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.sessionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 6. Setup adapter with click listeners
        adapter = new SessionsAdapter(
                this,
                session -> {
                    // tap → open SessionActivity
                    Intent intent = new Intent(this, SessionActivity.class);
                    intent.putExtra("SESSION_ID", session.getId());
                    intent.putExtra("CLASS_ID",   classId);
                    startActivity(intent);
                },
                session -> {
                    // long press → delete confirmation
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Session")
                            .setMessage("Delete this session?")
                            .setPositiveButton("Delete", (d, w) -> viewModel.deleteSession(session.getId()))
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
        );
        recyclerView.setAdapter(adapter);

        // 7. Observe sessions LiveData
        viewModel.getSessions().observe(this, sessions -> {
            adapter.updateList(sessions);
            TextView tvStats = findViewById(R.id.tvClassStats);
            tvStats.setText(sessions.size() + " sessions");
        });

        // 8. Observe errors
        viewModel.getError().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show());

        // 9. Students button
        Button btnStudents = findViewById(R.id.btnStudents);
        btnStudents.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentsActivity.class);
            intent.putExtra("CLASS_ID",   classId);
            intent.putExtra("CLASS_NAME", className);
            startActivity(intent);
        });

        // 10. New session button
        Button btnNewSession = findViewById(R.id.btnNewSession);
        btnNewSession.setOnClickListener(v -> showAddSessionDialog());
    }

    private void showAddSessionDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_session, null);
        EditText etTopic = dialogView.findViewById(R.id.etTopic);

        new AlertDialog.Builder(this)
                .setTitle("New Session")
                .setView(dialogView)
                .setPositiveButton("Add", (d, w) -> {
                    String topic = etTopic.getText().toString().trim();
                    String date  = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(new Date());
                    viewModel.addSession(new SessionModel(classId, date, topic));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
