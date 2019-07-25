package com.example.mentalhealthapp.models;

import android.app.Application;

<<<<<<< HEAD:app/src/main/java/com/example/mentalhealthapp/models/ParseApp.java
=======
import com.example.mentalhealthapp.model.Chat;
import com.example.mentalhealthapp.model.Tag;
>>>>>>> 19ecb5cddd05d56f6b1eb0f59a879163a2ad934b:app/src/main/java/com/example/mentalhealthapp/ParseApp.java
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();

        ParseObject.registerSubclass(HelperTags.class);
        ParseObject.registerSubclass(Tag.class);
        ParseObject.registerSubclass(Chat.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("alex-elena-michelle")
                .clientKey("javier")
                .server("http://fbu-team-project.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }

}
