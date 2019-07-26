package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mentalhealthapp.adapters.ChatsListAdapter;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Chat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatOverviewListFragment  extends Fragment {

    List<Chat> chats;
    RecyclerView rvChatsList;
    ChatsListAdapter chatListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChatsList = view.findViewById(R.id.rvChatsList);
        chats = new ArrayList<>();
        setRecyclerView();
        getListUrlChats();

    }

    private void setRecyclerView() {
        chatListAdapter = new ChatsListAdapter(this.getContext(), chats);
        rvChatsList.setAdapter(chatListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        rvChatsList.setLayoutManager(layoutManager);
    }

    public ArrayList<String> getListUrlChats() {
        ParseQuery<Chat> query = new ParseQuery<Chat>(Chat.class);
        query.setLimit(50);
        query.include("helper");
        query.include("reciever");
        boolean isHelper= ParseUser.getCurrentUser().getBoolean("helper");
        query.whereEqualTo(isHelper?"helper":"reciever",ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> objects, ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                    return;
                }
                for(Chat c: objects){
                    chats.add(0, c);
                }
                //chats.addAll(objects);
                chatListAdapter.notifyDataSetChanged();
            }
        });

        return null;
    }
    private void orderChatsByTimestamp(){

    }
}
