package com.example.trikesafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SafeTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Change 'activity_safe_trip' to whatever you named your new XML file
        setContentView(R.layout.activity_safe_trip);

        Button btnDone = findViewById(R.id.button);

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(SafeTripActivity.this, HomePage.class);
            // Flags clear the stack so the user can't "Back" into the confirmation
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}