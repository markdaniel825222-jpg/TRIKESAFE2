package com.example.trikesafe;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewLoginActivity extends AppCompatActivity {

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

        btnBack.setOnClickListener(v -> finish());
        loadRideHistory();
    }

    private void loadRideHistory() {
        pbLoading.setVisibility(View.VISIBLE);

        String currentPlate = getSharedPreferences("TrikeSafePrefs", MODE_PRIVATE)
                .getString("loggedInPlate", "");

        Query query;
        if ("ADMIN_ALL".equals(currentPlate)) {
            query = db.collection("ride_logs").orderBy("timestamp", Query.Direction.DESCENDING);
        } else {
            query = db.collection("ride_logs")
                    .whereEqualTo("plateNumber", currentPlate)
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            pbLoading.setVisibility(View.GONE);
            logList.clear();

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String student = doc.getString("studentName");
                String rawData = doc.getString("driverData"); // Reading the field saved in DriverInfoActivity
                String plate = doc.getString("plateNumber");
                Timestamp ts = doc.getTimestamp("timestamp");

                // Use the helper method to get just the Name from the long string
                String driverName = parseDriverName(rawData);

                String timeStr = (ts != null) ?
                        new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(ts.toDate())
                        : "Unknown Time";

                StringBuilder logLine = new StringBuilder();
                logLine.append("📅 ").append(timeStr);
                logLine.append("\n👤 Student: ").append(student != null ? student : "Guest");

                // Always show for Admin; can also show for drivers if needed
                if ("ADMIN_ALL".equals(currentPlate)) {
                    logLine.append("\n👨‍✈️ Driver: ").append(driverName);
                    logLine.append("\n🚲 Plate: ").append(plate != null ? plate : "N/A");
                }

                logList.add(logLine.toString());
            }

            if (logList.isEmpty()) logList.add("No history records found.");
            adapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            pbLoading.setVisibility(View.GONE);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String parseDriverName(String data) {
        if (data == null || data.isEmpty()) return "Unknown Driver";

        try {
            // Checks if string follows: "Name: [Name] Plate: [Plate]..."
            if (data.contains("Name:") && data.contains("Plate:")) {
                int start = data.indexOf("Name:") + 5;
                int end = data.indexOf("Plate:");
                return data.substring(start, end).trim();
            }
        } catch (Exception e) {
            return "Parsing Error";
        }
        return data; // Fallback to full string if format is different
    }
}