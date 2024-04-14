package com.example.myfirebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button buttonPwdReset;

    private EditText editTextPwdResetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    Toolbar toolbar;

    private final static String TAG="ForgotPasswordActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);

        editTextPwdResetEmail = findViewById(R.id.email_forgotPwd_editview);

        buttonPwdReset = findViewById(R.id.button_reset);

        progressBar = findViewById(R.id.progressbarReset);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextPwdResetEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this, "please! Enter your Registered Email.", Toast.LENGTH_LONG).show();
                    editTextPwdResetEmail.setError("Email is required");
                    editTextPwdResetEmail.requestFocus();

                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this, "please Enter a valid Email!", Toast.LENGTH_LONG).show();
                    editTextPwdResetEmail.setError("Valid Email is required");
                    editTextPwdResetEmail.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
//                    resetPassword(email);
                    resetPasswordsq(email);

                }
            }
        });

    }

    private void resetPasswordsq(String email){
        DBhandler db = new DBhandler(this);
        if (db.verifyUSER(email)){
            db.resetPassword(email);
            Toast.makeText(this, "Your Password has been set to 123456789, You can login Now <3", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
            finish();
        }
        else{
            Toast.makeText(ForgotPasswordActivity.this, "Email Not Found", Toast.LENGTH_LONG).show();
            editTextPwdResetEmail.setError("First Go Register");
            editTextPwdResetEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
        }

    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please! Check your Inbox for password Reset link", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                else{
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        editTextPwdResetEmail.setError("User does not exists or no longer valid.Please! Register again.");
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(ForgotPasswordActivity.this, "OOPS! Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}