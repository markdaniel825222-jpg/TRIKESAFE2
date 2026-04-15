package com.example.trikesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TrikeSafePrefs";
    private static final String IS_AGREED_KEY = "hasAgreedToTerms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Button btnAgree = findViewById(R.id.btnAgree);
        Button btnDecline = findViewById(R.id.btnDecline);

        // Action when 'I Agree' is clicked
        btnAgree.setOnClickListener(v -> {
            // Save the agreement state in SharedPreferences
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            prefs.edit().putBoolean(IS_AGREED_KEY, true).apply();

            Toast.makeText(this, "Consent recorded. Welcome to TrikeSafe!", Toast.LENGTH_SHORT).show();

            // Navigate to the HomePage
            Intent intent = new Intent(TermsActivity.this, HomePage.class);
            startActivity(intent);

            // Finish this activity so the user cannot go back to it
            finish();
        });

        // Action when 'Decline' is clicked
        btnDecline.setOnClickListener(v -> {
            // Close the application entirely
            Toast.makeText(this, "You must agree to use the application.", Toast.LENGTH_LONG).show();
            finishAffinity();
        });
    }

    // Disable the back button to prevent skipping the terms
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please Agree or Decline to continue.", Toast.LENGTH_SHORT).show();
    }
}