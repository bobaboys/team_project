package com.example.mentalhealthapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mentalhealthapp.OpenChatActivity;
import com.example.mentalhealthapp.R;
import com.parse.ParseUser;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;

import chatApp.ChatApp;
import chatApp.ConnectionHandle;

public class HelperChatsFragment extends Fragment {

    ParseUser currentUser = ParseUser.getCurrentUser();
    private String APP_CHAT_ID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = new Intent(getContext(), OpenChatActivity.class);

        intent.putExtra("group_channel",
                "sendbird_group_channel_129355554_09bc7db20f640928ed708b764866c07404c66860");
        startActivity(intent);
        return inflater.inflate(R.layout.fragment_helper_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startChatApp(getContext());
        connectUserToChat();
    }
    public  void startChatApp(Context context){
        APP_CHAT_ID = context.getString(R.string.APP_CHAT_ID);
        SendBird.init(APP_CHAT_ID, context);
    }
    private void connectUserToChat() {
        //connects logged in or new user to chat server
        String currUserObjID = currentUser.getObjectId();
        final ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(getContext());
        chatApp.connectToServer(currUserObjID, new  ConnectionHandle(){
            @Override
            public void onSuccess(String TAG, User user){
                //call new intent to start chat
                Log.d(TAG, "Connection successful with user: " + user);
                Toast.makeText(getContext(), "Chat connection successful!", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(String TAG, Exception e){
                Log.e(TAG,"Chat connection failed");
                e.printStackTrace();
                Toast.makeText(getContext(), "Chat failed!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
