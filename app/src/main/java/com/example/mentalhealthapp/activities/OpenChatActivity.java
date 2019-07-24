package com.example.mentalhealthapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.parse.ParseUser;
import com.sendbird.android.GroupChannel;

import org.parceler.Parcels;

import chatApp.ChatApp;
import chatApp.CreateChatHandle;
import chatApp.GetStringHandle;

public class OpenChatActivity extends AppCompatActivity {

    EditText chatRecipient;
    EditText chatMessage;
    Button sendBtn;
    ParseUser chatRecipientUser;
    private String APP_ID;
    ParseUser currentUser = ParseUser.getCurrentUser();


    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //send message to server
            final String chatMessageString = chatMessage.getText().toString();
            String groupChannelStr = getIntent().getStringExtra("group_channel");
            //find correct chat from group channel string
            ChatApp.getChat(groupChannelStr, new CreateChatHandle() {
                @Override
                public void onSuccess(String TAG, GroupChannel groupChannel) {
                    Toast.makeText(OpenChatActivity.this,"group channel found successfully", Toast.LENGTH_LONG).show();

                    //sent message into that chat
                    ChatApp.sendMessageText(groupChannel, chatMessageString, new GetStringHandle() {
                        @Override
                        public void onSuccess(String TAG, String channelUrl) {
                            Toast.makeText(OpenChatActivity.this,"chat message sent successfully", Toast.LENGTH_LONG).show();
                            //update recycler view
                        }

                        @Override
                        public void onFailure(String TAG, Exception e) {
                            e.printStackTrace();
                            Log.e("OPEN_CHAT_ACTIVITY", "sending chat failed");
                        }
                    });
                }

                @Override
                public void onFailure(String TAG, Exception e) {
                    Log.e("OPEN_CHAT_ACTIVITY", "accessing channel failed");
                    e.printStackTrace();
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chat);
        assignViewsAndListener();
    }

    private void assignViewsAndListener() {
        chatRecipient = findViewById(R.id.etRecipient_OpenChat);
        chatRecipientUser = Parcels.unwrap(getIntent().getParcelableExtra("clicked_helper"));
        chatRecipient.setText(chatRecipientUser.getUsername());
        chatMessage = findViewById(R.id.etChatBox_openChat);
        sendBtn = findViewById(R.id.btnChatboxSend_openChat);
        sendBtn.setOnClickListener(sendBtnListener);
    }


}
