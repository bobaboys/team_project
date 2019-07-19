package com.example.mentalhealthapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.sendbird.android.SendBird;

public class HelperChatsFragment extends Fragment {

    protected TextView testChat;
    private String APP_ID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testChat = view.findViewById(R.id.chatTest);
        startChatApp(getContext());
    }

    public  void startChatApp(Context context){
        APP_ID = context.getString(R.string.APP_CHAT_ID);
        SendBird.init(APP_ID, context);
        //This api is called using only our secret App ID.
    }
}
