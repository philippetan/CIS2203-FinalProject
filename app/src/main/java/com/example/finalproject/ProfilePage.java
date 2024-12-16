package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePage extends AppCompatActivity {
    private TextView accountName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    String firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.profile);
        NavigationUtil.handleNavigation(bottomNav, this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        accountName = findViewById(R.id.accountName);

        String userId = mAuth.getCurrentUser().getUid();

        if (userId != null) {
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    Toast.makeText(this, "Error fetching updates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    firstName = documentSnapshot.getString("firstName");
                    lastName = documentSnapshot.getString("lastName");

                    if (firstName != null && lastName != null) {
                        accountName.setText(firstName + " " + lastName);
                    } else {
                        accountName.setText("Name not available");
                    }
                } else {
                    Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        // Edit button click listener
        Button editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send the data to EditProfilePage via Intent
                Intent intent = new Intent(ProfilePage.this, EditProfilePage.class);
                intent.putExtra("userId", userId);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                startActivity(intent);
            }
        });

        // Change password button
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePage.this, ChangePasswordPage.class);
                startActivity(intent);
            }
        });

        // Sign out button click listener
        Button signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfilePage.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}