package com.example.trikesafe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {
    private EditText etFirst, etMiddle, etLast, etID, etParent;
    private Button btnUpdate;
    private FirebaseFirestore db;
    private String userId, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        db = FirebaseFirestore.getInstance();
        etFirst = findViewById(R.id.etFirstName);
        etMiddle = findViewById(R.id.etMiddleName);
        etLast = findViewById(R.id.etLastName);
        etID = findViewById(R.id.etSpecificID);
        etParent = findViewById(R.id.etParentNumber);
        btnUpdate = findViewById(R.id.btnUpdate);

        userId = getIntent().getStringExtra("USER_ID");
        userType = getIntent().getStringExtra("USER_TYPE");

        etFirst.setText(getIntent().getStringExtra("FIRST_NAME"));
        etMiddle.setText(getIntent().getStringExtra("MIDDLE_NAME"));
        etLast.setText(getIntent().getStringExtra("LAST_NAME"));
        etID.setText(getIntent().getStringExtra("SPECIFIC_ID"));
        etParent.setText(getIntent().getStringExtra("PARENT_NUMBER"));

        // Logic: Hide Guardian field for Drivers
        if (userType != null && userType.equalsIgnoreCase("driver")) {
            etParent.setVisibility(View.GONE);
            etID.setHint("Plate Number");
        } else {
            etParent.setVisibility(View.VISIBLE);
            etID.setHint("Student ID");
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updateData());
    }

    private void updateData() {
        String f = etFirst.getText().toString().trim();
        String m = etMiddle.getText().toString().trim();
        String l = etLast.getText().toString().trim();
        String idVal = etID.getText().toString().trim();
        String pNum = etParent.getText().toString().trim();

        if (f.isEmpty() || l.isEmpty() || idVal.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String collection = (userType != null && userType.equalsIgnoreCase("student")) ? "users" : "drivers";
        String idField = (userType != null && userType.equalsIgnoreCase("student")) ? "idNumber" : "plateNumber";

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", f);
        updates.put("middleName", m);
        updates.put("lastName", l);
        updates.put("fullName", (f + " " + m + " " + l).replace("  ", " ").trim());
        updates.put(idField, idVal);

        if (userType.equalsIgnoreCase("student")) {
            updates.put("parentNumber", pNum);
        }

        db.collection(collection).document(userId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}