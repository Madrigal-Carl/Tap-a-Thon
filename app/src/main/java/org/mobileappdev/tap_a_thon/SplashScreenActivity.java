package org.mobileappdev.tap_a_thon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SplashScreenActivity extends AppCompatActivity {

    UserPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        pref = new UserPreferences(this);
        Thread mythread = new Thread(() -> {
            try {
                Thread.sleep(3000);

                if (pref.isLoggedIn()){
                    runOnUiThread(() -> Toast.makeText(SplashScreenActivity.this,
                            "Welcome back, " + pref.getUsername(),
                            Toast.LENGTH_SHORT).show());

                    Intent myIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    Intent myIntent = new Intent(SplashScreenActivity.this, SigninActivity.class);
                    startActivity(myIntent);
                    finish();
                }
            } catch (Exception e) {
            }
        });

        mythread.start();
    }
}