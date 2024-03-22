package com.example.myfirebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome,textViewFirstName, textViewLastName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private  String firstname, lastname, dob, gender, mobile,email;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    Toolbar toolbar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_profile) {
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
                Toast.makeText(UserProfileActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            return false;
        });

        textViewWelcome = findViewById(R.id.textview_show_welcome);
        textViewFirstName = findViewById(R.id.textview_show_firstname);
        textViewLastName = findViewById(R.id.textview_show_lastname);
        textViewEmail = findViewById(R.id.textview_show_email);
        textViewDoB = findViewById(R.id.textview_show_dob);
        textViewGender = findViewById(R.id.textview_show_gender);
        textViewMobile = findViewById(R.id.textview_show_mobile);

        progressBar = findViewById(R.id.progressbarShow);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong! User details not available at this moment", Toast.LENGTH_LONG).show();
        }
        else{
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("Please! Verify your Email now. You cannot Login without Email verification");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile  = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails!=null){
                    firstname = readUserDetails.firstname;
                    lastname = readUserDetails.lastname;
                    email = firebaseUser.getEmail();
                    dob = readUserDetails.dob;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    textViewWelcome.setText("Welcome,"+firstname +"!");
                    textViewFirstName.setText(firstname);
                    textViewLastName.setText(lastname);
                    textViewEmail.setText(email);
                    textViewDoB.setText(dob);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went Wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.menu_refresh){
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }/* else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent( UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UserProfileActivity.this,UpdateEmail.class);
            startActivity(intent);
        } else if (id == R.id.menu_settings) {
            Toast.makeText(UserProfileActivity.this, "menu_settings", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UserProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UserProfileActivity.this,DeleteProfileActivity.class);

        } */else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }else {
            Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}