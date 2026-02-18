package com.example.betweenus;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout menuCalendar, menuChat, menuMural;
    private CardView cardLoveLanguage;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔹 MENU PRINCIPAL
        menuCalendar = findViewById(R.id.menuCalendar);
        menuChat = findViewById(R.id.menuChat);
        menuMural = findViewById(R.id.menuMural);

        // 🔹 CARD
        cardLoveLanguage = findViewById(R.id.cardLoveLanguage);

        // 🔥 DRAWER
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        // 🔥 ABRIR MENU PELO ÍCONE
        toolbar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(navigationView));

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

        // 🔥 MENU LATERAL CLICK
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navCoupleProfile) {
                Toast.makeText(this, "Perfil do casal", Toast.LENGTH_SHORT).show();
            }

            if (id == R.id.navPairCode) {
                Toast.makeText(this, "Gerar código de pareamento", Toast.LENGTH_SHORT).show();
            }

            if (id == R.id.navAbout) {
                Toast.makeText(this, "Sobre o aplicativo", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }
}
