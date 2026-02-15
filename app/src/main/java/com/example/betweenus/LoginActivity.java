package com.example.betweenus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnEnter;
    private TextView txtCreateAccount;
    private TextView txtForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnEnter = findViewById(R.id.btnEnter);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        txtForgot = findViewById(R.id.txtForgot);

        // 🔐 BOTÃO ENTRAR
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Preencha email e senha", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 👉 Preparado para autenticação futura (Firebase/API)
                Toast.makeText(LoginActivity.this, "Login preparado (sem backend ainda)", Toast.LENGTH_SHORT).show();

                // 👉 Vai para HomeActivity (que já existe)
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 🆕 CRIAR CONTA (ainda não implementado)
        txtCreateAccount.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Tela de cadastro ainda não implementada", Toast.LENGTH_SHORT).show()
        );

        // 🔑 RECUPERAR SENHA (ainda não implementado)
        txtForgot.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Recuperação ainda não implementada", Toast.LENGTH_SHORT).show()
        );
    }
}
