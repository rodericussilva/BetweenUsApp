package com.example.betweenus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoveLanguageActivity extends AppCompatActivity {

    private LinearLayout layoutIntro, layoutQuiz, layoutReadMore;
    private Button btnReadMore, btnStartTest, btnNext;

    private TextView txtQuestion, txtResult;
    private RadioGroup radioGroup;
    private RadioButton option1, option2;

    private int currentQuestion = 0;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private final String[][] questions = {
            {"Você prefere ouvir 'Eu te amo' ou receber um abraço?", "Palavras", "Toque"},
            {"Você se sente mais amado quando passam tempo juntos ou quando recebe ajuda?", "Tempo", "Serviço"},
            {"Prefere ganhar um presente ou receber elogios?", "Presente", "Palavras"},
            {"Se sente mais amado com carinho físico ou quando fazem algo por você?", "Toque", "Serviço"},
            {"Prefere sair juntos ou ganhar algo simbólico?", "Tempo", "Presente"}
    };

    private Map<String, Integer> score = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_language);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        layoutIntro = findViewById(R.id.layoutIntro);
        layoutQuiz = findViewById(R.id.layoutQuiz);
        layoutReadMore = findViewById(R.id.layoutReadMore);

        btnReadMore = findViewById(R.id.btnReadMore);
        btnStartTest = findViewById(R.id.btnStartTest);
        btnNext = findViewById(R.id.btnNext);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtResult = findViewById(R.id.txtResult);
        radioGroup = findViewById(R.id.radioGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);

        score.put("Palavras", 0);
        score.put("Tempo", 0);
        score.put("Serviço", 0);
        score.put("Presente", 0);
        score.put("Toque", 0);

        btnReadMore.setOnClickListener(v -> {
            if (layoutReadMore.getVisibility() == View.GONE) {
                layoutReadMore.setVisibility(View.VISIBLE);
            } else {
                layoutReadMore.setVisibility(View.GONE);
            }
        });

        btnStartTest.setOnClickListener(v -> {
            layoutReadMore.setVisibility(View.GONE);
            layoutQuiz.setVisibility(View.VISIBLE);
            btnReadMore.setVisibility(View.GONE);
            btnStartTest.setVisibility(View.GONE);
            loadQuestion();
        });

        btnNext.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestion() {
        txtQuestion.setText(questions[currentQuestion][0]);

        option1.setText(questions[currentQuestion][1]);
        option1.setTag(questions[currentQuestion][1]);

        option2.setText(questions[currentQuestion][2]);
        option2.setTag(questions[currentQuestion][2]);
    }

    private void nextQuestion() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return;

        RadioButton selected = findViewById(selectedId);
        String answer = selected.getTag().toString();

        score.put(answer, score.get(answer) + 1);
        currentQuestion++;

        if (currentQuestion < questions.length) {
            radioGroup.clearCheck();
            loadQuestion();
        } else {
            showResult();
        }
    }

    private void showResult() {

        String dominant = "";
        int max = 0;

        for (String key : score.keySet()) {
            if (score.get(key) > max) {
                max = score.get(key);
                dominant = key;
            }
        }

        txtResult.setVisibility(View.VISIBLE);
        txtResult.setText("Sua linguagem dominante é: " + dominant);

        radioGroup.setVisibility(View.GONE);
        btnNext.setEnabled(false);

        saveLoveLanguage(dominant);
    }

    private void saveLoveLanguage(String language) {

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .update("loveLanguage", language)
                .addOnSuccessListener(unused -> loadPartnerLanguage());
    }

    private void loadPartnerLanguage() {

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) return;

                    String partnerId = documentSnapshot.getString("partnerId");

                    if (partnerId == null) return;

                    db.collection("users")
                            .document(partnerId)
                            .get()
                            .addOnSuccessListener(partnerDoc -> {

                                if (!partnerDoc.exists()) return;

                                String partnerLanguage =
                                        partnerDoc.getString("loveLanguage");

                                if (partnerLanguage != null) {

                                    txtResult.append(
                                            "\n\nLinguagem do seu parceiro: "
                                                    + partnerLanguage
                                    );
                                }
                            });
                });
    }
}