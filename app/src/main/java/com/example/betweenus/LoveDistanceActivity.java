package com.example.betweenus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoveDistanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        setContentView(R.layout.activity_love_distance);
    }
}