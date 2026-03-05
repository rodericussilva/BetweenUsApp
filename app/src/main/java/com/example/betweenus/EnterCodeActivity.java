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

        db.collection("couples")
                .whereEqualTo("code", code)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        String coupleId =
                                queryDocumentSnapshots.getDocuments().get(0).getId();

                        // 🔥 Salva temporariamente
                        SharedPreferences prefs =
                                getSharedPreferences("BetweenUs", MODE_PRIVATE);

                        prefs.edit()
                                .putString("pendingCoupleId", coupleId)
                                .apply();

                        Toast.makeText(this,
                                "Código válido ❤️",
                                Toast.LENGTH_LONG).show();

                        finish();

                    } else {

                        Toast.makeText(this,
                                "Código inválido",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
