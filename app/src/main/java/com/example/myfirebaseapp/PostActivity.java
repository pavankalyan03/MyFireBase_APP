package com.example.myfirebaseapp;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 34567;
    private Uri imageUri = null;
    private ImageView postImage;
    private EditText etPost;
    private ProgressBar pb;
    private String post;
    private FirebaseAuth mAuth;
    private RelativeLayout homeLayout;
    private DatabaseReference mPhotosDatabase;
    private StorageReference mPhotosStrorage;
    private String userId,download_url;
    Toolbar toolbar;

    Button atch,pst;


    private final ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageResult);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar = findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);

        postImage = findViewById(R.id.postImage);
        etPost = findViewById(R.id.etPost);
        pb = findViewById(R.id.sendProgress);
        homeLayout = findViewById(R.id.homeLayout);
        atch = findViewById(R.id.atbutton);
        pst = findViewById(R.id.pobutton);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        mPhotosDatabase = FirebaseDatabase.getInstance().getReference().child("PhotoHub/Blog");
        mPhotosStrorage = FirebaseStorage.getInstance().getReference().child("PhotoHub/BlogImages");


        atch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                getContent.launch("image/*");
            }
        });

        pst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post = etPost.getText().toString();
                if (!TextUtils.isEmpty(post) && imageUri != null) {
                    pb.setVisibility(View.VISIBLE);
                    uploadImage(imageUri);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_attachment) {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            getContent.launch("image/*");
        } else if (item.getItemId() == R.id.menu_send) {
            post = etPost.getText().toString();
            if (!TextUtils.isEmpty(post) && imageUri != null) {
                pb.setVisibility(View.VISIBLE);
                uploadImage(imageUri);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadImage(Uri imageUri) {
        final StorageReference newPhoto = mPhotosStrorage.child(Objects.requireNonNull(imageUri.getLastPathSegment()));
        newPhoto.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    final String myKey = mPhotosDatabase.push().getKey();

                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            download_url = uri.toString();
                            // Continue with the upload process
                        }
                    });
                    String datem = getDateTime();
                    DatabaseReference newDatabase = mPhotosDatabase.child(myKey);

                    newDatabase.child("postid").setValue(myKey);
                    newDatabase.child("postedby").setValue(userId);
                    newDatabase.child("postedon").setValue(datem);
                    newDatabase.child("postdetails").setValue(post);
                    newDatabase.child("postlikes").setValue(0);
                    newDatabase.child("postviews").setValue(0);
                    newDatabase.child("postcomments").setValue(0);

                    newDatabase.child("postimage").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {


                                pb.setVisibility(View.GONE);
                                Pair[] pairs = new Pair[1];
                                pairs[0] = new Pair<View, String>(homeLayout, "etTransition");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PostActivity.this, pairs);


                                startActivity(new Intent(PostActivity.this, HomeActivity.class), options.toBundle());

                            }
                        }
                    });

                } else {
                    Toast.makeText(PostActivity.this, "Error:" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleImageResult(Uri uri) {
        if (uri != null) {
            imageUri = uri;
            postImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                postImage.setImageURI(imageUri);
            }
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
