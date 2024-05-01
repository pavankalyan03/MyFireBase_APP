package com.example.myfirebaseapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFirstName, editTextRegisterLastName, editTextEmail, editTextDOB, editTextMobile, editTextPwd, editTextConfirmPwd;

    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG="RegisterActivity";

    Toolbar toolbar;

    Button buttonRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);


        Toast.makeText(RegisterActivity.this, "You can Register Now", Toast.LENGTH_SHORT).show();

        progressBar = findViewById(R.id.progressbar);
        editTextRegisterFirstName = findViewById(R.id.first_editview);
        editTextRegisterLastName = findViewById(R.id.last_editview);
        editTextEmail = findViewById(R.id.email_editview);
        editTextDOB = findViewById(R.id.dob_editview);
        editTextMobile = findViewById(R.id.mobile_editview);
        editTextPwd = findViewById(R.id.password_editview);
        editTextConfirmPwd = findViewById(R.id.Confirm_password_editview);

        radioGroupRegisterGender = findViewById(R.id.textview_register_gender);
        radioGroupRegisterGender.clearCheck();

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDOB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });


        buttonRegister = findViewById(R.id.button_register1);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textFirstName = editTextRegisterFirstName.getText().toString();
                String textEmail = editTextEmail.getText().toString();
                String textLastName = editTextRegisterLastName.getText().toString();
                String textDOB = editTextDOB.getText().toString();
                String textMobile = editTextMobile.getText().toString();
                String textPwd = editTextPwd.getText().toString();
                String textConfirmPwd = editTextConfirmPwd.getText().toString();

                String textGender;

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);

                mobileMatcher = mobilePattern.matcher(textMobile);


                if(TextUtils.isEmpty(textFirstName)){
                    Toast.makeText(RegisterActivity.this, "Please Enter first Name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFirstName.setError("First Name is Required");
                    editTextRegisterFirstName.requestFocus();
                }
                else if(TextUtils.isEmpty(textLastName)){
                    Toast.makeText(RegisterActivity.this, "Please Enter Last Name", Toast.LENGTH_SHORT).show();
                    editTextRegisterLastName.setError("Last Name is Required");
                    editTextRegisterLastName.requestFocus();
                }
                else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Please Enter your Email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Email is Required");
                    editTextEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this, "Please Re-Enter your Email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Valid Email is Required");
                    editTextEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(textDOB)){
                    Toast.makeText(RegisterActivity.this, "Please Enter Last Name", Toast.LENGTH_SHORT).show();
                    editTextDOB.setError("Date of Birth is Required");
                    editTextDOB.requestFocus();
                }
                else if(radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your Gender", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is Required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }
                else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    editTextMobile.setError("Mobile Number is Required");
                    editTextMobile.requestFocus();
                }
                else if(textMobile.length()!=10){
                    Toast.makeText(RegisterActivity.this, "Please Re-Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    editTextMobile.setError("Mobile Number should be 10 digits");
                    editTextMobile.requestFocus();
                }
                else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "Please Re-Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    editTextMobile.setError("Mobile Number is not valid");
                    editTextMobile.requestFocus();
                }
                else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Password is Required");
                    editTextPwd.requestFocus();
                }
                else if(textPwd.length()<6) {
                    Toast.makeText(RegisterActivity.this, "Password should be at least six-digits", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Password is too weak");
                    editTextPwd.requestFocus();
                }
                else if(TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Please Confirm your Password", Toast.LENGTH_SHORT).show();
                    editTextConfirmPwd.setError("Password Confirmation is Required");
                    editTextConfirmPwd.requestFocus();
                }
                else if(!textPwd.equals(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Please give the same Password", Toast.LENGTH_SHORT).show();
                    editTextConfirmPwd.setError("Password Confirmation is Required");
                    editTextConfirmPwd.requestFocus();
                    editTextPwd.clearComposingText();
                    editTextConfirmPwd.clearComposingText();
                }
                else{
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFirstName, textLastName,textEmail, textDOB, textGender, textMobile, textPwd);
//                    registerUsersq(textFirstName, textLastName,textEmail, textDOB, textGender, textMobile, textPwd);
                }
            }
        });
    }

    private void registerUser(String textFirstName, String textLastName,String textEmail, String textDOB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPwd ).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "User Registration is Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFirstName,textLastName,textDOB, textGender,textMobile);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "User Registered Successfully. Please Verify your E-mail", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "User Registration is Failed. Please try again", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);


                        }
                    });
                }
                else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        editTextPwd.setError("Your Password is too weak. Kindly use the mix of alphabets ,numbers and special characters ");
                        editTextPwd.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextPwd.setError("Your E-mail already in use. Kindly use another E-mail address");
                        editTextPwd.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        editTextPwd.setError("User is already registered with this email. Kindly use another E-mail address");
                        editTextPwd.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void registerUsersq(String textFirstName, String textLastName,String textEmail, String textDOB, String textGender, String textMobile, String textPwd) {
        DBhandler dbhandler = new DBhandler(RegisterActivity.this);
        dbhandler.addNewUSER(textFirstName,textLastName,textEmail,textDOB,textGender,textMobile,textPwd);
        Toast.makeText(RegisterActivity.this, "User Registered Successfully.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        progressBar.setVisibility(View.GONE);
    }
}