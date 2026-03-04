package com.example.betweenus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoveLanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Linguagens do Amor");
        }

        setContentView(R.layout.activity_love_language);
    }
}