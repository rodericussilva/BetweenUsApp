package com.example.betweenus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CoupleProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_couple_profile);

        // Depois vamos carregar dados do casal aqui
    }
}
