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
import com.example.mentalhealthapp.models.CompleteChat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;

import java.util.ArrayList;
import java.util.List;

import chatApp.ChatApp;
import chatApp.CreateChatHandle;

public class ChatOverviewListFragment  extends Fragment {

    ArrayList<CompleteChat> completeChats;
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
        completeChats = new ArrayList<>();
        setRecyclerView();
        getCompleteChats();

    }

    private void setRecyclerView() {
        chatListAdapter = new ChatsListAdapter(this.getContext(), completeChats);
        rvChatsList.setAdapter(chatListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        rvChatsList.setLayoutManager(layoutManager);
    }

    public void getCompleteChats() {
        ParseQuery<Chat> query = new ParseQuery<Chat>(Chat.class);
        query.setLimit(50);
        query.include("helper");
        query.include("reciever");
        boolean isHelper= ParseUser.getCurrentUser().getBoolean("helper");
        query.whereEqualTo(isHelper?"helper":"reciever",ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> objects, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                for (Chat chat : objects) {
                    //TODO CALL CHANNEL and order by timestamp.
                    final CompleteChat cc = new CompleteChat();
                    cc.chat = chat;
                    ChatApp.getChat(chat.getString("chatUrl"), new CreateChatHandle() {
                        @Override
                        public void onSuccess(String TAG, GroupChannel groupChannel) {
                            cc.groupChannel = groupChannel;
                            addByTimestamp(completeChats, cc);
                        }

                        @Override
                        public void onFailure(String TAG, Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

            }
        });
    }
    private void addByTimestamp(ArrayList<CompleteChat> list, CompleteChat element){
        //TODO
        element.timestampLast = getLastTimestampLong(element.groupChannel);
        int index=list.size(); // default case, add to the tail. (or head if empty)

        for(int i=0;i<list.size();i++){
            if(element.timestampLast>list.get(i).timestampLast){// bigger --> newer
                index = i;
                break;
            }
        }

        list.add(index,element);
        chatListAdapter.notifyItemInserted(index);
        rvChatsList.scrollToPosition(0);
    }


    private long getLastTimestampLong(GroupChannel gc){
        BaseMessage lastM = gc.getLastMessage();
        if(lastM==null){
            return 0;
        }
        return lastM.getCreatedAt();

    }
}
