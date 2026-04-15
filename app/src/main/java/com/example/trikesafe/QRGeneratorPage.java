package com.example.trikesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class QRGeneratorPage extends AppCompatActivity {

    private EditText etFirstName, etMiddleName, etLastName, etPlateNo, etEmergencyNo;
    private Button btnGenerate;
    private ImageButton btnHome;
    private FirebaseFirestore db;

    // THE SECRET KEY: Must match the one in HomePage.java
    private static final String APP_PREFIX = "TSAFE_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrgeneratorpage);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        etFirstName = findViewById(R.id.editTextText2_firstname);
        etMiddleName = findViewById(R.id.editTextText2_middlename);
        etLastName = findViewById(R.id.editTextText2_lastname);
        etPlateNo = findViewById(R.id.editTextNumber_plate);
        etEmergencyNo = findViewById(R.id.editTextNumber_emergency);
        btnGenerate = findViewById(R.id.button);
        btnHome = findViewById(R.id.imageButton5);

        // Set Click Listeners
        btnGenerate.setOnClickListener(v -> saveDriverToFirebase());
        btnHome.setOnClickListener(v -> finish());
    }

    private void saveDriverToFirebase() {
        String fName = etFirstName.getText().toString().trim();
        String mName = etMiddleName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();
        String plate = etPlateNo.getText().toString().trim();
        String emergency = etEmergencyNo.getText().toString().trim();

        // 1. Basic validation for empty fields
        if (fName.isEmpty() || lName.isEmpty() || plate.isEmpty() || emergency.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Specific Validation: Check if exactly 11 digits
        if (emergency.length() != 11) {
            etEmergencyNo.setError("Number is invalid");
            etEmergencyNo.requestFocus();
            return;
        }

        // 3. Prepare data for Firestore
        Map<String, Object> driver = new HashMap<>();
        driver.put("firstName", fName);
        driver.put("middleName", mName);
        driver.put("lastName", lName);
        driver.put("plateNumber", plate);
        driver.put("emergencyContact", emergency);
        driver.put("timestamp", com.google.firebase.Timestamp.now());

        // Save to Firestore using plate number as the unique document ID
        db.collection("drivers").document(plate)
                .set(driver)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(QRGeneratorPage.this, "Driver Recorded!", Toast.LENGTH_SHORT).show();
                    openQRDisplayPage(fName, lName, plate, emergency);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(QRGeneratorPage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openQRDisplayPage(String fName, String lName, String plate, String emergency) {
        // Create the string that will be encoded into the QR code
        String rawData = "Name: " + fName + " " + lName + "\n" +
                "Plate: " + plate + "\n" +
                "Emergency: " + emergency;

        // Attach the security prefix
        String secureData = APP_PREFIX + rawData;

        // Pass the data to the next Activity
        Intent intent = new Intent(QRGeneratorPage.this, QRdisplay.class);
        intent.putExtra("qr_data", secureData);
        startActivity(intent);
    }
}