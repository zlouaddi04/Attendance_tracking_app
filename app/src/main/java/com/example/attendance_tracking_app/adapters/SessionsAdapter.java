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
import com.example.attendance_tracking_app.models.SessionModel;
import java.util.ArrayList;
import java.util.List;
public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionViewHolder>{
    public interface OnSessionClickListener {
        void onSessionClick(SessionModel session);
    }

    public interface OnSessionLongClickListener {
        boolean onSessionLongClick(SessionModel session);
    }

    private List<SessionModel> sessionList = new ArrayList<>();
    private final OnSessionClickListener onClickListener;
    private final OnSessionLongClickListener onLongClickListener;
    private final DatabaseHelper db;

    public SessionsAdapter(Context context,
                           OnSessionClickListener onClickListener,
                           OnSessionLongClickListener onLongClickListener) {
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        this.db = DatabaseHelper.getInstance(context);
    }

    public void updateList(List<SessionModel> newList) {
        this.sessionList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionModel session = sessionList.get(position);

        holder.tvDate.setText(session.getDate());
        holder.tvTopic.setText(session.getTopic());

        // Show how many students were present
        int presentCount = db.getPresentCountForSession(session.getId());
        holder.tvAttendance.setText(presentCount + " present");

        holder.itemView.setOnClickListener(v -> onClickListener.onSessionClick(session));
        holder.itemView.setOnLongClickListener(v -> onLongClickListener.onSessionLongClick(session));
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTopic, tvAttendance;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate       = itemView.findViewById(R.id.tvSessionDate);
            tvTopic      = itemView.findViewById(R.id.tvSessionTopic);
            tvAttendance = itemView.findViewById(R.id.tvSessionAttendance);
        }
    }
}
