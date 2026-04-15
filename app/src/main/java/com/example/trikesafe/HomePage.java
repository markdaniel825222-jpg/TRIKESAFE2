package com.example.trikesafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class HomePage extends AppCompatActivity {

    private static final String APP_PREFIX = "TSAFE_";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // QR Code Scanner Launcher
    private final ActivityResultLauncher<ScanOptions> qrLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String scannedData = result.getContents();
                    // Validation to ensure it's a TrikeSafe QR
                    if (scannedData.startsWith(APP_PREFIX)) {
                        String cleanID = scannedData.substring(APP_PREFIX.length());
                        Intent intent = new Intent(HomePage.this, DriverInfoActivity.class);
                        intent.putExtra("QR_RESULT", cleanID);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Invalid Code: Not a TrikeSafe QR", Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Request permissions on startup (Camera for QR, SMS for notifications)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.SEND_SMS}, 100);
        }

        // Initialize UI Elements
        ImageButton registerButton = findViewById(R.id.imageButtonRegister);
        ImageButton adminButton = findViewById(R.id.imageButtonAdmin);
        Button scanNowButton = findViewById(R.id.buttonScan);
        ImageButton driverHistoryBtn = findViewById(R.id.imageButtonHistory);
        ImageButton studentHistoryBtn = findViewById(R.id.imageButtonStudentHistory);

        // Navigation Listeners
        registerButton.setOnClickListener(v -> startActivity(new Intent(this, StudentRegistration.class)));
        adminButton.setOnClickListener(v -> startActivity(new Intent(this, AdminLoginPage.class)));

        // QR Scanner Listener
        scanNowButton.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan the Tricycle QR Code");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureAct.class);
            qrLauncher.launch(options);
        });

        // Login Dialog Listeners
        driverHistoryBtn.setOnClickListener(v -> showDriverLoginDialog());
        studentHistoryBtn.setOnClickListener(v -> showStudentLoginDialog());
    }

    /**
     * Dialog for Student Authentication via Firebase
     */
    private void showStudentLoginDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_student_login, null);
        EditText emailInput = dialogView.findViewById(R.id.dialogStudentId); // Used as Email input
        EditText passwordInput = dialogView.findViewById(R.id.dialogStudentPin); // Used as Password input

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("LOGIN", null)
                .setNegativeButton("CANCEL", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Authenticate with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            fetchStudentIdAndOpenHistory(uid, dialog);
                        } else {
                            Toast.makeText(this, "Login Failed: Check credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    /**
     * Fetches the unique Student ID from Firestore profile to filter history logs
     */
    private void fetchStudentIdAndOpenHistory(String uid, AlertDialog dialog) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String idNum = doc.getString("idNumber");
                        // Save ID locally for the session
                        getSharedPreferences("TrikeSafePrefs", MODE_PRIVATE).edit()
                                .putString("loggedInStudentID", idNum).apply();

                        startActivity(new Intent(HomePage.this, StudentHistoryActivity.class));
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show());
    }

    /**
     * Quick login for Drivers using Plate Number and a default password
     */
    private void showDriverLoginDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_driver_login, null);
        EditText plateInput = dialogView.findViewById(R.id.dialogPlate);
        EditText passwordInput = dialogView.findViewById(R.id.dialogPass);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("LOGIN", null)
                .setNegativeButton("CANCEL", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String plate = plateInput.getText().toString().trim().toUpperCase();
            String pass = passwordInput.getText().toString().trim();

            // Simple validation for driver access
            if (!plate.isEmpty() && pass.equals("1234")) {
                getSharedPreferences("TrikeSafePrefs", MODE_PRIVATE).edit()
                        .putString("loggedInPlate", plate).apply();
                startActivity(new Intent(HomePage.this, ViewLoginActivity.class));
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Access Denied: Invalid Plate or PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}