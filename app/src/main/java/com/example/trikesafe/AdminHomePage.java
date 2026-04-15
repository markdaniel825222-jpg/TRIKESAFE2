package com.example.trikesafe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class AdminHomePage extends AppCompatActivity {

    private ImageButton btnGenerateQR, btnViewUsers, btnHome, btnAdminViewLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_homepage);

        btnGenerateQR    = findViewById(R.id.imageButton2);
        btnViewUsers     = findViewById(R.id.imageButton7);
        btnHome          = findViewById(R.id.imageButton3);
        btnAdminViewLogs = findViewById(R.id.btnAdminViewLogs);

        // Standard Navigation
        btnGenerateQR.setOnClickListener(v ->
                startActivity(new Intent(AdminHomePage.this, QRGeneratorPage.class)));

        // Goes to the selection screen (Students or Drivers)
        btnViewUsers.setOnClickListener(v ->
                startActivity(new Intent(AdminHomePage.this, UserTypeSelectionActivity.class)));

        btnAdminViewLogs.setOnClickListener(v -> {
            getSharedPreferences("TrikeSafePrefs", MODE_PRIVATE)
                    .edit()
                    .putString("loggedInPlate", "ADMIN_ALL")
                    .apply();

            startActivity(new Intent(AdminHomePage.this, ViewLoginActivity.class));
        });

        btnHome.setOnClickListener(v -> finish());
    }
}