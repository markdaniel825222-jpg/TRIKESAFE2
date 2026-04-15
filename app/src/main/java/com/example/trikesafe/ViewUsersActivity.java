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

public class ViewUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private List<User> userList;        // Master list
    private List<User> filteredList;    // List shown in UI
    private FirebaseFirestore db;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewuser);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewUsers);
        searchView = findViewById(R.id.searchView); // Ensure this ID exists in XML

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // We pass the filteredList to the adapter so it updates during search
        adapter = new UsersAdapter(filteredList, this);
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
            filteredList.addAll(userList);
        } else {
            for (User user : userList) {
                // Search by Name or ID
                if (user.getFullName().toLowerCase().contains(text.toLowerCase()) ||
                        user.getIdNumber().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        db.collection("users").get().addOnSuccessListener(snapshots -> {
            userList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                User user = doc.toObject(User.class);
                if (user != null) {
                    user.setUserId(doc.getId());
                    userList.add(user);
                }
            }
            userList.sort((u1, u2) -> u1.getFullName().compareToIgnoreCase(u2.getFullName()));
            filter(searchView.getQuery().toString());
        });
    }
}