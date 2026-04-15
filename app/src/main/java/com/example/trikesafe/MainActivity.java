package com.example.trikesafe;

import android.content.Intent;
import android.content.SharedPreferences; // Import this for saving the choice
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getStartedButton = findViewById(R.id.button);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Access the saved preference to see if they agreed before
                SharedPreferences prefs = getSharedPreferences("TrikeSafePrefs", MODE_PRIVATE);
                boolean hasAgreed = prefs.getBoolean("hasAgreedToTerms", false);

                if (hasAgreed) {
                    // 2. If they ALREADY agreed, go to HomePage
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    startActivity(intent);
                } else {
                    // 3. If they HAVE NOT agreed, go to TermsActivity
                    Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                    startActivity(intent);
                }

                // Optional: Close MainActivity so they can't go back to the splash screen
                finish();
            }
        });
    }
}