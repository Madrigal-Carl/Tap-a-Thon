package org.mobileappdev.tap_a_thon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username, password, confirm_password;
    Button signup;
    TextView signin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.reset);

        signin.setOnClickListener(this);
        signup.setOnClickListener(this);

        db = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                addUser();
                break;

            case R.id.signin:
                Intent signinIntent = new Intent(this, SigninActivity.class);
                startActivity(signinIntent);
                finish();
                break;
        }
    }

    private void addUser() {
        String name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String c_pass = confirm_password.getText().toString().trim();

        if (name.isEmpty() || pass.isEmpty() || c_pass.isEmpty()) {
            if (name.isEmpty()) {
                username.setError("Input your username");
            }
            if (pass.isEmpty()) {
                password.setError("Input your password");
            }
            if (c_pass.isEmpty()) {
                confirm_password.setError("Confirm your password");
            }
            return;
        }

        if (!pass.equals(c_pass)) {
            confirm_password.setError("Passwords do not match");
            return;
        }

        if (name.length() < 4) {
            username.setError("Username must be at least 4 characters long");
            return;
        } else if (name.length() > 8) {
            username.setError("Username must not exceed 8 characters");
            return;
        } else if (name.contains(" ")) {
            username.setError("Username must not contain spaces");
            return;
        }

        if (pass.length() < 8) {
            password.setError("Password must be at least 8 characters");
            return;
        } else if (pass.contains(" ")) {
            password.setError("Password must not contain spaces");
            return;
        }

        if (db.addUser(name, pass)) {
            Toast.makeText(this, "Account has been Created", Toast.LENGTH_SHORT).show();
            Intent signinIntent = new Intent(this, SigninActivity.class);
            startActivity(signinIntent);
            finish();
        } else {
            Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}