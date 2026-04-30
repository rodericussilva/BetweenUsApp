package com.example.betweenus;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageView imgBack, imgPartner, btnSend;
    private EditText edtMessage;
    private RecyclerView recyclerChat;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String myUid;
    private String coupleId;
    private String partnerId;

    private ArrayList<MessageModel> list;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        myUid = auth.getCurrentUser().getUid();

        imgBack = findViewById(R.id.imgBack);
        imgPartner = findViewById(R.id.imgPartner);
        btnSend = findViewById(R.id.btnSend);
        edtMessage = findViewById(R.id.edtMessage);
        recyclerChat = findViewById(R.id.recyclerChat);

        list = new ArrayList<>();
        adapter = new ChatAdapter(this, list, myUid);

        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        imgBack.setOnClickListener(v -> finish());

        loadData();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadData() {

        db.collection("users")
                .document(myUid)
                .get()
                .addOnSuccessListener(doc -> {

                    coupleId = doc.getString("coupleId");
                    partnerId = doc.getString("partnerId");

                    loadPartnerPhoto();
                    listenMessages();
                });
    }

    private void loadPartnerPhoto() {

        db.collection("users")
                .document(partnerId)
                .get()
                .addOnSuccessListener(doc -> {

                    String photo = doc.getString("photoUrl");

                    if (photo != null && !photo.isEmpty()) {

                        Glide.with(this)
                                .load(photo)
                                .circleCrop()
                                .into(imgPartner);
                    }
                });
    }

    private void sendMessage() {

        String msg = edtMessage.getText().toString().trim();

        if (msg.isEmpty()) return;

        btnSend.setEnabled(false);

        Map<String, Object> map = new HashMap<>();
        map.put("text", msg);
        map.put("senderId", myUid);
        map.put("timestamp", FieldValue.serverTimestamp());
        map.put("status", "sending");

        db.collection("couples")
                .document(coupleId)
                .collection("chat")
                .add(map)
                .addOnSuccessListener(documentReference -> {

                    documentReference.update("status", "sent");

                    edtMessage.setText("");
                    btnSend.setEnabled(true);

                })
                .addOnFailureListener(e -> {

                    btnSend.setEnabled(true);

                    Toast.makeText(
                            ChatActivity.this,
                            "Erro ao enviar",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void listenMessages() {

        db.collection("couples")
                .document(coupleId)
                .collection("chat")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(
                                ChatActivity.this,
                                "Erro ao carregar mensagens",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (value == null) return;

                    list.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        MessageModel msg = doc.toObject(MessageModel.class);

                        if (msg != null) {

                            list.add(msg);

                            if (!msg.getSenderId().equals(myUid)) {
                                doc.getReference().update("status", "read");
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (list.size() > 0) {
                        recyclerChat.scrollToPosition(list.size() - 1);
                    }
                });
    }
}