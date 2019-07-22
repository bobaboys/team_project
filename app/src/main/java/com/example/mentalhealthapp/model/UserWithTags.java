package com.example.mentalhealthapp.model;

import com.parse.Parse;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UserWithTags {
    public ParseUser user;
    public ArrayList<String> tags;
    public  UserWithTags(){
        tags = new ArrayList<>();
    }
}
