package com.example.mentalhealthapp.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;


@ParseClassName("User")
public class User extends ParseUser {

    public static final String KEY_HELPER = "helper";

    User(){ }

    public boolean isHelper(){
        return getBoolean(KEY_HELPER);
    }

}
