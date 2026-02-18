package com.example.betweenus;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnLogin;
    private Button btnHaveCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnHaveCode = findViewById(R.id.btnHaveCode);

        btnHaveCode.setOnClickListener(v -> showPairDialog());
    }

    private void showPairDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_pair_code, null);
        builder.setView(view);

        EditText edtCode = view.findViewById(R.id.edtDialogCode);

        builder.setPositiveButton("Conectar", (dialog, which) -> {

            String code = edtCode.getText().toString();

            // 🔜 Aqui entra a lógica Firebase depois

        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
