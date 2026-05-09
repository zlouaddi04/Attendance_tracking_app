package com.example.attendance_tracking_app.activities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.attendance_tracking_app.R;
import com.example.attendance_tracking_app.models.ClassStatsModel;
import com.example.attendance_tracking_app.viewmodels.ReportsViewModel;


public class ReportsActivity extends AppCompatActivity {
    private ReportsViewModel viewModel;
    private LinearLayout llClassStats, llMostAbsent, llLowestClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // 1. Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 2. Setup ViewModel
        viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);

        // 3. Get layout containers
        llClassStats    = findViewById(R.id.llClassStats);
        llMostAbsent    = findViewById(R.id.llMostAbsent);
        llLowestClasses = findViewById(R.id.llLowestClasses);

        // 4. Observe and render stats
        viewModel.getClassStats().observe(this, stats -> {
            llClassStats.removeAllViews();
            for (ClassStatsModel s : stats)
                llClassStats.addView(createStatCard(
                        s.getClassName(),
                        s.getAttendanceRate() + "% attendance",
                        s.getTotalStudents() + " students • " + s.getTotalSessions() + " sessions",
                        s.getAttendanceRate()
                ));
        });

        viewModel.getMostAbsent().observe(this, absentList -> {
            llMostAbsent.removeAllViews();
            for (String entry : absentList)
                llMostAbsent.addView(createSimpleCard(entry));
        });

        viewModel.getLowestClasses().observe(this, lowest -> {
            llLowestClasses.removeAllViews();
            for (ClassStatsModel s : lowest)
                llLowestClasses.addView(createStatCard(
                        s.getClassName(),
                        s.getAttendanceRate() + "% attendance",
                        s.getTotalStudents() + " students • " + s.getTotalSessions() + " sessions",
                        s.getAttendanceRate()
                ));
        });
    }

    // card with colored attendance rate
    private View createStatCard(String title, String rate, String subtitle, int attendanceRate) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_stat_card, llClassStats, false);
        ((TextView) card.findViewById(R.id.tvStatTitle)).setText(title);
        ((TextView) card.findViewById(R.id.tvStatSubtitle)).setText(subtitle);
        TextView tvRate = card.findViewById(R.id.tvStatRate);
        tvRate.setText(rate);
        tvRate.setTextColor(attendanceRate >= 75 ? 0xFF4CAF50 : 0xFFFF5252);
        return card;
    }

    // simple card for most absent students
    private View createSimpleCard(String text) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_stat_card, llMostAbsent, false);
        ((TextView) card.findViewById(R.id.tvStatTitle)).setText(text);
        ((TextView) card.findViewById(R.id.tvStatSubtitle)).setVisibility(View.GONE);
        ((TextView) card.findViewById(R.id.tvStatRate)).setVisibility(View.GONE);
        return card;
    }
}
