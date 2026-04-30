package com.example.betweenus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btnCreateAccount;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> {

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .update("fcmToken", token);

                });

        // CONFIGURA GOOGLE SIGN IN
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnCreateAccount.setOnClickListener(v -> startGoogleLogin());
    }

    // ====================================
    // LOGIN GOOGLE
    // ====================================

    private void startGoogleLogin() {

        Intent signInIntent = googleSignInClient.getSignInIntent();

        launcher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK) {

                            Intent data = result.getData();

                            try {

                                GoogleSignInAccount account =
                                        GoogleSignIn.getSignedInAccountFromIntent(data)
                                                .getResult(ApiException.class);

                                firebaseAuth(account.getIdToken());

                            } catch (ApiException e) {

                                Toast.makeText(this,
                                        "Erro login Google",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    // ====================================
    // AUTENTICAR NO FIREBASE
    // ====================================

    private void firebaseAuth(String idToken) {

        AuthCredential credential =
                GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        createUserFirestore(user);

                    } else {

                        Toast.makeText(this,
                                "Falha na autenticação",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ====================================
    // CRIAR USUÁRIO NO FIRESTORE
    // ====================================

    private void createUserFirestore(FirebaseUser user) {

        if (user == null) return;

        String uid = user.getUid();

        SharedPreferences prefs =
                getSharedPreferences("BetweenUs", MODE_PRIVATE);

        String partnerId = prefs.getString("pendingPartnerId", null);

        Map<String, Object> data = new HashMap<>();

        data.put("name", user.getDisplayName());
        data.put("email", user.getEmail());
        data.put("photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
        data.put("createdAt", System.currentTimeMillis());
        data.put("onboardingCompleted", false);
        data.put("partnerId", partnerId);

        db.collection("users")
                .document(uid)
                .set(data)
                .addOnSuccessListener(unused -> {

                    if (partnerId != null) {

                        createCouple(uid, partnerId);

                    } else {

                        goToOnboarding();
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Erro ao salvar usuário",
                                Toast.LENGTH_SHORT).show());
    }

    private void createCouple(String userId, String partnerId) {

        Map<String, Object> couple = new HashMap<>();

        couple.put("user1Id", partnerId);
        couple.put("user2Id", userId);
        couple.put("createdAt", System.currentTimeMillis());

        db.collection("couples")
                .add(couple)
                .addOnSuccessListener(documentReference -> {

                    String coupleId = documentReference.getId();

                    updateUsersCouple(userId, partnerId, coupleId);

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Erro ao criar casal",
                                Toast.LENGTH_SHORT).show());
    }

    private void updateUsersCouple(String userId, String partnerId, String coupleId) {

        Map<String, Object> update = new HashMap<>();

        update.put("coupleId", coupleId);
        update.put("pairCode", null);

        db.collection("users")
                .document(userId)
                .update(update);

        Map<String, Object> updatePartner = new HashMap<>();

        updatePartner.put("partnerId", userId);
        updatePartner.put("coupleId", coupleId);
        updatePartner.put("pairCode", null);

        db.collection("users")
                .document(partnerId)
                .update(updatePartner);

        goToOnboarding();
    }

    // ====================================
    // IR PARA ONBOARDING
    // ====================================

    private void goToOnboarding() {

        Intent intent = new Intent(
                LoginActivity.this,
                OnboardingActivity.class
        );

        startActivity(intent);
        finish();
    }
}