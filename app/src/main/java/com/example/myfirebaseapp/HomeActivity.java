package com.example.myfirebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        authProfile = FirebaseAuth.getInstance();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                finish();
                return true;
            }
//            else if (item.getItemId() == R.id.bottom_editprofile) {
//                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
//                return true;
//            }
            else if (item.getItemId() == R.id.bottom_logout) {
                authProfile.signOut();
                Toast.makeText(HomeActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            return false;
        });
    }
}