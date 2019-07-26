package com.example.mentalhealthapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.ChatsFragmentAdapter;
import com.parse.Parse;
import com.parse.ParseUser;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import chatApp.ChatApp;
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;
import chatApp.GetStringHandle;

public class OpenChatActivity extends AppCompatActivity {

    EditText chatRecipient;
    EditText chatMessage;
    Button sendBtn;
    ParseUser chatRecipientUser;
    RecyclerView rv_chatBubbles;
    ChatsFragmentAdapter chatAdapter;
    ArrayList<UserMessage> messages;
    Context context;
    GroupChannel groupChannel;
    String groupChannelStr;

    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //send message to server
            final String chatMessageString = chatMessage.getText().toString();
            //find correct chat from group channel string
            groupChannel.sendUserMessage(chatMessageString, new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if(e!=null){
                        Toast.makeText(OpenChatActivity.this,"Message not sent",Toast.LENGTH_LONG ).show();
                        e.printStackTrace();
                        return;
                    }
                    //You add this new message to the lowest part of the chat
                    messages.add(userMessage);
                    chatAdapter.notifyItemInserted(messages.size()-1);
                    rv_chatBubbles.scrollToPosition(messages.size()-1);
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_chat);

        groupChannelStr = getIntent().getStringExtra("group_channel");
        messages = new ArrayList<>();

        assignViewsAndListener();
        setRecyclerView();
        getConnectionAndChat();
    }


    private void getConnectionAndChat(){
        ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer(ParseUser.getCurrentUser().getObjectId(), new ConnectionHandle() {
            @Override
            public void onSuccess(String TAG, User user) {
                //find correct chat from group channel string
                ChatApp.getChat(groupChannelStr, new CreateChatHandle() {
                    @Override
                    public void onSuccess(String TAG, GroupChannel groupChannel) {
                        OpenChatActivity.this.groupChannel = groupChannel;
                        Toast.makeText(OpenChatActivity.this, "group channel found successfully", Toast.LENGTH_LONG).show();
                        populateChat(groupChannel);
                        sendBtn.setClickable(true); // now you are able to click and send messages.
                        setChannelHandler(groupChannelStr);
                    }

                    @Override
                    public void onFailure(String TAG, Exception e) {
                        Log.e("OPEN_CHAT_ACTIVITY", "accessing channel failed");
                        e.printStackTrace();
                        //TODO CASE FAILURE GET CHAT?
                    }
                });
            }

            @Override
            public void onFailure(String TAG, Exception e) {
                e.printStackTrace();
                //TODO offline view ?
            }
        });
    }


    private void setChannelHandler(String groupChannelStr){
        SendBird.addChannelHandler(groupChannelStr, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                //TODO
                if (baseMessage.getMentionType().name().equals("USERS")) {
                    UserMessage userM = (UserMessage)baseMessage;
                    // You add to the lowest position of the chat this new message.
                    messages.add(userM);
                    chatAdapter.notifyItemInserted(messages.size()-1);
                    rv_chatBubbles.scrollToPosition(messages.size() -1 );
                }
            }
        });
    }


    private void assignViewsAndListener() {
        chatRecipient = findViewById(R.id.etRecipient_OpenChat);
        chatRecipientUser = (ParseUser) getIntent().getParcelableExtra("clicked_helper");
        chatRecipient.setText(chatRecipientUser.getUsername());
        chatMessage = findViewById(R.id.etChatBox_openChat);
        sendBtn = findViewById(R.id.btnChatboxSend_openChat);
        sendBtn.setClickable(false); // protects clicks before enable connection
        rv_chatBubbles = findViewById(R.id.rv_open_chat);
        sendBtn.setOnClickListener(sendBtnListener);
    }

    protected void setRecyclerView() {
        Log.d("setRecyclerView", "attaching adapter");
        chatAdapter = new ChatsFragmentAdapter(this, messages);
        chatAdapter.setAddressee(chatRecipientUser);
        rv_chatBubbles.setAdapter(chatAdapter);
        rv_chatBubbles.setLayoutManager(new LinearLayoutManager(context));
    }
    //TODO LISTENER NEW MESSAGES.

    protected void populateChat(GroupChannel groupChannel) {
        //TODO
        PreviousMessageListQuery prevMessageListQuery = groupChannel.createPreviousMessageListQuery();
        prevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult( List<BaseMessage> ms, SendBirdException e) {
                if (e != null) {    // Error.
                    e.printStackTrace();
                    return;
                }
                for (BaseMessage message : ms) {
                    if (message.getMentionType().name().equals("USERS")) {
                        UserMessage userM = (UserMessage)message;
                        //The first messages on ms are the newest. therefore, if you add all of them at 0,
                        //the newest gonna "fall" to the tail of the recycler view.
                        // the tail (POSITION N-1) of the recycler view is the lowest part of the rv.
                        //we wanna watch first the newest ( lowest) messages.
                        messages.add(0,userM);
                        chatAdapter.notifyItemInserted(0);
                        rv_chatBubbles.scrollToPosition(messages.size() -1 );
                    }
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        SendBird.removeChannelHandler(groupChannelStr);
        super.onDestroy();
    }
}
