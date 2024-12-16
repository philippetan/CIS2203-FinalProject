package com.example.finalproject;

import com.google.firebase.Timestamp;

public class Task {
    private String taskId;
    private String name;
    private String description;
    private String status;
    private Timestamp taskCreated;

    // Default constructor for Firestore
    public Task() {}

    public Task(String name, String description, String status, Timestamp taskCreated) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskCreated = taskCreated;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getTaskCreated() {
        return taskCreated;
    }

    public String getTaskId() { return taskId; }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
