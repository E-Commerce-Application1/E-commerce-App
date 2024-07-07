package com.example.e_commerceapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText signupEmail, signupPassword, signupConfirmPassword;
    private Button signupButton;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupConfirmPassword = findViewById(R.id.signupConfirmPassword);
        signupButton = findViewById(R.id.signupButton);

        SQLiteOpenHelper helper = new DatabaseHelper(this);
        db = helper.getWritableDatabase();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String confirmPassword = signupConfirmPassword.getText().toString();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);

        long newRowId = db.insert("users", null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Signup failed, please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
