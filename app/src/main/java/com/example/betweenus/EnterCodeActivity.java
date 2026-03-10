package com.example.betweenus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EnterCodeActivity extends AppCompatActivity {

    private EditText edtCode;
    private Button btnValidate;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edtCode = findViewById(R.id.edtCode);
        btnValidate = findViewById(R.id.btnValidate);

        db = FirebaseFirestore.getInstance();

        btnValidate.setOnClickListener(v -> validateCode());
    }

    private void validateCode() {

        String code = edtCode.getText().toString().trim();

        if (code.isEmpty()) {
            Toast.makeText(this, "Digite o código", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("pairCode", code)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        String partnerUserId =
                                queryDocumentSnapshots.getDocuments().get(0).getId();

                        // salva temporariamente
                        SharedPreferences prefs =
                                getSharedPreferences("BetweenUs", MODE_PRIVATE);

                        prefs.edit()
                                .putString("pendingPartnerId", partnerUserId)
                                .apply();

                        Toast.makeText(this,
                                "Código válido ❤️",
                                Toast.LENGTH_SHORT).show();

                        // 🔥 vai para login Google
                        startActivity(new Intent(
                                EnterCodeActivity.this,
                                LoginActivity.class
                        ));

                        Intent intent = new Intent(
                                EnterCodeActivity.this,
                                LoginActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(this,
                                "Código inválido",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Erro ao validar código",
                                Toast.LENGTH_SHORT).show());
    }
}