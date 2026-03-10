package com.example.betweenus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events;
    private OnEventActionListener listener;

    public EventAdapter(List<Event> events, OnEventActionListener listener) {
        this.events = events;
        this.listener = listener;
    }

    public interface OnEventActionListener {
        void onEdit(Event event);
        void onDelete(Event event);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textTime;
        TextView textType;

        ImageView btnEdit;
        ImageView btnDelete;

        public EventViewHolder(View view) {
            super(view);

            textTitle = view.findViewById(R.id.textTitle);
            textTime = view.findViewById(R.id.textTime);
            textType = view.findViewById(R.id.textType);

            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        Event event = events.get(position);

        holder.textTitle.setText(event.title);
        holder.textTime.setText(event.time);
        holder.textType.setText(event.type);

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(event);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}