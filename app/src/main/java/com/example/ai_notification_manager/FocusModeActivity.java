package com.example.ai_notification_manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ai_notification_manager.utils.FocusModeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class FocusModeActivity extends AppCompatActivity {

    private FocusModeManager focusManager;
    private SwitchMaterial switchFocus;
    private TextView tvFocusStatus, tvTimer, tvActiveProfile, tvReceivedCount, tvAllowedCount, tvDeferredCount;
    private MaterialButton btn25, btn45, btn60;

    private int selectedDurationMins = 25;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimerDisplay();
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        focusManager = new FocusModeManager(this);

        initViews();
        setupListeners();
        updateUIState();
    }

    private void initViews() {
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        switchFocus = findViewById(R.id.switchFocus);
        tvFocusStatus = findViewById(R.id.tvFocusStatus);
        tvTimer = findViewById(R.id.tvTimer);
        tvActiveProfile = findViewById(R.id.tvActiveProfile);
        tvReceivedCount = findViewById(R.id.tvReceivedCount);
        tvAllowedCount = findViewById(R.id.tvAllowedCount);
        tvDeferredCount = findViewById(R.id.tvDeferredCount);

        btn25 = findViewById(R.id.btn25);
        btn45 = findViewById(R.id.btn45);
        btn60 = findViewById(R.id.btn60);
    }

    private void setupListeners() {
        switchFocus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                focusManager.startSession("Deep Work", selectedDurationMins);
                Toast.makeText(this, "Focus Mode started (" + selectedDurationMins + " mins)", Toast.LENGTH_SHORT).show();
                timerHandler.post(timerRunnable);
            } else {
                focusManager.stopSession();
                Toast.makeText(this, "Focus Mode stopped", Toast.LENGTH_SHORT).show();
                timerHandler.removeCallbacks(timerRunnable);
            }
            updateUIState();
        });

        btn25.setOnClickListener(v -> {
            selectedDurationMins = 25;
            tvTimer.setText("25:00");
            Toast.makeText(this, "Duration set to 25 mins", Toast.LENGTH_SHORT).show();
        });
        btn45.setOnClickListener(v -> {
            selectedDurationMins = 45;
            tvTimer.setText("45:00");
            Toast.makeText(this, "Duration set to 45 mins", Toast.LENGTH_SHORT).show();
        });
        btn60.setOnClickListener(v -> {
            selectedDurationMins = 60;
            tvTimer.setText("60:00");
            Toast.makeText(this, "Duration set to 60 mins", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUIState() {
        boolean active = focusManager.isSessionActive();
        switchFocus.setChecked(active);
        if (active) {
            tvFocusStatus.setText("Focus Mode is ACTIVE (Deep Work)");
            timerHandler.post(timerRunnable);
        } else {
            tvFocusStatus.setText("Focus Mode is OFF");
            long totalMillis = selectedDurationMins * 60L * 1000L;
            tvTimer.setText(formatMillis(totalMillis));
            timerHandler.removeCallbacks(timerRunnable);
        }

        tvReceivedCount.setText(String.valueOf(focusManager.getReceivedCount()));
        tvAllowedCount.setText(String.valueOf(focusManager.getAllowedCount()));
        tvDeferredCount.setText(String.valueOf(focusManager.getDeferredCount()));
    }

    private void updateTimerDisplay() {
        long remaining = focusManager.getRemainingMillis();
        tvTimer.setText(formatMillis(remaining));
        if (remaining <= 0 && focusManager.isSessionActive()) {
            focusManager.stopSession();
            updateUIState();
            Toast.makeText(this, "Focus session completed!", Toast.LENGTH_LONG).show();
        }
    }

    private String formatMillis(long millis) {
        long seconds = millis / 1000;
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIState();
        if (focusManager.isSessionActive()) {
            timerHandler.post(timerRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }
}
