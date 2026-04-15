package com.example.trikesafe;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StudentHistoryActivity extends AppCompatActivity {

    private ListView lvHistory;
    private ProgressBar pbLoading;
    private ImageButton btnBack;
    private FirebaseFirestore db;
    private ArrayList<String> logList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewlogin);

        db = FirebaseFirestore.getInstance();
        lvHistory = findViewById(R.id.lvHistory);
        pbLoading = findViewById(R.id.pbLoading);
        btnBack = findViewById(R.id.btnBack);

        logList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item_black, logList);
        lvHistory.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            // LOGOUT: Clear the stored ID and Sign out from Firebase
            getSharedPreferences("TrikeSafePrefs", MODE_PRIVATE).edit()
                    .remove("loggedInStudentID").apply();
            FirebaseAuth.getInstance().signOut();
            finish();
        });

        loadStudentRideHistory();
    }

    private void loadStudentRideHistory() {
        pbLoading.setVisibility(View.VISIBLE);

        String currentStudentID = getSharedPreferences("TrikeSafePrefs", MODE_PRIVATE)
                .getString("loggedInStudentID", "").trim();

        if (currentStudentID.isEmpty()) {
            pbLoading.setVisibility(View.GONE);
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db.collection("ride_logs")
                .whereEqualTo("idNumber", currentStudentID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pbLoading.setVisibility(View.GONE);
                    logList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String plate = doc.getString("plateNumber");
                        String rawData = doc.getString("driverData");
                        Timestamp ts = doc.getTimestamp("timestamp");

                        String driverName = parseDriverName(rawData);

                        String timeStr = (ts != null) ?
                                new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(ts.toDate())
                                : "Unknown Time";

                        logList.add("📅 " + timeStr +
                                "\n👤 Driver: " + driverName +
                                "\n🚲 Plate: " + (plate != null ? plate : "N/A") +
                                "\n✅ Ride Logged");
                    }

                    if (logList.isEmpty()) {
                        logList.add("No travel history found for: " + currentStudentID);
                    }
                    adapter.notifyDataSetChanged();

                }).addOnFailureListener(e -> {
                    pbLoading.setVisibility(View.GONE);
                    Log.e("FIRESTORE_ERROR", "Error: " + e.getMessage(), e);
                    Toast.makeText(this, "Error loading history", Toast.LENGTH_LONG).show();
                });
    }

    private String parseDriverName(String data) {
        if (data == null || data.isEmpty()) return "Unknown Driver";
        try {
            if (data.contains("Name:") && data.contains("Plate:")) {
                int start = data.indexOf("Name:") + 5;
                int end = data.indexOf("Plate:");
                return data.substring(start, end).trim();
            }
        } catch (Exception e) {
            return "Unknown Driver";
        }
        return data;
    }
}