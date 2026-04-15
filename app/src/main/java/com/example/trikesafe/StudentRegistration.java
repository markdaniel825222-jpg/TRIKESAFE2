package com.example.trikesafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class StudentRegistration extends AppCompatActivity {

    private EditText editTextFirstName, editTextMiddleName, editTextLastName,
            editTextIDNumber, editTextParentNumber, editTextEmail, editTextPassword;
    private Button buttonDone;
    private ImageButton imageButtonBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");

        final Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextMiddleName = findViewById(R.id.editTextMiddleName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextIDNumber = findViewById(R.id.editTextIDNumber);
        editTextParentNumber = findViewById(R.id.editTextParentNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonDone = findViewById(R.id.buttonDone);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        imageButtonBack.setOnClickListener(v -> {
            v.startAnimation(clickAnim);
            finish();
        });

        buttonDone.setOnClickListener(v -> {
            v.startAnimation(clickAnim);

            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String parentNum = editTextParentNumber.getText().toString().trim();
            String firstName = editTextFirstName.getText().toString().trim();
            String middleName = editTextMiddleName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String idNum = editTextIDNumber.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                    lastName.isEmpty() || idNum.isEmpty() || parentNum.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                return;
            }

            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Map<String, Object> student = new HashMap<>();
                    student.put("firstName", firstName);
                    student.put("middleName", middleName);
                    student.put("lastName", lastName);
                    student.put("idNumber", idNum);
                    student.put("parentNumber", parentNum);
                    student.put("email", email);
                    student.put("role", "student");

                    db.collection("users").document(user.getUid()).set(student)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                startActivity(new Intent(this, StudentConfirmationPage.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}