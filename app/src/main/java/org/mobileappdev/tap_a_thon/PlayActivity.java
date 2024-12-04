package org.mobileappdev.tap_a_thon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    TextView timer, taps, totalTapsText;
    DatabaseHelper db;
    UserPreferences pref;
    int tapCount = 0;

    private static final long START_TIME_IN_MILLIS = 18000;
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        db = new DatabaseHelper(this);
        pref = new UserPreferences(this);

        timer = findViewById(R.id.timer);
        taps = findViewById(R.id.tap);
        totalTapsText = findViewById(R.id.total_taps);

        startTimer();

        ConstraintLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (timerRunning) {
                        addTap();
                    }
                }
                return true;
            }
        });
    }

    private void addTap() {
        tapCount++;
        taps.setText(String.valueOf(tapCount));
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                timer.setText(String.format("%02d", secondsRemaining));
            }

            @Override
            public void onFinish() {
                timer.setText("00");
                timerRunning = false;

                timer.setVisibility(View.GONE);
                taps.setVisibility(View.GONE);

                findViewById(R.id.timesUpContainer).setVisibility(View.VISIBLE);
                totalTapsText.setText(String.valueOf(tapCount));

                pref.updateHighestScore(tapCount);
                db.insertScore(pref.getUserId(), tapCount);
                db.updateLeaderboard(pref.getUserId(), tapCount);
            }
        };

        countDownTimer.start();
        timerRunning = true;
    }

    public void onRetryClicked(View view) {
        tapCount = 0;
        taps.setText(String.valueOf(tapCount));
        timer.setVisibility(View.VISIBLE);
        taps.setVisibility(View.VISIBLE);
        findViewById(R.id.timesUpContainer).setVisibility(View.GONE);

        startTimer();
    }

    public void onMenuClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
