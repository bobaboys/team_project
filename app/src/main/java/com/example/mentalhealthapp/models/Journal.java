package com.example.mentalhealthapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Journal")
public class Journal extends ParseObject {
    public final String USER_KEY = "user";
    public final String JOURNAL_KEY = "journalEntry";
    public final String DATE_KEY = "date";

    public ParseUser getJournalUser(){
        return getParseUser(USER_KEY);
    }

    public String getJournalEntry(){
        return getString(JOURNAL_KEY);
    }

    public String getDate(){
        return getString(DATE_KEY);
    }

    public void setJournalEntry(String entry){
        put(JOURNAL_KEY, entry);
    }

    public void setDate(String date){
        put(DATE_KEY, date);
    }

    public void setJournalUser(ParseUser user){
        put(USER_KEY, user);
    }

}
