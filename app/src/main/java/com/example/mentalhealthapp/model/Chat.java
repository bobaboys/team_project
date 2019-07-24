package com.example.mentalhealthapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Chat")
public class Chat extends ParseObject {
    public String getChatObjectId(){
        return getString("objectId");
    }

    public String getChatHelper(){
        return getString("helper");
    }

    public void setChatHelper(String helper){
        put("helper", helper);
    }

    public String getReceiver(){
        return getString("reciever");
    }

    public void setChatReceiver(String receiver){
        put("reciever", receiver);
    }

    public String getChatUrl(){
        return getString("chatUrl");
    }
}
