package com.example.betweenus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PairCodeActivity extends AppCompatActivity {

    private TextView txtCode;
    private Button btnGenerate, btnCopy;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String generatedCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_pair_code);

        txtCode = findViewById(R.id.txtCode);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnCopy = findViewById(R.id.btnCopy);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnGenerate.setOnClickListener(v -> generatePairCode());
        btnCopy.setOnClickListener(v -> copyCode());
    }

    private void generatePairCode() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this,
                    "Usuário não autenticado",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        generatedCode = String.format("%06d", new Random().nextInt(999999));

        txtCode.setText(generatedCode);

        String userId = user.getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("pairCode", generatedCode);
        data.put("pairCodeCreatedAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userId)
                .update(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                "Código gerado ❤️",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Erro ao salvar código",
                                Toast.LENGTH_SHORT).show());
    }

    private void copyCode() {

        if (generatedCode.isEmpty()) {
            Toast.makeText(this,
                    "Gere um código primeiro",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText(
                "Código de Pareamento",
                generatedCode
        );

        clipboard.setPrimaryClip(clip);

        Toast.makeText(this,
                "Código copiado 📋",
                Toast.LENGTH_SHORT).show();
    }
}