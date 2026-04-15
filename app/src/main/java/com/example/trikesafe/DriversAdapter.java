package com.example.trikesafe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class DriversAdapter extends RecyclerView.Adapter<DriversAdapter.ViewHolder> {
    private List<User> driverList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DriversAdapter(List<User> driverList, Context context) {
        this.driverList = driverList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_driver, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User driver = driverList.get(position);
        holder.name.setText(driver.getFullName());
        holder.plate.setText("Plate: " + driver.getPlateNumber());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Remove driver " + driver.getFirstName() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        db.collection("drivers").document(driver.getUserId()).delete()
                                .addOnSuccessListener(a -> {
                                    driverList.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                                });
                    }).setNegativeButton("Cancel", null).show();
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("USER_ID", driver.getUserId());
            intent.putExtra("USER_TYPE", "driver");
            intent.putExtra("FIRST_NAME", driver.getFirstName());
            intent.putExtra("MIDDLE_NAME", driver.getMiddleName());
            intent.putExtra("LAST_NAME", driver.getLastName());
            intent.putExtra("SPECIFIC_ID", driver.getPlateNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return driverList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, plate;
        ImageButton btnEdit, btnDelete;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tvDriverName);
            plate = v.findViewById(R.id.tvPlateNumber);
            btnEdit = v.findViewById(R.id.btnEditUser);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}