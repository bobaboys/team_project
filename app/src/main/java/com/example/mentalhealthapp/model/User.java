package com.example.mentalhealthapp.model;

import com.parse.ParseClassName;
import com.parse.ParseUser;


@ParseClassName("User")
public class User extends ParseUser {

    public static final String KEY_HELPER = "helper";

    User(){ }

    public boolean getHelper(){
        return getBoolean(KEY_HELPER);
    }

}
