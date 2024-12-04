package org.mobileappdev.tap_a_thon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener  {

    EditText username, password, confirm_password;
    Button reset, back;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        reset = findViewById(R.id.reset);
        back = findViewById(R.id.back);

        back.setOnClickListener(this);
        reset.setOnClickListener(this);

        db = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                if (!validation()){
                    return;
                }

                if (checkUser()){
                    resetPassword();
                } else {
                    Toast.makeText(this, "No username found.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.back:
                Intent signinIntent = new Intent(this, SigninActivity.class);
                startActivity(signinIntent);
                finish();
                break;
        }
    }

    private boolean validation() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String cpass = confirm_password.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
            if (user.isEmpty()) {
                username.setError("Input your username");
            }
            if (pass.isEmpty()) {
                password.setError("Input your password");
            }
            if (cpass.isEmpty()) {
                confirm_password.setError("Confirm your password");
            }
            return false;
        }

        if (!pass.equals(cpass)) {
            confirm_password.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean checkUser() {
        String user = username.getText().toString().trim();

        if (db.checkUser(user)){
            return true;
        }
        return false;
    }

    private void resetPassword() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Are you sure you want to reset the password?")
                .setPositiveButton("Continue", (dialog, which) -> {

                    if (db.resetPassword(user, pass)){
                        Toast.makeText(this, "Password has been reset", Toast.LENGTH_SHORT).show();
                    }

                    Intent signinIntent = new Intent(this, SigninActivity.class);
                    startActivity(signinIntent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}