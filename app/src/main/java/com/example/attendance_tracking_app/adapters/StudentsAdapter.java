package com.example.attendance_tracking_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.attendance_tracking_app.R;
import com.example.attendance_tracking_app.db.DatabaseHelper;
import com.example.attendance_tracking_app.models.StudentModel;
import java.util.ArrayList;
import java.util.List;
public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {
    public interface OnStudentClickListener {
        void onStudentClick(StudentModel student);
    }

    public interface OnStudentLongClickListener {
        boolean onStudentLongClick(StudentModel student);
    }

    private List<StudentModel> studentList = new ArrayList<>();
    private final OnStudentClickListener onClickListener;
    private final OnStudentLongClickListener onLongClickListener;
    private final DatabaseHelper db;
    private final int classId;

    public StudentsAdapter(Context context,
                           int classId,
                           OnStudentClickListener onClickListener,
                           OnStudentLongClickListener onLongClickListener) {
        this.db = DatabaseHelper.getInstance(context);
        this.classId = classId;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public void updateList(List<StudentModel> newList) {
        this.studentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = studentList.get(position);

        holder.tvName.setText(student.getName());
        holder.tvNumber.setText(student.getStudentNumber());

        // Calculate attendance rate
        int totalSessions = db.getSessionsByClass(classId).size();
        int presentCount  = db.getStudentPresentCount(student.getId(), classId);

        if (totalSessions > 0) {
            int rate = (int) ((presentCount / (float) totalSessions) * 100);
            holder.tvRate.setText(rate + "%");
            holder.tvRate.setTextColor(rate >= 75 ? 0xFF4CAF50 : 0xFFFF5252);
        } else {
            holder.tvRate.setText("N/A");
            holder.tvRate.setTextColor(0xFFAAAAAA);
        }

        holder.itemView.setOnClickListener(v -> onClickListener.onStudentClick(student));
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onStudentLongClick(student));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber, tvRate;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName   = itemView.findViewById(R.id.tvStudentName);
            tvNumber = itemView.findViewById(R.id.tvStudentNumber);
            tvRate   = itemView.findViewById(R.id.tvAttendanceRate);
        }
    }
}
