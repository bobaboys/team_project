package com.example.mentalhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;


public class HelperDetails extends AppCompatActivity {
    public TextView helperBio;
    public TextView helperTags;
    public Button openChat;
    public ParseUser clickedHelper;
    public final String HELPER_BIO_FIELD = "helperBio";
    public View.OnClickListener openChatBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HelperDetails.this, OpenChatActivity.class);
            intent.putExtra("clicked_helper",Parcels.wrap(clickedHelper));
            startActivity(intent);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_details);

        assignViewsAndListeners();
        //now we want to query for all of the tags for the current user and display it under helperTags
        populateBioAndTags();
    }

    private void assignViewsAndListeners() {
        helperBio = findViewById(R.id.tvBio_helperdetails);
        helperTags = findViewById(R.id.tvTags_helperdetails);
        openChat = findViewById(R.id.btnChat_helperdetails);
        openChat.setOnClickListener(openChatBtnListener);
    }



    public void populateBioAndTags(){

        clickedHelper = (ParseUser) Parcels.unwrap(getIntent().getParcelableExtra("clicked_bio"));
        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include("user");
        query.whereEqualTo("user", clickedHelper);
        query.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                String colors = "";
                if(e==null){
                    for(HelperTags tag : objects){
                        colors = colors + tag.getTag() + " ";
                    }
                    helperTags.setText(colors);
                    helperBio.setText(clickedHelper.getString(HELPER_BIO_FIELD));

                }else{
                    Log.e("HelperDetails", "failure");
                }
            }
        });
    }
}
