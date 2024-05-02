package com.example.myfirebaseapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {
    ImageButton postButton;
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageResult);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        postButton = findViewById(R.id.btn_upload);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_logout) {
                Toast.makeText(HomeActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        retrieveImages();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PostActivity.class));
            }
        });
    }

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference imageRef = storage.getReference().child("images/" + UUID.randomUUID().toString());

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    db.collection("images")
                                            .add(new HashMap<String, String>() {{ put("imageUrl", imageUrl); }})
                                            .addOnSuccessListener(docRef -> Log.d("Upload", "Image URL saved"))
                                            .addOnFailureListener(e -> Log.e("Upload", "Error saving image URL", e));
                                })
                                .addOnFailureListener(e -> Log.e("Upload", "Error uploading image", e));
                    });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void retrieveImages() {
        RecyclerView myRecyclerView = findViewById(R.id.recyclerView);
        ImageAdapter adapter = new ImageAdapter();
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        myRecyclerView.setAdapter(adapter);

        db.collection("images")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> imageUrls = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        imageUrls.add(document.getString("imageUrl"));
                    }
                    adapter.setImageUrls(imageUrls);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Retrieve", "Error retrieving images", e));
    }
    private void handleImageResult(Uri uri) {
        if (uri != null) {
            // Handle the selected image URI here
            // For example, you can upload the image to Firebase Storage
            uploadImage(uri);
        } else {
            // Handle case where user did not select an image
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getContent.launch("image/*");
    }



}
