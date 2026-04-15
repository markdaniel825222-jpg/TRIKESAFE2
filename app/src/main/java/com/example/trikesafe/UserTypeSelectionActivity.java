package com.example.trikesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class UserTypeSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);

        final Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);

        ImageButton btnBack = findViewById(R.id.btnBackSelection);
        btnBack.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            finish();
        });

        findViewById(R.id.btnViewDrivers).setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            openList("driver");
        });

        findViewById(R.id.btnViewStudents).setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            openList("student");
        });
    }

    private void openList(String type) {
        Intent intent;
        if (type.equalsIgnoreCase("driver")) {
            // Points to the Driver Activity
            intent = new Intent(this, ViewDriversActivity.class);
        } else {
            // Points to the Student Activity
            intent = new Intent(this, ViewUsersActivity.class);
        }
        intent.putExtra("USER_TYPE", type);
        startActivity(intent);
    }
}