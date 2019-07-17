package com.example.mentalhealthapp;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("alex-elena-michelle")
                .clientKey("javier")
                .server("http://fbu-team-project.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }

}
