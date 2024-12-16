package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TaskDetailsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details_page);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        String taskId = intent.getStringExtra("taskId");
        String taskName = intent.getStringExtra("taskName");
        String taskDescription = intent.getStringExtra("taskDescription");
        String taskStatus = intent.getStringExtra("taskStatus");
        String taskComment = intent.getStringExtra("taskComment");
        long taskCreatedTimestamp = intent.getLongExtra("taskCreated", -1);

        // Find views and populate them with data
        TextView taskNameView = findViewById(R.id.taskName);
        TextView taskDescriptionView = findViewById(R.id.taskDescription);
        TextView taskStatusView = findViewById(R.id.taskStatus);
        TextView taskCreatedView = findViewById(R.id.taskCreated);
        TextView taskCommentView = findViewById(R.id.taskComment);

        taskNameView.setText(taskName != null ? taskName : "N/A");
        taskDescriptionView.setText(taskDescription != null ? taskDescription : "N/A");
        taskStatusView.setText(taskStatus != null ? taskStatus : "N/A");
        taskCommentView.setText(taskComment != null ? taskComment : "N/A");

        if("Pending".equals(taskStatus)) {
            taskStatusView.setTextColor(Color.RED);
        } else {
            taskStatusView.setTextColor(Color.GREEN);
        }

        // Format and display the taskCreated date
        if (taskCreatedTimestamp != -1) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
            String formattedDate = outputFormat.format(new Date(taskCreatedTimestamp));
            taskCreatedView.setText(formattedDate);
        } else {
            taskCreatedView.setText("N/A");
        }

        // Handle back button click
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Complete button click
        Button completeButton = findViewById(R.id.completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String taskId = intent.getStringExtra("taskId");

                if(taskId != null && "Pending".equals(taskStatus)) {
                    DocumentReference taskRef = db.collection("tasks").document(taskId);

                    taskRef.update("status", "Completed")
                            .addOnSuccessListener(aVoid -> {
                                taskStatusView.setText("Completed");
                                taskStatusView.setTextColor(Color.GREEN);

                                Toast.makeText(TaskDetailsPage.this, "Task marked as completed.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TaskDetailsPage.this, "Failed to update task status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(TaskDetailsPage.this, "Task is already completed or invalid task ID.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Edit button click
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskName = taskNameView.getText().toString();
                String taskDescription = taskDescriptionView.getText().toString();

                Intent intent = new Intent(TaskDetailsPage.this, EditTaskPage.class);
                intent.putExtra("taskId", taskId);
                intent.putExtra("taskName", taskName);
                intent.putExtra("taskDescription", taskDescription);

                startActivity(intent);
            }
        });

        // Delete button click
        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String taskId = intent.getStringExtra("taskId");

                if(taskId != null) {
                    DocumentReference taskRef = db.collection("tasks").document(taskId);

                    taskRef.delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(TaskDetailsPage.this, "Task deleted successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TaskDetailsPage.this, "Failed to delete task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(TaskDetailsPage.this, "Task ID is invalid.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference taskRef = db.collection("tasks").document(taskId);

        taskRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(TaskDetailsPage.this, "Failed to load task data.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Update the UI with the new task data
                String updatedTaskName = documentSnapshot.getString("name");
                String updatedTaskDescription = documentSnapshot.getString("description");
                String updatedTaskStatus = documentSnapshot.getString("status");
                Timestamp updatedTaskCreatedTimestamp = documentSnapshot.getTimestamp("taskCreated");
                String newTaskComment = documentSnapshot.getString("comment");

                // Update your UI views here with the latest data
                taskNameView.setText(updatedTaskName != null ? updatedTaskName : "N/A");
                taskDescriptionView.setText(updatedTaskDescription != null ? updatedTaskDescription : "N/A");
                taskStatusView.setText(updatedTaskStatus != null ? updatedTaskStatus : "N/A");
                taskCommentView.setText(newTaskComment != null ? newTaskComment : "N/A");

                // Color code task status
                if ("Pending".equals(updatedTaskStatus)) {
                    taskStatusView.setTextColor(Color.RED);
                } else {
                    taskStatusView.setTextColor(Color.GREEN);
                }

                // Update the creation date
                if (updatedTaskCreatedTimestamp != null) {
                    // Convert Timestamp to Date
                    Date date = updatedTaskCreatedTimestamp.toDate();

                    // Format the date
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
                    String formattedDate = outputFormat.format(date);
                    taskCreatedView.setText(formattedDate);
                } else {
                    taskCreatedView.setText("N/A");
                }
            }
        });
    }
}