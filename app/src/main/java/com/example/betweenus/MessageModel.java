package com.example.betweenus;

import com.google.firebase.Timestamp;

public class MessageModel {

    private String text;
    private String senderId;
    private com.google.firebase.Timestamp timestamp;
    private String status;

    public MessageModel() {}

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}