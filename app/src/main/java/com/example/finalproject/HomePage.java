package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import adapter.TaskAdapter;


public class HomePage extends AppCompatActivity {
    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        NavigationUtil.handleNavigation(bottomNav, this);

        // Initialize RecyclerView
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Task List
        taskList = new ArrayList<>();

        // Set up TaskAdapter with click handling
        taskAdapter = new TaskAdapter(taskList, task -> {
            // Navigate to TaskDetailsPage
            Intent intent = new Intent(HomePage.this, TaskDetailsPage.class);
            intent.putExtra("taskId", task.getTaskId());
            intent.putExtra("taskName", task.getName());
            intent.putExtra("taskDescription", task.getDescription());
            intent.putExtra("taskStatus", task.getStatus());
            intent.putExtra("taskCreated", task.getTaskCreated().toDate().getTime());
            startActivity(intent);
        });
        taskRecyclerView.setAdapter(taskAdapter);

        // Load tasks from Firestore
        loadTasks();
    }

    private void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(HomePage.this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch tasks from the "tasks" collection and listen for real-time updates
        db.collection("tasks")
                .whereEqualTo("status", "Pending")
                .whereEqualTo("userId", userId)
                .orderBy("taskCreated", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        taskList.clear();

                        // Iterate over the updated task documents
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Task task = document.toObject(Task.class);
                            task.setTaskId(document.getId());
                            taskList.add(task);
                        }

                        // Notify the adapter that the data has changed
                        taskAdapter.notifyDataSetChanged();
                    }
                });
    }
}