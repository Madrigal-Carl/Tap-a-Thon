package org.mobileappdev.tap_a_thon;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_HIGHEST_SCORE = "highest_score";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Constructor
    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user data
    public void saveUserData(int userId, String username, int highestScore, boolean isLoggedIn) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_HIGHEST_SCORE, highestScore);
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Set username
    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // Set highest score
    public void setHighestScore(int highestScore) {
        editor.putInt(KEY_HIGHEST_SCORE, highestScore);
        editor.apply();
    }

    // Set login status
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Get User ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Get Username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // Get Highest Score
    public int getHighestScore() {
        return sharedPreferences.getInt(KEY_HIGHEST_SCORE, 0);
    }

    // Get login status
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Update Highest Score
    public void updateHighestScore(int newScore) {
        int currentHighest = getHighestScore();
        if (newScore > currentHighest) {
            editor.putInt(KEY_HIGHEST_SCORE, newScore);
            editor.apply();
        }
    }

    // Clear user data
    public void clearUserData() {
        editor.clear();
        editor.apply();
    }
}
