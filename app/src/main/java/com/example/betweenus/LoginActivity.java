package com.example.betweenus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnEntrar: Button
    private lateinit var txtCriarConta: TextView
    private lateinit var txtEsqueceu: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🔹 Referências dos componentes
        editEmail = findViewById(R.id.editEmail)
        editSenha = findViewById(R.id.editSenha)
        btnEntrar = findViewById(R.id.btnEntrar)
        txtCriarConta = findViewById(R.id.txtCriarConta)
        txtEsqueceu = findViewById(R.id.txtEsqueceu)

        // 🔐 BOTÃO ENTRAR (LOGIN)
        btnEntrar.setOnClickListener {

            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 👉 FUTURO: autenticação com Firebase/API
            Toast.makeText(this, "Login preparado (sem backend ainda)", Toast.LENGTH_SHORT).show()

            // 👉 Simulação de sucesso → vai para Home
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // 🆕 CRIAR CONTA
        txtCriarConta.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 🔑 RECUPERAR SENHA
        txtEsqueceu.setOnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity::class.java))
        }
    }
}
