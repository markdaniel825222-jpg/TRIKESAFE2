package com.example.trikesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Added for back button
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginPage extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ImageButton btnBack; // Added this variable
    private Animation clickAnim; // Added for the shrink effect

    // Hardcoded admin account
    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);

        // Bind views
        etUsername = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin   = findViewById(R.id.button2);
        btnBack    = findViewById(R.id.btnBack); // Match XML id

        // Load the shrink animation
        clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);

        // Back Button Logic
        btnBack.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            finish(); // Goes back to the previous screen
        });

        // Login Button Logic
        btnLogin.setOnClickListener(v -> {
            // Add shrink animation for feedback
            v.startAnimation(clickAnim);

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(AdminLoginPage.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
                // Login successful → go to AdminHomePage
                // Fixed the extra space in the class path below
                Intent intent = new Intent(AdminLoginPage.this, AdminHomePage.class);
                startActivity(intent);
                finish();
            } else {
                // Optional: Clear password if wrong
                etPassword.setText("");
                Toast.makeText(AdminLoginPage.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}