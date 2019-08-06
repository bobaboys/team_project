package com.example.mentalhealthapp.models;

import com.parse.ParseUser;

import java.util.ArrayList;

public class UserWithTags {


    private ParseUser user;
    private ArrayList<String> tags;

    public ParseUser getUser() {
        return user;
    }

    public void setUser(ParseUser user) {
        this.user = user;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public  UserWithTags(){
        tags = new ArrayList<>();
    }
}
