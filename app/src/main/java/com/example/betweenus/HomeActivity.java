package com.example.betweenus;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnAgenda;
    private Button btnChat;
    private Button btnMural;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAgenda = findViewById(R.id.btnAgenda);
        btnChat = findViewById(R.id.btnChat);
        btnMural = findViewById(R.id.btnMural);

        btnAgenda.setOnClickListener(v -> openAgenda());
        btnChat.setOnClickListener(v -> openChat());
        btnMural.setOnClickListener(v -> openMural());
    }

    private void openAgenda() {
        // Segunda prova
    }

    private void openChat() {
        // Terceira prova
    }

    private void openMural() {
        // Terceira prova
    }
}
