package com.example.betweenus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

        if (holder instanceof SentHolder) {

            SentHolder sentHolder = (SentHolder) holder;

            // texto da mensagem
            sentHolder.txtMessage.setText(msg.getText());

            // status visual estilo WhatsApp
            String status = msg.getStatus();

            if ("sending".equals(status)) {

                sentHolder.imgStatus.setImageResource(R.drawable.ic_clock);

            } else if ("sent".equals(status)) {

                sentHolder.imgStatus.setImageResource(R.drawable.ic_check_one);

            } else if ("delivered".equals(status)) {

                sentHolder.imgStatus.setImageResource(R.drawable.ic_check_two);

            } else if ("read".equals(status)) {

                sentHolder.imgStatus.setImageResource(R.drawable.ic_check_read);

            } else if ("error".equals(status)) {

                sentHolder.imgStatus.setImageResource(R.drawable.ic_error);

            } else {

                sentHolder.imgStatus.setImageResource(R.drawable.ic_clock);
            }

        } else {

            ReceivedHolder receivedHolder = (ReceivedHolder) holder;

            receivedHolder.txtMessage.setText(msg.getText());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SentHolder extends RecyclerView.ViewHolder {

        TextView txtMessage;
        ImageView imgStatus;

        public SentHolder(@NonNull View itemView) {
            super(itemView);

            txtMessage = itemView.findViewById(R.id.txtMessageSent);
            imgStatus = itemView.findViewById(R.id.imgStatus);
        }
    }

    static class ReceivedHolder extends RecyclerView.ViewHolder {

        TextView txtMessage;

        public ReceivedHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessageReceived);
        }
    }
}