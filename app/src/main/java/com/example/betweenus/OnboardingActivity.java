package com.example.betweenus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private EditText etBirthDate;
    private EditText etRelationshipDate;
    private Button btnAddAddress;
    private Button btnSave;
    private LinearLayout containerAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        etBirthDate = findViewById(R.id.etBirthDate);
        etRelationshipDate = findViewById(R.id.etRelationshipDate);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        btnSave = findViewById(R.id.btnSave);
        containerAddresses = findViewById(R.id.containerAddresses);

        setupUI();
    }

    private void setupUI() {

        etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementar DatePicker depois
            }
        });

        etRelationshipDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementar DatePicker depois
            }
        });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Depois vamos adicionar novos campos dinamicamente
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Enviar dados depois
            }
        });
    }
}