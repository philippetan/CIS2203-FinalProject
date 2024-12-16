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

public class CompleteTaskPage extends AppCompatActivity {
    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_task_page);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.completed_tasks);
        NavigationUtil.handleNavigation(bottomNav, this);

        // Initialize RecyclerView
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();

        taskAdapter = new TaskAdapter(taskList, task -> {
           Intent intent = new Intent(CompleteTaskPage.this, TaskDetailsPage.class);
           intent.putExtra("taskId", task.getTaskId());
            intent.putExtra("taskName", task.getName());
            intent.putExtra("taskDescription", task.getDescription());
            intent.putExtra("taskStatus", task.getStatus());
            intent.putExtra("taskCreated", task.getTaskCreated().toDate().getTime());
            startActivity(intent);
        });
        taskRecyclerView.setAdapter(taskAdapter);

        loadTasks();
    }

    private  void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(CompleteTaskPage.this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("tasks")
                .whereEqualTo("status", "Completed")
                .whereEqualTo("userId", userId)
                .orderBy("taskCreated", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    taskList.clear();

                    for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Task task = document.toObject(Task.class);
                        task.setTaskId(document.getId());
                        taskList.add(task);
                    }
                    taskAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}