package com.example.betweenus;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        adapter = new EventAdapter(eventList, new EventAdapter.OnEventActionListener() {

            @Override
            public void onEdit(Event event) {

                showEditEventDialog(event);

            }

            @Override
            public void onDelete(Event event) {

                confirmDeleteEvent(event);

            }
        });

        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerEvents.setAdapter(adapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

            loadEventsOfDay(selectedDate);

        });

        loadCouple();

        btnAddEvent.setOnClickListener(v -> showAddEventDialog());

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        selectedDate = year + "-" + (month + 1) + "-" + day;
    }

    private void loadCouple() {

        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        coupleId = doc.getString("coupleId");

                        if (coupleId != null) {

                            loadEventsOfDay(selectedDate);

                        } else {

                            Toast.makeText(this,
                                    "Conta de casal não encontrada",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Erro ao carregar dados do casal",
                            Toast.LENGTH_LONG).show();
                });
    }

    private void showAddEventDialog() {

        if (selectedDate == null) {
            Toast.makeText(this, "Selecione uma data", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_event, null);

        EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
        EditText inputTime = dialogView.findViewById(R.id.inputTime);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        new AlertDialog.Builder(this)
                .setTitle("Novo Evento")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {

                    String title = inputTitle.getText().toString();
                    String time = inputTime.getText().toString();
                    String type = spinnerType.getSelectedItem().toString();

                    saveEvent(title, time, type);

                })
                .setNegativeButton("Cancelar", null)
                .show();

        String[] types = {
                "Encontro ❤️",
                "Aniversário 🎂",
                "Viagem ✈",
                "Cinema 🍿",
                "Jantar 🍷",
                "Outro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                types
        );

        spinnerType.setAdapter(adapter);
    }

    private void saveEvent(String title, String time, String type) {

        if (coupleId == null) {
            Toast.makeText(this, "Conta do casal ainda não carregada", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> event = new HashMap<>();

        event.put("title", title);
        event.put("date", selectedDate);
        event.put("time", time);
        event.put("type", type);

        event.put("description", "");
        event.put("location", "");
        event.put("notify", "");
        event.put("repeat", "");

        event.put("createdAt", FieldValue.serverTimestamp());
        event.put("createdBy", auth.getCurrentUser().getUid());

        db.collection("couples")
                .document(coupleId)
                .collection("events")
                .add(event)
                .addOnSuccessListener(doc -> {

                    Toast.makeText(this, "Evento salvo", Toast.LENGTH_SHORT).show();

                    loadEventsOfDay(selectedDate);

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Erro ao salvar evento", Toast.LENGTH_LONG).show();

                });
    }

    private void loadEventsOfDay(String date) {

        if (coupleId == null || date == null) return;

        db.collection("couples")
                .document(coupleId)
                .collection("events")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(query -> {

                    eventList.clear();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        Event event = doc.toObject(Event.class);

                        if (event != null) {

                            event.id = doc.getId();
                            eventList.add(event);
                        }
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Erro ao carregar eventos",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showEventOptions(Event event) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(event.title);

        String[] options = {"✏️ Editar evento", "🗑 Excluir evento"};

        builder.setItems(options, (dialog, which) -> {

            if (which == 0) {
                showEditEventDialog(event);
            }

            if (which == 1) {
                confirmDeleteEvent(event);
            }

        });

        builder.show();
    }

    private void confirmDeleteEvent(Event event) {

        new AlertDialog.Builder(this)
                .setTitle("Excluir evento")
                .setMessage("Tem certeza que deseja excluir este evento?")
                .setPositiveButton("Excluir", (dialog, which) -> {

                    deleteEvent(event);

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteEvent(Event event) {

        db.collection("couples")
                .document(coupleId)
                .collection("events")
                .document(event.id)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(this, "Evento excluído", Toast.LENGTH_SHORT).show();

                    loadEventsOfDay(selectedDate);

                });
    }

    private void showEditEventDialog(Event event) {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_event, null);

        EditText inputTitle = dialogView.findViewById(R.id.inputTitle);
        EditText inputTime = dialogView.findViewById(R.id.inputTime);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        inputTitle.setText(event.title);
        inputTime.setText(event.time);

        String[] types = {
                "Encontro ❤️",
                "Aniversário 🎂",
                "Viagem ✈",
                "Cinema 🍿",
                "Jantar 🍷",
                "Outro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                types
        );

        spinnerType.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle("Editar Evento")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {

                    String title = inputTitle.getText().toString();
                    String time = inputTime.getText().toString();
                    String type = spinnerType.getSelectedItem().toString();

                    updateEvent(event, title, time, type);

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateEvent(Event event, String title, String time, String type) {

        db.collection("couples")
                .document(coupleId)
                .collection("events")
                .document(event.id)
                .update(
                        "title", title,
                        "time", time,
                        "type", type
                )
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(this, "Evento atualizado", Toast.LENGTH_SHORT).show();

                    loadEventsOfDay(selectedDate);

                });
    }
}