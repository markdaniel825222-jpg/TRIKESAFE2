package com.example.trikesafe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewDriversActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DriversAdapter adapter;
    private List<User> driverList;
    private List<User> filteredList;
    private FirebaseFirestore db;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewdrivers);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rvDrivers);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        driverList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new DriversAdapter(filteredList, this);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(driverList);
        } else {
            for (User user : driverList) {
                // Search by Name or Plate
                if (user.getFullName().toLowerCase().contains(text.toLowerCase()) ||
                        user.getPlateNumber().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDrivers();
    }

    private void loadDrivers() {
        db.collection("drivers").get().addOnSuccessListener(snapshots -> {
            driverList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                User user = doc.toObject(User.class);
                if (user != null) {
                    user.setUserId(doc.getId());
                    driverList.add(user);
                }
            }
            driverList.sort((u1, u2) -> u1.getFullName().compareToIgnoreCase(u2.getFullName()));
            filter(searchView.getQuery().toString());
        });
    }
}