package com.example.attendance_tracking_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendance_tracking_app.R;
import com.example.attendance_tracking_app.models.StudentModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>{

    private List<StudentModel> studentList = new ArrayList<>();

    // stores present/absent state for each student id
    private Map<Integer, Boolean> attendanceMap = new HashMap<>();


    private boolean isEditMode = false;

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        notifyDataSetChanged();
    }

    public void updateList(List<StudentModel> students,
                           Map<Integer, Boolean> existingAttendance,
                           boolean editMode) {
        this.studentList = students;
        this.attendanceMap = new HashMap<>(existingAttendance);
        this.isEditMode = editMode;
        notifyDataSetChanged();
    }

    // returns the final attendance map when save is clicked
    public Map<Integer, Boolean> getAttendanceMap() {
        return attendanceMap;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        StudentModel student = studentList.get(position);

        holder.tvName.setText(student.getName());
        holder.tvNumber.setText(student.getStudentNumber());

        // set checkbox state without triggering listener
        holder.cbPresent.setOnCheckedChangeListener(null);
        holder.cbPresent.setChecked(attendanceMap.getOrDefault(student.getId(), false));

        // disable checkbox in view mode, enable in edit mode
        holder.cbPresent.setEnabled(isEditMode);
        holder.cbPresent.setAlpha(isEditMode ? 1f : 0.5f);

        // only save state when checkbox is ticked in edit mode
        holder.cbPresent.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isEditMode)
                attendanceMap.put(student.getId(), isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber;
        CheckBox cbPresent;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName   = itemView.findViewById(R.id.tvStudentName);
            tvNumber = itemView.findViewById(R.id.tvStudentNumber);
            cbPresent = itemView.findViewById(R.id.cbPresent);
        }
    }
}
