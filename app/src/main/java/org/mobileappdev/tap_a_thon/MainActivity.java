package org.mobileappdev.tap_a_thon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView username, high_score;
    Button start, leaderboard, exit;
    UserPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        high_score = findViewById(R.id.high_score);
        start = findViewById(R.id.start);
        leaderboard = findViewById(R.id.leaderboard);
        exit = findViewById(R.id.exit);

        start.setOnClickListener(this);
        leaderboard.setOnClickListener(this);
        exit.setOnClickListener(this);

        pref = new UserPreferences(this);
        username.setText(pref.getUsername());
        high_score.setText(String.valueOf(pref.getHighestScore()));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tap-a-Thon");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E5733F")));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                Intent playIntent = new Intent(this, PlayActivity.class);
                startActivity(playIntent);
                finish();
                break;

            case R.id.leaderboard:
                Intent leaderboardIntent = new Intent(this, LeaderboardActivity.class);
                startActivity(leaderboardIntent);
                finish();
                break;

            case R.id.exit:
                new AlertDialog.Builder(this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            finishAffinity();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about_id) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.developers_id) {
            Intent intent = new Intent(this, DevelopersActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.log_out_id) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        pref.clearUserData();
                        Intent intent = new Intent(this, SigninActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void exitByBackKey() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the application?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        exitByBackKey();
        super.onBackPressed();
    }
}