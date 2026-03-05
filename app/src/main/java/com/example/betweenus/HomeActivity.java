package com.example.betweenus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout menuCalendar, menuChat, menuMural;
    private CardView cardLoveLanguage;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageView btnMenu;
    private ImageView imgUser, imgPartner;

    private TextView txtRelationshipCounter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Date relationshipDate;

    // 🔁 handler para atualizar contador
    private Handler handler = new Handler();
    private Runnable counterRunnable;

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

        txtRelationshipCounter = findViewById(R.id.txtRelationshipCounter);

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
        loadRelationshipDate();
    }

    // ===============================
    // BUSCAR DATA RELACIONAMENTO
    // ===============================
    private void loadRelationshipDate() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {

                    String dateString = doc.getString("relationshipDate");

                    if (dateString != null) {

                        try {

                            SimpleDateFormat sdf =
                                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                            relationshipDate = sdf.parse(dateString);

                            startCounter();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // ===============================
    // INICIAR CONTADOR
    // ===============================
    private void startCounter() {

        counterRunnable = new Runnable() {
            @Override
            public void run() {

                updateRelationshipCounter();

                // atualiza a cada minuto
                handler.postDelayed(this, 60000);
            }
        };

        handler.post(counterRunnable);
    }

    // ===============================
    // ATUALIZAR CONTADOR
    // ===============================
    private void updateRelationshipCounter() {

        if (relationshipDate == null) return;

        Calendar start = Calendar.getInstance();
        start.setTime(relationshipDate);

        Calendar today = Calendar.getInstance();

        int years = today.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        int months = today.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        int days = today.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH);

        if (days < 0) {
            months--;
            Calendar temp = (Calendar) today.clone();
            temp.add(Calendar.MONTH, -1);
            days += temp.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        if (months < 0) {
            years--;
            months += 12;
        }

        StringBuilder tempoPrincipal = new StringBuilder("❤️ ");

        if (years > 0) {

            tempoPrincipal.append(years)
                    .append(years == 1 ? " ano" : " anos");

            if (months > 0) {
                tempoPrincipal.append(", ")
                        .append(months)
                        .append(months == 1 ? " mês" : " meses");
            }

            if (days > 0) {
                tempoPrincipal.append(" e ")
                        .append(days)
                        .append(days == 1 ? " dia" : " dias");
            }

        } else if (months > 0) {

            tempoPrincipal.append(months)
                    .append(months == 1 ? " mês" : " meses");

            if (days > 0) {
                tempoPrincipal.append(" e ")
                        .append(days)
                        .append(days == 1 ? " dia" : " dias");
            }

        } else {

            tempoPrincipal.append(days)
                    .append(days == 1 ? " dia" : " dias");
        }

        tempoPrincipal.append(" juntos");

        long diff = new Date().getTime() - relationshipDate.getTime();

        long totalDays = TimeUnit.MILLISECONDS.toDays(diff);
        long totalHours = TimeUnit.MILLISECONDS.toHours(diff);
        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        String linhaTempo = totalDays + " dias • "
                + totalHours + " horas • "
                + totalMinutes + " minutos";

        String finalText =
                tempoPrincipal +
                        "\n" +
                        linhaTempo +
                        " construindo a nossa história";

        txtRelationshipCounter.setText(finalText);
    }

    // ===============================
    // CARREGAR FOTOS
    // ===============================
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

                                    String partnerPhoto =
                                            partnerDoc.getString("photoUrl");

                                    if (partnerPhoto != null) {

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