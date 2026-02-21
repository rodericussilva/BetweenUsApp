package com.example.betweenus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnRegister, btnLogin, btnHaveCode;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;

    private static final int RC_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_welcome);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnHaveCode = findViewById(R.id.btnHaveCode);

        // 🔥 Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔥 Configuração Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // 🔴 BOTÃO CRIAR CONTA
        btnRegister.setOnClickListener(v -> signInWithGoogle());

        // 🔵 BOTÃO ENTRAR
        btnLogin.setOnClickListener(v -> signInWithGoogle());

        // 🔥 SE JÁ ESTIVER LOGADO → PULAR WELCOME
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkIfUserExists(currentUser);
        }
    }

    // ====================================
    // LOGIN GOOGLE
    // ====================================
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            try {
                GoogleSignInAccount account =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                                .getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {

                Toast.makeText(this,
                        "Erro no login Google",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ====================================
    // AUTENTICAÇÃO FIREBASE
    // ====================================
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        checkIfUserExists(user);

                    } else {

                        Toast.makeText(this,
                                "Falha na autenticação",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ====================================
    // VERIFICA SE USUÁRIO EXISTE
    // ====================================
    private void checkIfUserExists(FirebaseUser user) {

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        Boolean completed =
                                documentSnapshot.getBoolean("onboardingCompleted");

                        if (completed != null && completed) {

                            // 🏠 PERFIL COMPLETO → HOME
                            startActivity(new Intent(
                                    this,
                                    HomeActivity.class
                            ));
                            finish();

                        } else {

                            // 📝 PERFIL INCOMPLETO → ONBOARDING
                            startActivity(new Intent(
                                    this,
                                    OnboardingActivity.class
                            ));
                            finish();
                        }

                    } else {

                        // 🔴 USUÁRIO NOVO → CRIAR NO FIRESTORE
                        createUserInFirestore(user);
                    }
                });
    }

    // ====================================
    // CRIAR USUÁRIO NO FIRESTORE
    // ====================================
    private void createUserInFirestore(FirebaseUser user) {

        Map<String, Object> userData = new HashMap<>();

        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("photoUrl",
                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

        userData.put("birthDate", null);
        userData.put("gender", null);
        userData.put("partnerId", null);
        userData.put("coupleId", null);

        userData.put("onboardingCompleted", false);
        userData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this,
                            "Conta criada ❤️",
                            Toast.LENGTH_LONG).show();

                    // 🔥 ABRIR ONBOARDING
                    startActivity(new Intent(
                            WelcomeActivity.this,
                            OnboardingActivity.class
                    ));

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Erro ao criar usuário",
                                Toast.LENGTH_SHORT).show());
    }
}