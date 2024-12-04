package org.mobileappdev.tap_a_thon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private ArrayList<String> usernames;
    private ArrayList<String> scores;
    private ArrayList<String> placements;
    Button back;
    LeaderboardScoreAdapter leaderboardScoreAdapter;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back);
        db = new DatabaseHelper(this);

        usernames = new ArrayList<>();
        scores = new ArrayList<>();
        placements = new ArrayList<>();

        // Initialize the adapter before fetching data
        leaderboardScoreAdapter = new LeaderboardScoreAdapter(this, this, usernames, scores);
        recyclerView.setAdapter(leaderboardScoreAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        displayLeaderboard();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void displayLeaderboard() {
        usernames.clear();
        scores.clear();

        Cursor cursor = db.getAllLeaderboardData();

        if (cursor.getCount() == 0) {
            // Handle empty leaderboard case
        } else {
            int placement = 1;
            while (cursor.moveToNext()) {
                usernames.add(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                scores.add(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("leaderboard_points"))));
                placements.add(String.valueOf(placement));
                placement++;
            }

            leaderboardScoreAdapter.notifyDataSetChanged();
        }

        cursor.close();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
