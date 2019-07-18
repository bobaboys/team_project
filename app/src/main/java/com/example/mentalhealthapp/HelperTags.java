package com.example.mentalhealthapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("HelperTags")
public class HelperTags extends ParseObject {
    public final String USER_KEY = "user";
    public final String COLOR_KEY = "Color";

    public ParseUser getUser(){
        return getParseUser(USER_KEY);
    }

    public String getColor(){
        return getString(COLOR_KEY);
    }

    public void setHelperTags(ParseUser user, String color){
        put(USER_KEY, user);
        put(COLOR_KEY, color);
    }
}
