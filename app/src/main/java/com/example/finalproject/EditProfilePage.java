package com.example.finalproject;

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

public class EditProfilePage extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);

        db = FirebaseFirestore.getInstance();

        // Back button click listener
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");

        EditText firstNameInput = findViewById(R.id.firstNameInput);
        EditText lastNameInput = findViewById(R.id.lastNameInput);

        if(firstName != null) {
            firstNameInput.setText(firstName);
        }

        if(lastName != null) {
            lastNameInput.setText(lastName);
        }

        // Save button click listener
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedFirstName = firstNameInput.getText().toString();
                String updatedLastName = lastNameInput.getText().toString();
                
                if(userId != null) {
                    updateTaskInDatabase(userId, updatedFirstName, updatedLastName);
                } else {
                    Toast.makeText(EditProfilePage.this, "Error: User ID not found.", Toast.LENGTH_SHORT).show();
                }
                
                finish();
            }
        });
    }

    private void updateTaskInDatabase(String userId, String updatedFirstName, String updatedLastName) {
        Map<String, Object> taskUpdates = new HashMap<>();
        taskUpdates.put("firstName", updatedFirstName);
        taskUpdates.put("lastName", updatedLastName);

        db.collection("users").document(userId)
                .update(taskUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Name updated successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating name.", Toast.LENGTH_SHORT).show();
                });
    }
}