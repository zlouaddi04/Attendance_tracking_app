package com.example.attendance_tracking_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance_tracking_app.adapters.ClassesAdapter;
import com.example.attendance_tracking_app.models.ClassModel;
import com.example.attendance_tracking_app.viewmodels.ClassViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClassesAdapter adapter;
    private List<ClassModel> classList;

    private ClassViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Setup ViewModel
        viewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        // 2. Setup RecyclerView
        recyclerView = findViewById(R.id.classesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Setup Adapter with click listeners
        adapter = new ClassesAdapter(
                classModel -> {
                    Intent intent = new Intent(this, ClassDetailActivity.class);
                    intent.putExtra("CLASS_ID",   classModel.getId());
                    intent.putExtra("CLASS_NAME", classModel.getClassName());
                    startActivity(intent);
                },
                classModel -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Delete Class")
                            .setMessage("Are you sure you want to delete " + classModel.getClassName() + "?")
                            .setPositiveButton("Delete", (d, w) -> viewModel.deleteClass(classModel.getId()))
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                }
        );
        recyclerView.setAdapter(adapter);

        // 4. Observe LiveData
        viewModel.getClasses().observe(this, classes -> adapter.updateList(classes));
        viewModel.getError().observe(this, error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());

        // 5. Bottom nav
        findViewById(R.id.bottomNav).findViewById(R.id.btnStats).setOnClickListener(v ->
                startActivity(new Intent(this, ReportsActivity.class)));

        //findViewById(R.id.btnSettings).setOnClickListener(v ->
        //       startActivity(new Intent(this, SettingsActivity.class)));

        // 6. New Class button
        Button btnNewClass = findViewById(R.id.btnNewClass);
        btnNewClass.setOnClickListener(v -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        EditText input = new EditText(this);
        input.setHint("Class name");
        input.setTextColor(0xFFFFFFFF);
        input.setHintTextColor(0xFFAAAAAA);
        new AlertDialog.Builder(this)
                .setTitle("New Class")
                .setView(input)
                .setPositiveButton("Add", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    viewModel.addClass(new ClassModel(name, 0, 0));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}