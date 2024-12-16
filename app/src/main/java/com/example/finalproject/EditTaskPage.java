package com.example.finalproject;

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

public class EditTaskPage extends AppCompatActivity {
    private EditText taskNameInput, taskDescriptionInput, taskCommentInput;
    private Button saveButton;
    private String taskId, currentTaskName, currentTaskDescription, taskComment;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_page);

        db = FirebaseFirestore.getInstance();

        // Back button click listener
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Initialize views
        taskNameInput = findViewById(R.id.taskNameInput);
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput);
        taskCommentInput = findViewById(R.id.taskComment);
        saveButton = findViewById(R.id.saveButton);

        // Get the task details passed from TaskDetailsPage
        taskId = getIntent().getStringExtra("taskId");
        currentTaskName = getIntent().getStringExtra("taskName");
        currentTaskDescription = getIntent().getStringExtra("taskDescription");
        taskComment = getIntent().getStringExtra("taskComment");


        // Set the current task details into the EditText fields
        taskNameInput.setText(currentTaskName);
        taskDescriptionInput.setText(currentTaskDescription);
        taskCommentInput.setText(taskComment);

        // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Collect updated task details
                String updatedTaskName = taskNameInput.getText().toString();
                String updatedTaskDescription = taskDescriptionInput.getText().toString();
                String updatedTaskComment = taskCommentInput.getText().toString();

                // Update the task in your database (Firestore, etc.)
                updateTaskInDatabase(updatedTaskName, updatedTaskDescription, updatedTaskComment);

                // Close the activity (navigate back)
                finish();
            }
        });
    }

    // Method to update the task in your database
    private void updateTaskInDatabase(String updatedTaskName, String updatedTaskDescription, String updatedTaskComment) {
        // Implement your logic here to update the task in your database
        Map<String, Object> taskUpdates = new HashMap<>();
        taskUpdates.put("name", updatedTaskName);
        taskUpdates.put("description", updatedTaskDescription);
        taskUpdates.put("comment", updatedTaskComment);

        db.collection("tasks").document(taskId)
                .update(taskUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    }
}
