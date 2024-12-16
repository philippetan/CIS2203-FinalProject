package com.example.finalproject;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationUtil {
    public static void handleNavigation(BottomNavigationView bottomNav, final Context context) {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if(itemId == R.id.nav_home) {
                intent = new Intent(context, HomePage.class);
            } else if(itemId == R.id.add_task) {
                intent = new Intent(context, AddTaskPage.class);
            } else if(itemId == R.id.completed_tasks) {
                intent = new Intent(context, CompleteTaskPage.class);
            } else if(itemId == R.id.profile) {
                intent = new Intent(context, ProfilePage.class);
            }

            if(intent != null) {
                context.startActivity(intent);
                if(context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).overridePendingTransition(0, 0);
                }
            }
            return true;
        });
    }
}
