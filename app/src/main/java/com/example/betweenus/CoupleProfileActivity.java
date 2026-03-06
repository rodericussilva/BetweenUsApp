package com.example.betweenus;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoupleProfileActivity extends AppCompatActivity {

    private ImageView imgUser, imgPartner;

    private TextView tvCoupleName;
    private TextView tvTogetherTime;
    private TextView tvBirthdays;
    private TextView tvRelationshipStart;

    private LinearLayout containerAddresses;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Date relationshipDate;

    private Handler handler = new Handler();
    private Runnable counterRunnable;

    private String userName = "";
    private String partnerName = "";

    private String userBirthday = "";
    private String partnerBirthday = "";

    private String coupleId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_couple_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        imgUser = findViewById(R.id.imgUser);
        imgPartner = findViewById(R.id.imgPartner);

        tvCoupleName = findViewById(R.id.tvCoupleName);
        tvTogetherTime = findViewById(R.id.tvTogetherTime);
        tvBirthdays = findViewById(R.id.tvBirthdays);
        tvRelationshipStart = findViewById(R.id.tvRelationshipStart);

        containerAddresses = findViewById(R.id.containerAddresses);

        loadUser();
    }

    // =========================
    // CARREGAR USUÁRIO LOGADO
    // =========================
    private void loadUser() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {

                    if (!userDoc.exists()) return;

                    userName = userDoc.getString("name");
                    userBirthday = userDoc.getString("birthDate");

                    coupleId = userDoc.getString("coupleId");

                    String photoUrl = userDoc.getString("photoUrl");
                    String partnerId = userDoc.getString("partnerId");

                    if (photoUrl != null) {
                        Glide.with(this)
                                .load(photoUrl)
                                .circleCrop()
                                .into(imgUser);
                    }

                    // MOSTRA NOME MESMO SEM PARCEIRO
                    tvCoupleName.setText(userName);

                    // ENDEREÇOS DO USUÁRIO
                    loadUserAddresses(userDoc.get("addresses"), userName);

                    // CARREGAR PARCEIRO
                    if (partnerId != null && !partnerId.isEmpty()) {
                        loadPartner(partnerId);
                    }

                    // ENDEREÇOS DO COUPLE
                    if (coupleId != null && !coupleId.isEmpty()) {
                        loadCoupleAddresses();
                    }

                    updateBirthdays();
                    loadRelationshipDate();
                });
    }

    // =========================
    // PARCEIRO
    // =========================
    private void loadPartner(String partnerId) {

        db.collection("users")
                .document(partnerId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    partnerName = doc.getString("name");
                    partnerBirthday = doc.getString("birthDate");

                    String photo = doc.getString("photoUrl");

                    if (photo != null) {
                        Glide.with(this)
                                .load(photo)
                                .circleCrop()
                                .into(imgPartner);
                    }

                    // ENDEREÇOS DO PARCEIRO
                    loadUserAddresses(doc.get("addresses"), partnerName);

                    tvCoupleName.setText(userName + " ❤️ " + partnerName);

                    updateBirthdays();
                });
    }

    // =========================
    // ANIVERSÁRIOS
    // =========================
    private void updateBirthdays() {

        String text =
                userName + ": " + safe(userBirthday);

        if (partnerName != null && !partnerName.isEmpty()) {
            text += "\n" + partnerName + ": " + safe(partnerBirthday);
        }

        tvBirthdays.setText("Aniversários:\n" + text);
    }

    // =========================
    // ENDEREÇOS USERS
    // =========================
    private void loadUserAddresses(Object addressField, String owner) {

        if (addressField == null) return;

        if (addressField instanceof List) {

            List<Map<String, Object>> addresses =
                    (List<Map<String, Object>>) addressField;

            for (Map<String, Object> address : addresses) {

                String rua = (String) address.get("endereco");
                String numero = (String) address.get("numero");
                String cidade = (String) address.get("cidade");
                String estado = (String) address.get("estado");
                String cep = (String) address.get("cep");
                String complemento = (String) address.get("complemento");

                String texto =
                        "📍 " + owner +
                                "\n" +
                                safe(rua) + ", " + safe(numero) +
                                "\n" +
                                safe(cidade) + " - " + safe(estado) +
                                "\nCEP: " + safe(cep) +
                                "\n" + safe(complemento);

                addAddress(texto);
            }
        }
    }

    // =========================
    // ENDEREÇOS DO COUPLE
    // =========================
    private void loadCoupleAddresses() {

        db.collection("couples")
                .document(coupleId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    Object addressField = doc.get("addresses");

                    if (addressField instanceof List) {

                        List<Map<String, Object>> addresses =
                                (List<Map<String, Object>>) addressField;

                        for (Map<String, Object> address : addresses) {

                            String rua = (String) address.get("endereco");
                            String numero = (String) address.get("numero");
                            String cidade = (String) address.get("cidade");
                            String estado = (String) address.get("estado");
                            String cep = (String) address.get("cep");

                            String texto =
                                    "🏠 Endereço do casal" +
                                            "\n" +
                                            safe(rua) + ", " + safe(numero) +
                                            "\n" +
                                            safe(cidade) + " - " + safe(estado) +
                                            "\nCEP: " + safe(cep);

                            addAddress(texto);
                        }
                    }
                });
    }

    // =========================
    // ADICIONAR ENDEREÇO NA TELA
    // =========================
    private void addAddress(String text) {

        TextView tv = new TextView(this);

        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        tv.setPadding(0,0,0,40);

        containerAddresses.addView(tv);
    }

    // ===============================
    // RELATIONSHIP DATE
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

                        tvRelationshipStart.setText("Início do namoro: " + dateString);

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
    // CONTADOR
    // ===============================
    private void startCounter() {

        counterRunnable = new Runnable() {
            @Override
            public void run() {

                updateRelationshipCounter();

                handler.postDelayed(this,60000);
            }
        };

        handler.post(counterRunnable);
    }

    // ===============================
    // UPDATE CONTADOR
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

        String linhaTempo =
                totalDays + " dias • " +
                        totalHours + " horas • " +
                        totalMinutes + " minutos";

        String finalText =
                tempoPrincipal.toString() +
                        "\n" +
                        linhaTempo +
                        " construindo a nossa história";

        tvTogetherTime.setText(finalText);
    }

    private String safe(String text){
        return text == null ? "-" : text;
    }
}