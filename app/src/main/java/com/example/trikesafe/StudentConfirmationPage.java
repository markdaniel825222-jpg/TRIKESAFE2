package com.example.trikesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StudentConfirmationPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentthanks); // Ensure correct XML file name

        // Find the Home button
        Button homeButton = findViewById(R.id.button);

        // Set click listener for Home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to HomePage
                Intent intent = new Intent(StudentConfirmationPage.this, HomePage.class);
                startActivity(intent);
                finish(); // Close current activity to prevent stacking
            }
        });
    }
}
