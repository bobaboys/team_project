package com.example.mentalhealthapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("SearchOptions")
public class SearchOptions extends ParseObject {

    public String getTagString(){
        return getString(Constants.TAG_FIELD);
    }
}
