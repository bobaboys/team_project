package com.example.mentalhealthapp;

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

import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
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
    private String APP_ID;
    ParseUser currentUser = ParseUser.getCurrentUser();
    GroupChannel groupChannel;


    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(OpenChatActivity.this, "button clicked successfully", Toast.LENGTH_LONG);
            //send message to server
            final String chatMessageString = chatMessage.getText().toString();
            //sent message into that chat
            ChatApp.sendMessageText(groupChannel, chatMessageString, new GetStringHandle() {
                @Override
                public void onSuccess(String TAG, String channelUrl) {
                    Toast.makeText(OpenChatActivity.this, "chat message sent successfully", Toast.LENGTH_LONG).show();
                    //update recycler view

                }

                @Override
                public void onFailure(String TAG, Exception e) {
                    e.printStackTrace();
                    Log.e("OPEN_CHAT_ACTIVITY", "sending chat failed");
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chat);
        messages = new ArrayList<>();
        assignViewsAndListener();
        setRecyclerView();

        ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer(currentUser.getObjectId(), new ConnectionHandle() {
            @Override
            public void onSuccess(String TAG, User user) {
                String groupChannelStr = "sendbird_group_channel_129355554_09bc7db20f640928ed708b764866c07404c66860";
                //find correct chat from group channel string
                ChatApp.getChat(groupChannelStr, new CreateChatHandle() {
                    @Override
                    public void onSuccess(String TAG, GroupChannel groupChannel) {
                        OpenChatActivity.this.groupChannel = groupChannel;
                        Toast.makeText(OpenChatActivity.this, "group channel found successfully", Toast.LENGTH_LONG).show();
                        populateChat(groupChannel);

                    }

                    @Override
                    public void onFailure(String TAG, Exception e) {
                        Log.e("OPEN_CHAT_ACTIVITY", "accessing channel failed");
                        e.printStackTrace();
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

    private void assignViewsAndListener() {
        chatRecipient = findViewById(R.id.etRecipient_OpenChat);
        chatRecipientUser = Parcels.unwrap(getIntent().getParcelableExtra("clicked_helper"));
        //chatRecipient.setText(chatRecipientUser.getUsername());
        chatMessage = findViewById(R.id.etChatBox_openChat);
        sendBtn = findViewById(R.id.btnChatboxSend_openChat);
        rv_chatBubbles = findViewById(R.id.rv_OpenChat);
        sendBtn.setOnClickListener(sendBtnListener);
    }

    protected void setRecyclerView() {
        Log.d("setRecyclerView", "attaching adapter");
        chatAdapter = new ChatsFragmentAdapter(this, messages);
        rv_chatBubbles.setAdapter(chatAdapter);
        rv_chatBubbles.setLayoutManager(new LinearLayoutManager(context));
    }

    protected void populateChat(GroupChannel groupChannel) {
//        SendBird.addChannelHandler(UNIQUE_HANDLER_ID, new SendBird.ChannelHandler() {
//            @Override
//            public void onMessageReceived(BaseChannel channel, BaseMessage message) {
//                if (message instanceof UserMessage) {
//                    messages.add((UserMessage) message);
//                    chatAdapter.notifyItemInserted(messages.size() - 1);
//                } else if (message instanceof FileMessage) {
//                    // send to adapter
//                }
//            }
//        });

        PreviousMessageListQuery prevMessageListQuery = groupChannel.createPreviousMessageListQuery();
        prevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult( List<BaseMessage> ms, SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }

                for (BaseMessage message : ms) {
                    if (message.getMentionType().name().equals("USERS")) {
                        UserMessage userm = (UserMessage)message;
                        messages.add(userm);
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                    }
                }

            }
        });
    }
}
