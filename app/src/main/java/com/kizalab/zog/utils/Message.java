package com.kizalab.zog.utils;

import java.io.Serializable;

public class Message implements Serializable {

    private String name;
    private String phone;
    private String senderId;
    private String date;
    private String message;

    public Message(String name, String phone, String senderId, String date, String message) {
        this.name = name;
        this.phone = phone;
        this.senderId = senderId;
        this.date = date;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
