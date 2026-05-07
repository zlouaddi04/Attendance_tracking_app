package com.example.attendance_tracking_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance_tracking_app.R;
import com.example.attendance_tracking_app.models.ClassModel;

import java.util.ArrayList;
import java.util.List;
public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassViewHolder>{
    public interface OnClassClickListener {
        void onClassClick(ClassModel classModel);
    }

    public interface OnClassLongClickListener {
        boolean onClassLongClick(ClassModel classModel);
    }

    private List<ClassModel> classList = new ArrayList<>();
    private final OnClassClickListener onClickListener;
    private final OnClassLongClickListener onLongClickListener;

    public ClassesAdapter(OnClassClickListener onClickListener,
                          OnClassLongClickListener onLongClickListener) {
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public void updateList(List<ClassModel> newList) {
        this.classList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassModel currentClass = classList.get(position);

        holder.tvName.setText(currentClass.getClassName());

        String details = currentClass.getStudentCount() + " students • " +
                currentClass.getSessionCount() + " sessions";
        holder.tvDetails.setText(details);

        holder.itemView.setOnClickListener(v -> onClickListener.onClassClick(currentClass));
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onClassLongClick(currentClass));
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvClassName);
            tvDetails = itemView.findViewById(R.id.tvClassDetails);
        }
    }
}

