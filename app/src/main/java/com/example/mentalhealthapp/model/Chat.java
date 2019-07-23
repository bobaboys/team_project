package com.example.mentalhealthapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Chat")
public class Chat extends ParseObject {
     String chatUrl;
     ParseUser reciever;
     ParseUser helper;

    public Chat(){}

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public ParseUser getReciever() {
        return reciever;
    }

    public void setReciever(ParseUser reciever) {
        this.reciever = reciever;
    }

    public ParseUser getHelper() {
        return helper;
    }

    public void setHelper(ParseUser helper) {
        this.helper = helper;
    }
}
