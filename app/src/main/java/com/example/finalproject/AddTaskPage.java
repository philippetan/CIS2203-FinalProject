package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTaskPage extends AppCompatActivity {
    EditText taskNameInput, taskDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_page);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.add_task);
        NavigationUtil.handleNavigation(bottomNav, this);

        taskNameInput = findViewById(R.id.taskNameInput);
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput);
        Button addTaskButton = findViewById(R.id.addTaskButton);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskName = taskNameInput.getText().toString().trim();
                String taskDescription = taskDescriptionInput.getText().toString().trim();

                if(taskName.isEmpty() || taskDescription.isEmpty()) {
                    Toast.makeText(AddTaskPage.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

                if (userId == null) {
                    Toast.makeText(AddTaskPage.this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> task = new HashMap<>();
                task.put("name", taskName);
                task.put("description", taskDescription);
                task.put("taskCreated", Timestamp.now());
                task.put("status", "Pending");
                task.put("userId", userId);

                db.collection("tasks")
                        .add(task)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(AddTaskPage.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                            taskNameInput.setText("");
                            taskDescriptionInput.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddTaskPage.this, "Failed to add task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}