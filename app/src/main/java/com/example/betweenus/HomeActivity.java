package com.example.betweenus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout menuCalendar, menuChat, menuMural;
    private CardView cardLoveLanguage;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageView btnMenu;
    private ImageView imgUser, imgPartner;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        imgUser = findViewById(R.id.imgUser);
        imgPartner = findViewById(R.id.imgPartner);

        menuCalendar = findViewById(R.id.menuCalendar);
        menuChat = findViewById(R.id.menuChat);
        menuMural = findViewById(R.id.menuMural);
        cardLoveLanguage = findViewById(R.id.cardLoveLanguage);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v ->
                drawerLayout.openDrawer(navigationView)
        );

        menuCalendar.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Calendário", Toast.LENGTH_SHORT).show());

        menuChat.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Chat", Toast.LENGTH_SHORT).show());

        menuMural.setOnClickListener(v ->
                Toast.makeText(this, "Abrir Mural", Toast.LENGTH_SHORT).show());

        cardLoveLanguage.setOnClickListener(v ->
                startActivity(new Intent(this, LoveLanguageActivity.class))
        );

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navPairCode) {
                startActivity(new Intent(this, PairCodeActivity.class));
            }

            drawerLayout.closeDrawers();
            return true;
        });

        loadUserPhotos();
    }

    private void loadUserPhotos() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) return;

                    String photoUrl = documentSnapshot.getString("photoUrl");
                    String partnerId = documentSnapshot.getString("partnerId");

                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(this)
                                .load(photoUrl)
                                .circleCrop()
                                .into(imgUser);
                    }

                    if (partnerId != null && !partnerId.isEmpty()) {

                        db.collection("users")
                                .document(partnerId)
                                .get()
                                .addOnSuccessListener(partnerDoc -> {

                                    if (!partnerDoc.exists()) return;

                                    String partnerPhoto =
                                            partnerDoc.getString("photoUrl");

                                    if (partnerPhoto != null && !partnerPhoto.isEmpty()) {

                                        Glide.with(this)
                                                .load(partnerPhoto)
                                                .circleCrop()
                                                .into(imgPartner);
                                    }
                                });
                    }
                });
    }
}