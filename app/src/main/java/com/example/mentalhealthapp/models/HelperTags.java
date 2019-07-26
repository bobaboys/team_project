package com.example.mentalhealthapp.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("HelperTags")
public class HelperTags extends ParseObject {
    public final String USER_KEY = "user";
    public final String TAG_KEY = "Tag";

    public ParseUser getUser(){
        return getParseUser(USER_KEY);
    }

    public String getTag(){
        return getString(TAG_KEY);
    }

    public void setHelperTags(ParseUser user, Tag tag){
        put(USER_KEY, user);
        put(TAG_KEY, ParseObject.createWithoutData(Constants.SEARCH_OPTIONS_CLASS, tag.getObjectId()));
    }
}
