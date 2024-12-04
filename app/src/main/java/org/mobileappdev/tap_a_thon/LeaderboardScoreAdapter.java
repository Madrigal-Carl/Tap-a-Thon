package org.mobileappdev.tap_a_thon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Custom adapter for RecyclerView to display leaderboard scores
public class LeaderboardScoreAdapter extends RecyclerView.Adapter<LeaderboardScoreAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<String> usernames, scores;

    // Constructor to initialize context, activity, and data lists
    public LeaderboardScoreAdapter(Activity activity, Context context, ArrayList<String> usernames, ArrayList<String> scores) {
        this.activity = activity;
        this.context = context;
        this.usernames = usernames;
        this.scores = scores;
    }

    // Inflates the layout for each item in the RecyclerView
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leaderboard_score_item, parent, false);
        return new MyViewHolder(view);
    }

    // Binds data to views in each item of the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Set placement (position + 1)
        holder.placementText.setText(String.valueOf(position + 1));

        // Set username and score
        holder.usernameText.setText(usernames.get(position));
        holder.scoreText.setText(scores.get(position));
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return usernames.size();
    }

    // ViewHolder class to cache views for each item in the RecyclerView
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView placementText, usernameText, scoreText;
        CardView cardView;

        // Initializes views from the layout
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placementText = itemView.findViewById(R.id.placement_text); // New view for placement
            usernameText = itemView.findViewById(R.id.username_text);
            scoreText = itemView.findViewById(R.id.score_text);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}
