package com.example.mentalhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HelperDetails extends AppCompatActivity {

    public Button openChat;
    public View.OnClickListener openChatbtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HelperDetails.this,OpenChatActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_details);

        openChat = findViewById(R.id.btnChat_helperdetails);
        openChat.setOnClickListener(openChatbtnListener);

    }
}
