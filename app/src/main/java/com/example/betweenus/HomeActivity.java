package com.example.betweenus;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout menuCalendar, menuChat, menuMural;
    private CardView cardLoveLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔹 MENU
        menuCalendar = findViewById(R.id.menuCalendar);
        menuChat = findViewById(R.id.menuChat);
        menuMural = findViewById(R.id.menuMural);

        // 🔹 CARD
        cardLoveLanguage = findViewById(R.id.cardLoveLanguage);

        // 🧭 MENU CLICK
        menuCalendar.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Calendário", Toast.LENGTH_SHORT).show());

        menuChat.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Chat", Toast.LENGTH_SHORT).show());

        menuMural.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Mural", Toast.LENGTH_SHORT).show());

        // 🧾 CARD CLICK
        cardLoveLanguage.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Linguagens do Amor", Toast.LENGTH_SHORT).show());
    }
}
