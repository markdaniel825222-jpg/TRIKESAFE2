package com.example.trikesafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class DriverInfoActivity extends AppCompatActivity {

    private TextView tvDriverInfo;
    private Button btnNotifyGuardian;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String guardianNumber = "";
    private String qrData = "";
    private String studentFullName = "Unknown Student";
    private String studentIdFromProfile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driverinfo);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        tvDriverInfo = findViewById(R.id.tvDriverInfo);
        btnNotifyGuardian = findViewById(R.id.btnNotifyGuardian);
        progressBar = findViewById(R.id.progressBar);

        qrData = getIntent().getStringExtra("QR_RESULT");
        tvDriverInfo.setText(qrData != null ? qrData : "No data found.");

        fetchStudentAndGuardianInfo();

        btnNotifyGuardian.setOnClickListener(v -> checkSmsPermissionAndSend());
    }

    private void fetchStudentAndGuardianInfo() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            progressBar.setVisibility(View.VISIBLE);
            db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                progressBar.setVisibility(View.GONE);
                if (doc.exists()) {
                    guardianNumber = doc.getString("parentNumber");
                    studentFullName = doc.getString("firstName") + " " + doc.getString("lastName");
                    studentIdFromProfile = doc.getString("idNumber");
                }
                btnNotifyGuardian.setEnabled(true);
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error fetching profile", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveRideLog() {
        Map<String, Object> rideLog = new HashMap<>();
        rideLog.put("idNumber", studentIdFromProfile);
        rideLog.put("studentName", studentFullName);
        rideLog.put("timestamp", com.google.firebase.Timestamp.now());

        // Field for Admin and Student History to parse name
        rideLog.put("driverData", qrData != null ? qrData : "No Driver Data Found");

        String tempPlate = "N/A";
        if (qrData != null) {
            try {
                String[] lines = qrData.split("\n");
                for (String line : lines) {
                    if (line.contains("Plate:")) {
                        tempPlate = line.replace("Plate:", "").trim().toUpperCase();
                    }
                }
            } catch (Exception e) {
                Log.e("DB_LOG", "Parsing error", e);
            }
        }
        rideLog.put("plateNumber", tempPlate);

        db.collection("ride_logs").add(rideLog)
                .addOnSuccessListener(ref -> {
                    // UPDATED: Redirect to SafeTripActivity instead of StudentConfirmationPage
                    Intent intent = new Intent(DriverInfoActivity.this, SafeTripActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Log Failed", Toast.LENGTH_SHORT).show());
    }

    private void checkSmsPermissionAndSend() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 101);
        } else {
            sendAlertSms();
        }
    }

    private void sendAlertSms() {
        try {
            SmsManager sms = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ?
                    this.getSystemService(SmsManager.class) : SmsManager.getDefault();

            if (guardianNumber != null && !guardianNumber.isEmpty()) {
                sms.sendTextMessage(guardianNumber, null, "TRIKE SAFE: I am riding a trike. " + qrData, null, null);
                saveRideLog();
                Toast.makeText(this, "Guardian Notified!", Toast.LENGTH_LONG).show();
            } else {
                showNotRegisteredDialog();
            }
        } catch (Exception e) {
            Toast.makeText(this, "SMS Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotRegisteredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Profile Missing").setMessage("Complete registration first.")
                .setPositiveButton("Register", (d, w) -> startActivity(new Intent(this, StudentRegistration.class)))
                .setNegativeButton("Cancel", null).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) sendAlertSms();
    }
}