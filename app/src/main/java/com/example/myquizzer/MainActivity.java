package com.example.myquizzer;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button StartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartBtn = findViewById(R.id.start_btn);

        StartBtn.setOnClickListener(v -> {
            Intent categoryintent=new Intent(MainActivity.this,CategoriesActivity.class);
            startActivity(categoryintent);
        });
//        booksmarkbtn.setOnClickListener(v -> {
//            Intent booksmarkintent=new Intent(MainActivity.this,BookmarkActivity.class);
//            startActivity(booksmarkintent);
//        });
    }
}