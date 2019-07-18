package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HelperDetails extends AppCompatActivity {

    TextView helperBio;
    TextView helperTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_details);

        helperBio = findViewById(R.id.tvBio_helperdetails);
        helperTags = findViewById(R.id.tvTags_helperdetails);

        //now we want to query for all of the tags for the current user and display it under helperTags
        populateTags();
    }

    public void populateTags(){

        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include("user");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                String colors = "";
                if(e==null){
                    for(HelperTags tag : objects){
                        colors = colors + tag.getColor() + " ";
                    }
                    helperTags.setText(colors);

                }else{
                    Log.e("HelperDetails", "failure");
                }
            }
        });
    }
}
