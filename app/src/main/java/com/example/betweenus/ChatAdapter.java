package com.example.betweenus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int SENT = 1;
    private final int RECEIVED = 2;

    private Context context;
    private List<MessageModel> list;
    private String myUid;

    public ChatAdapter(Context context, List<MessageModel> list, String myUid) {
        this.context = context;
        this.list = list;
        this.myUid = myUid;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getSenderId().equals(myUid)) {
            return SENT;
        } else {
            return RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENT) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel msg = list.get(position);

        // formatar hora
        String time = "";
        if (msg.getTimestamp() != null) {
            Date date = msg.getTimestamp().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            time = sdf.format(date);
        }

        if (holder instanceof SentHolder) {

            SentHolder h = (SentHolder) holder;

            h.txtMessage.setText(msg.getText());
            h.txtTime.setText(time);

            // STATUS estilo WhatsApp
            if ("sending".equals(msg.getStatus())) {
                h.txtStatus.setText("⏳");
            } else if ("sent".equals(msg.getStatus())) {
                h.txtStatus.setText("✓");
            } else if ("delivered".equals(msg.getStatus())) {
                h.txtStatus.setText("✓✓");
            } else if ("read".equals(msg.getStatus())) {
                h.txtStatus.setText("✓✓");
                h.txtStatus.setTextColor(0xFF4FC3F7); // azul
            }

        } else {

            ReceivedHolder h = (ReceivedHolder) holder;

            h.txtMessage.setText(msg.getText());
            h.txtTime.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SentHolder extends RecyclerView.ViewHolder {

        TextView txtMessage, txtTime, txtStatus;

        public SentHolder(@NonNull View itemView) {
            super(itemView);

            txtMessage = itemView.findViewById(R.id.txtMessageSent);
            txtTime = itemView.findViewById(R.id.txtTimeSent);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }

    static class ReceivedHolder extends RecyclerView.ViewHolder {

        TextView txtMessage, txtTime;

        public ReceivedHolder(@NonNull View itemView) {
            super(itemView);

            txtMessage = itemView.findViewById(R.id.txtMessageReceived);
            txtTime = itemView.findViewById(R.id.txtTimeReceived);
        }
    }
}