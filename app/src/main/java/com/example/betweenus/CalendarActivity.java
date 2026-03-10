package com.example.betweenus;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerEvents;
    private Button btnAddEvent;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String coupleId;
    private String selectedDate;

    private List<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        recyclerEvents = findViewById(R.id.recyclerEvents);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new EventAdapter(eventList);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerEvents.setAdapter(adapter);

        loadCouple();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

            loadEventsOfDay(selectedDate);

        });

        btnAddEvent.setOnClickListener(v -> showAddEventDialog());
    }

    private void loadCouple() {

        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    coupleId = doc.getString("coupleId");

                });
    }

    private void showAddEventDialog() {

        if (selectedDate == null) {

            Toast.makeText(this,
                    "Selecione uma data primeiro",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        EditText input = new EditText(this);
        input.setHint("Título do evento");

        new AlertDialog.Builder(this)
                .setTitle("Novo Evento")
                .setView(input)
                .setPositiveButton("Salvar", (dialog, which) -> {

                    String title = input.getText().toString();

                    saveEvent(title);

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void saveEvent(String title) {

        Event event = new Event(title, selectedDate);

        db.collection("couples")
                .document(coupleId)
                .collection("events")
                .add(event)
                .addOnSuccessListener(doc -> {

                    Toast.makeText(this,
                            "Evento criado",
                            Toast.LENGTH_SHORT).show();

                    loadEventsOfDay(selectedDate);

                });
    }

    private void loadEventsOfDay(String date) {

        if (coupleId == null) return;

        db.collection("couples")
                .document(coupleId)
                .collection("events")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(query -> {

                    eventList.clear();

                    for (var doc : query.getDocuments()) {

                        Event event = doc.toObject(Event.class);

                        eventList.add(event);
                    }

                    adapter.notifyDataSetChanged();

                });
    }
}