package com.example.mentalhealthapp.models;

import com.parse.ParseUser;

import java.util.ArrayList;

public class UserWithTags {
    public ParseUser user;
    public ArrayList<String> tags;
    public  UserWithTags(){
        tags = new ArrayList<>();
    }
}
