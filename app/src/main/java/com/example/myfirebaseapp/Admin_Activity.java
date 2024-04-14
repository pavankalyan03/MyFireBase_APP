package com.example.myfirebaseapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Admin_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DBhandler dbHandler = new DBhandler(this);
        List<String> emailList = dbHandler.getAllEmails();
        adapter = new EmailAdapter(emailList);
        recyclerView.setAdapter(adapter);
    }
}