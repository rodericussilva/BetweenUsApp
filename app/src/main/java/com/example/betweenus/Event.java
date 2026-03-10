package com.example.betweenus;

public class Event {

    public String id;
    public String title;
    public String date;
    public String time;
    public String type;

    public Event() {}

    public Event(String title, String date, String time, String type) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.type = type;
    }
}