package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;

import org.parceler.Parcels;

import chatApp.ChatApp;
import chatApp.CreateChatHandle;
import chatApp.GetStringHandle;

public class OpenChatActivity extends AppCompatActivity {

    EditText chatRecipient;
    EditText chatMessage;
    Button sendBtn;
    ParseUser chatRecipientUser;

    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


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

    public void createChatAndFirstMessage(final ChatApp chatApp){
        chatApp.createChat( "newUser3",  "newUser2",false, new CreateChatHandle(){
            @Override
            public void onFailure(String TAG, Exception e){
                e.printStackTrace();
            }
            @Override
            public void onSuccess(String TAG, GroupChannel groupChannel){
                Log.d(TAG, "New conversation : ");
                message(chatApp, groupChannel);
                //chatApp.sendMessageText( groupChannel,   "Hola", final MasterHandle handle);
            }
        });

    }
    public void message(ChatApp chatApp,final GroupChannel chat){
        if(chat!=null){
            chatApp.sendMessageText(chat, "Hola amigos mios", new GetStringHandle() {
                @Override
                public void onSuccess(String TAG, String message) {
                    Log.d(TAG,"Message sent:"+ message);
                    SendBird.addChannelHandler(chat.getUrl(), new SendBird.ChannelHandler(){
                        @Override
                        public void onMessageReceived(BaseChannel var1, BaseMessage var2){

                        }
                    });
                }

                @Override
                public void onFailure(String TAG, Exception e) {
                    Log.e(TAG,"Message sent failure:");
                }
            });
        }
    }
}
