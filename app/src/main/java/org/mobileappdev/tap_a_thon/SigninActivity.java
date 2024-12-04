package org.mobileappdev.tap_a_thon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{

    EditText username, password;
    TextView signup, forgot_password;
    Button signin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.reset);
        forgot_password = findViewById(R.id.forgot_password);

        signin.setOnClickListener(this);
        signup.setOnClickListener(this);
        forgot_password.setOnClickListener(this);

        db = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                Intent signupIntent = new Intent(this, SignupActivity.class);
                startActivity(signupIntent);
                finish();
                break;

            case R.id.forgot_password:
                Intent forgotPassIntent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(forgotPassIntent);
                finish();
                break;

            case R.id.signin:
                userAuth();
                break;
        }
    }

    public void userAuth() {
        String name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (name.isEmpty() && pass.isEmpty()) {
            username.setError("Input your username");
            password.setError("Input your password");
            return;
        } else if (name.isEmpty()) {
            username.setError("Input your username");
            return;
        } else if (pass.isEmpty()) {
            password.setError("Input your password");
            return;
        }

        if (db.userAuth(name, pass)) {
            UserPreferences pref = new UserPreferences(this);
            Toast.makeText(this, String.format("Welcome %s", pref.getUsername()), Toast.LENGTH_SHORT).show();
            Intent toMain = new Intent(this, MainActivity.class);
            startActivity(toMain);
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
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