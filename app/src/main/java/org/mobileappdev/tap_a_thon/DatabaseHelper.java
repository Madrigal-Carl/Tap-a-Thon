package org.mobileappdev.tap_a_thon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "Tap-a-Thon.db";
    private static final int DATABASE_VERSION = 1;

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";

    // Scores Table
    private static final String TABLE_SCORES = "scores";
    private static final String COLUMN_SCORE_ID = "score_id";
    private static final String COLUMN_SCORE_USER_ID = "user_id";
    private static final String COLUMN_SCORE_POINTS = "score_points";
    private static final String COLUMN_SCORE_DATE = "score_date";

    // Leaderboard Table
    private static final String TABLE_LEADERBOARD = "leaderboard";
    private static final String COLUMN_LEADERBOARD_ID = "leaderboard_id";
    private static final String COLUMN_LEADERBOARD_USER_ID = "user_id";
    private static final String COLUMN_LEADERBOARD_POINTS = "leaderboard_points";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Users table
        String userTableQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s VARCHAR(30) NOT NULL UNIQUE, " +
                        "%s VARCHAR(30) NOT NULL) ",
                TABLE_USERS, COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_USER_PASSWORD
        );
        db.execSQL(userTableQuery);

        // Create the Scores table
        String scoresTableQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "%s DATE DEFAULT (CURRENT_DATE), " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE) ",
                TABLE_SCORES, COLUMN_SCORE_ID, COLUMN_SCORE_USER_ID, COLUMN_SCORE_POINTS, COLUMN_SCORE_DATE,
                COLUMN_SCORE_USER_ID, TABLE_USERS, COLUMN_USER_ID
        );
        db.execSQL(scoresTableQuery);

        // Create the Leaderboard table
        String leaderboardTableQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL UNIQUE, " +
                        "%s INTEGER NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                TABLE_LEADERBOARD, COLUMN_LEADERBOARD_ID,
                COLUMN_LEADERBOARD_USER_ID, COLUMN_LEADERBOARD_POINTS,
                COLUMN_LEADERBOARD_USER_ID, TABLE_USERS, COLUMN_USER_ID
        );
        db.execSQL(leaderboardTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_NAME, username);
            values.put(COLUMN_USER_PASSWORD, password);

            long result = db.insert(TABLE_USERS, null, values);

            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean userAuth(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format(
                "SELECT %s, %s FROM %s WHERE %s = ? AND %s = ?",
                COLUMN_USER_ID, COLUMN_USER_NAME, TABLE_USERS, COLUMN_USER_NAME, COLUMN_USER_PASSWORD
        );

        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String retrievedUsername = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
            int highestScore = getHighestScore(userId);

            UserPreferences userPreferences = new UserPreferences(context);
            userPreferences.saveUserData(userId, retrievedUsername, highestScore, true);

            cursor.close();
            db.close();
            return true;
        }

        cursor.close();
        db.close();
        return false;
    }

    public int getHighestScore(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format(
                "SELECT MAX(%s) FROM %s WHERE %s = ?",
                COLUMN_SCORE_POINTS, TABLE_SCORES, COLUMN_SCORE_USER_ID
        );

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        int highestScore = 0;

        if (cursor.moveToFirst()) {
            highestScore = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return highestScore;
    }

    public void insertScore(int userId, int points) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SCORE_USER_ID, userId);
            values.put(COLUMN_SCORE_POINTS, points);
            values.put(COLUMN_SCORE_DATE, "CURRENT_DATE");

            db.insert(TABLE_SCORES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void updateLeaderboard(int userId, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            // Check if the user already exists in the leaderboard
            String checkQuery = String.format(
                    "SELECT %s FROM %s WHERE %s = ?",
                    COLUMN_LEADERBOARD_POINTS, TABLE_LEADERBOARD, COLUMN_LEADERBOARD_USER_ID
            );

            cursor = db.rawQuery(checkQuery, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                // User exists, update their score if the new score is higher
                int currentLeaderboardScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEADERBOARD_POINTS));

                if (newScore > currentLeaderboardScore) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_LEADERBOARD_POINTS, newScore);

                    db.update(
                            TABLE_LEADERBOARD,
                            values,
                            COLUMN_LEADERBOARD_USER_ID + " = ?",
                            new String[]{String.valueOf(userId)}
                    );
                }
            } else {
                // User does not exist, add them to the leaderboard
                ContentValues values = new ContentValues();
                values.put(COLUMN_LEADERBOARD_USER_ID, userId);
                values.put(COLUMN_LEADERBOARD_POINTS, newScore);

                db.insert(TABLE_LEADERBOARD, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    public Cursor getAllLeaderboardData() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = String.format(
                "SELECT u.%s AS username, l.%s AS leaderboard_points " +
                        "FROM %s l " +
                        "INNER JOIN %s u ON l.%s = u.%s " +
                        "ORDER BY l.%s DESC " +
                        "LIMIT 10",
                COLUMN_USER_NAME, COLUMN_LEADERBOARD_POINTS,
                TABLE_LEADERBOARD, TABLE_USERS,
                COLUMN_LEADERBOARD_USER_ID, COLUMN_USER_ID,
                COLUMN_LEADERBOARD_POINTS
        );

        return db.rawQuery(query, null);
    }

    public boolean checkUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                COLUMN_USER_ID, TABLE_USERS, COLUMN_USER_NAME
        );

        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean userExists = cursor.moveToFirst();

        cursor.close();
        db.close();

        return userExists;
    }

    public boolean resetPassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_PASSWORD, newPassword);

            int rowsUpdated = db.update(
                    TABLE_USERS,
                    values,
                    COLUMN_USER_NAME + " = ?",
                    new String[]{username}
            );

            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }


}
