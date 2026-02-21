package com.example.betweenus;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class OnboardingActivity extends AppCompatActivity {

    private EditText etBirthDate;
    private EditText etRelationshipDate;
    private Button btnAddAddress;
    private Button btnSave;
    private LinearLayout containerAddresses;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final List<View> addressBlocks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_onboarding);

        etBirthDate = findViewById(R.id.etBirthDate);
        etRelationshipDate = findViewById(R.id.etRelationshipDate);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        btnSave = findViewById(R.id.btnSave);
        containerAddresses = findViewById(R.id.containerAddresses);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupUI();
    }

    private void setupUI() {

        // Seletores de data
        etBirthDate.setOnClickListener(v -> openDatePicker(etBirthDate));
        etRelationshipDate.setOnClickListener(v -> openDatePicker(etRelationshipDate));

        // Endereços
        btnAddAddress.setOnClickListener(v -> addAddressBlock());

        // Salvar
        btnSave.setOnClickListener(v -> saveOnboardingData());
    }

    // ===============================
    // DATE PICKER
    // ===============================
    private void openDatePicker(EditText target) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    String date = String.format("%02d/%02d/%d", day, month + 1, year);
                    target.setText(date);

                    // Texto mais escuro após selecionar
                    target.setTextColor(Color.parseColor("#1A1A1A"));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // ===============================
    // BLOCO DE ENDEREÇO COMPLETO
    // ===============================
    private void addAddressBlock() {

        LinearLayout block = new LinearLayout(this);
        block.setOrientation(LinearLayout.VERTICAL);
        block.setPadding(0, 20, 0, 20);

        block.addView(createField("Endereço"));
        block.addView(createField("Número"));
        block.addView(createField("Complemento"));
        block.addView(createField("CEP"));
        block.addView(createField("Cidade"));
        block.addView(createField("Estado"));

        containerAddresses.addView(block);
        addressBlocks.add(block);
    }

    private EditText createField(String hint) {

        EditText field = new EditText(this);

        field.setHint(hint);
        field.setBackgroundResource(R.drawable.bg_input);
        field.setPadding(16, 16, 16, 16);

        field.setTextColor(Color.parseColor("#1A1A1A"));
        field.setHintTextColor(Color.parseColor("#777777"));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 8, 0, 8);
        field.setLayoutParams(params);

        return field;
    }

    // ===============================
    // SALVAR DADOS
    // ===============================
    private void saveOnboardingData() {

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(this,
                    "Sessão expirada. Faça login novamente.",
                    Toast.LENGTH_LONG).show();

            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        String birthDate = etBirthDate.getText().toString().trim();
        String relationshipDate = etRelationshipDate.getText().toString().trim();

        if (birthDate.isEmpty() || relationshipDate.isEmpty()) {

            Toast.makeText(this,
                    "Preencha as datas obrigatórias",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ===============================
        // COLETAR ENDEREÇOS
        // ===============================
        List<Map<String, String>> addresses = new ArrayList<>();

        for (View block : addressBlocks) {

            LinearLayout layout = (LinearLayout) block;

            Map<String, String> address = new HashMap<>();

            address.put("endereco", ((EditText) layout.getChildAt(0)).getText().toString());
            address.put("numero", ((EditText) layout.getChildAt(1)).getText().toString());
            address.put("complemento", ((EditText) layout.getChildAt(2)).getText().toString());
            address.put("cep", ((EditText) layout.getChildAt(3)).getText().toString());
            address.put("cidade", ((EditText) layout.getChildAt(4)).getText().toString());
            address.put("estado", ((EditText) layout.getChildAt(5)).getText().toString());

            addresses.add(address);
        }

        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("birthDate", birthDate);
        data.put("relationshipDate", relationshipDate);
        data.put("addresses", addresses);
        data.put("onboardingCompleted", true);

        // ===============================
        // SALVAR NO FIRESTORE
        // ===============================
        db.collection("users")
                .document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this,
                            "Perfil salvo ❤️",
                            Toast.LENGTH_SHORT).show();

                    try {

                        Intent intent = new Intent(OnboardingActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();

                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(this,
                                "Erro ao abrir a Home",
                                Toast.LENGTH_LONG).show();

                        // fallback seguro
                        startActivity(new Intent(this, WelcomeActivity.class));
                        finish();
                    }

                });
    }
}