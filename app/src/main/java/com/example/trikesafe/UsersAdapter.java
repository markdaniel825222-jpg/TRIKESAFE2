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

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<User> userList;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UsersAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.name.setText(user.getFullName());
        holder.id.setText("ID: " + user.getIdNumber());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Remove student " + user.getFirstName() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        db.collection("users").document(user.getUserId()).delete()
                                .addOnSuccessListener(a -> {
                                    userList.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                                });
                    }).setNegativeButton("Cancel", null).show();
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("USER_ID", user.getUserId());
            intent.putExtra("USER_TYPE", "student");
            intent.putExtra("FIRST_NAME", user.getFirstName());
            intent.putExtra("MIDDLE_NAME", user.getMiddleName());
            intent.putExtra("LAST_NAME", user.getLastName());
            intent.putExtra("SPECIFIC_ID", user.getIdNumber());
            // Updated: Passes parent number to EditUserActivity
            intent.putExtra("PARENT_NUMBER", user.getParentNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, id;
        ImageButton btnEdit, btnDelete;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tvStudentName);
            id = v.findViewById(R.id.tvStudentID);
            btnEdit = v.findViewById(R.id.btnEditUser);
            btnDelete = v.findViewById(R.id.btnDeleteStudent);
        }
    }
}