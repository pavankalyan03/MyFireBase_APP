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
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText EmailLogin,PasswordLogin;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    Toolbar toolbar;

    Button login;

    Button buttonForgotPassword;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);

        EmailLogin = findViewById(R.id.email_login_editview);
        PasswordLogin = findViewById(R.id.password_login_editview);

        buttonForgotPassword =findViewById(R.id.button_forgot_password);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "You can reset your password now!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });



        progressBar = findViewById(R.id.progressbarLogin);

        ImageView imageViewShowHidePwd = findViewById(R.id.imageview_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PasswordLogin.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    PasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else {
                    PasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        authProfile = FirebaseAuth.getInstance();

        login = findViewById(R.id.button_login1);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = EmailLogin.getText().toString();
                String textPwd = PasswordLogin.getText().toString();

                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Please! Enter your Email", Toast.LENGTH_SHORT).show();
                    EmailLogin.setError("Email is Required");
                    EmailLogin.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "Please! Re-Enter your Email", Toast.LENGTH_SHORT).show();
                    EmailLogin.setError("Valid Email is Required");
                    EmailLogin.requestFocus();
                }
                else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this, "Please! Enter your Password", Toast.LENGTH_SHORT).show();
                    EmailLogin.setError("Password is Required");
                    EmailLogin.requestFocus();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });


    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    if (firebaseUser.isEmailVerified()){
                        Intent intent = new Intent(LoginActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        //start user profile activity
                        startActivity(intent);
                        finish();
                        Toast.makeText(LoginActivity.this, "You are Logged In now!", Toast.LENGTH_SHORT).show();

                    }else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
                }
                else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        EmailLogin.setError("User does not Exists! or no longer valid! Please! Register again.");
                        EmailLogin.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        EmailLogin.setError("Invalid Credentials! Kindly, check and Re-Enter the Credentials.");
                        EmailLogin.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(LoginActivity.this, "Oops! Something went Wrong.", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser()!=null){
            Toast.makeText(LoginActivity.this, "Already Logged In!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this,UserProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            //start user profile activity
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(LoginActivity.this, "You can Login now.", Toast.LENGTH_SHORT).show();
        }
    }
}