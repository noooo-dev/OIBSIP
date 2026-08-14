package com.example.stopwatch;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView timeDisplay;
    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Button lapButton;

    private boolean running = false;
    private long elapsedBeforeStart = 0L;
    private long startTimestamp = 0L;

    private final android.os.Handler handler = new android.os.Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                updateDisplay();
                handler.postDelayed(this, 50);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeDisplay = findViewById(R.id.timeDisplay);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        lapButton = findViewById(R.id.lapButton);

        startButton.setOnClickListener(v -> startStopwatch());
        pauseButton.setOnClickListener(v -> pauseStopwatch());
        resetButton.setOnClickListener(v -> resetStopwatch());
        lapButton.setOnClickListener(v -> recordLap());

        updateButtonStates();
        updateDisplay();
    }

    private void startStopwatch() {
        if (running) return;

        startTimestamp = SystemClock.elapsedRealtime() - elapsedBeforeStart;
        running = true;

        updateButtonStates();
        handler.removeCallbacks(timerRunnable);
        handler.post(timerRunnable);
    }

    private void pauseStopwatch() {
        if (!running) return;

        elapsedBeforeStart = SystemClock.elapsedRealtime() - startTimestamp;
        running = false;

        handler.removeCallbacks(timerRunnable);
        updateDisplay();
        updateButtonStates();
    }

    private void resetStopwatch() {
        running = false;
        elapsedBeforeStart = 0L;
        startTimestamp = 0L;

        handler.removeCallbacks(timerRunnable);
        updateDisplay();
        updateButtonStates();
    }

    private void recordLap() {
        if (!running) return;

        long elapsed = SystemClock.elapsedRealtime() - startTimestamp;
        String lapTime = formatTime(elapsed);

        TextView lapEntry = new TextView(this);
        lapEntry.setText(lapTime);
        lapEntry.setTextSize(18);
        lapEntry.setTextColor(getResources().getColor(android.R.color.white));
        lapEntry.setPadding(16, 12, 16, 12);

        android.widget.LinearLayout lapContainer = findViewById(R.id.lapContainer);

        int lapNumber = lapContainer.getChildCount() + 1;
        lapEntry.setText(String.format(Locale.getDefault(),
                "Lap %d    %s", lapNumber, lapTime));

        lapContainer.addView(lapEntry, 0);
    }

    private void updateDisplay() {
        long elapsed = running
                ? SystemClock.elapsedRealtime() - startTimestamp
                : elapsedBeforeStart;

        timeDisplay.setText(formatTime(elapsed));
    }

    private String formatTime(long elapsedMillis) {
        long totalSeconds = elapsedMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateButtonStates() {
        startButton.setEnabled(!running);
        pauseButton.setEnabled(running);
        lapButton.setEnabled(running);

        startButton.setAlpha(running ? 0.45f : 1.0f);
        pauseButton.setAlpha(running ? 1.0f : 0.45f);
        lapButton.setAlpha(running ? 1.0f : 0.45f);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (running) {
            elapsedBeforeStart = SystemClock.elapsedRealtime() - startTimestamp;
            handler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (running) {
            startTimestamp = SystemClock.elapsedRealtime() - elapsedBeforeStart;
            handler.removeCallbacks(timerRunnable);
            handler.post(timerRunnable);
        }

        updateDisplay();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }
}
