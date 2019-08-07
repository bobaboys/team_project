package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.adapters.ChatsListAdapter;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Chat;
import com.example.mentalhealthapp.models.CompleteChat;
import com.example.mentalhealthapp.models.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;

import java.util.ArrayList;
import java.util.List;

import Utils.EndlessRecyclerViewScrollListener;
import chatApp.ChatApp;
import chatApp.CreateChatHandle;

public class ChatOverviewListFragment  extends Fragment {

    public static int LIMIT_QUERY = 25;
    private ArrayList<CompleteChat> completeChats;
    private RecyclerView rvChatsList;
    private ChatsListAdapter chatListAdapter;
    private LinearLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;
    private TextView altText;

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            completeChats.clear();
            chatListAdapter.notifyDataSetChanged();
            queryParseChats(1, querySBChats);
        }
    };


    FindCallback<Chat> querySBChats = new FindCallback<Chat>() {
        @Override
        public void done(List<Chat> objects, ParseException e) {
            if (e != null) {
                e.printStackTrace();
                return;
            }
            visibilityLayout(objects.size()==0);
            for (Chat chat : objects) {
                addNewCompleteChat(chat);
            }
            swipeContainer.setRefreshing(false);
        }
    };


    private  void addNewCompleteChat(Chat chat){
        final CompleteChat cc = new CompleteChat();
        cc.setChat(chat);
        ChatApp.getChat(chat.getString("chatUrl"), new CreateChatHandle() {
            @Override
            public void onSuccess(String TAG, GroupChannel groupChannel) {

                cc.setGroupChannel(groupChannel);
                addByTimestamp(completeChats, cc);
            }

            @Override
            public void onFailure(String TAG, Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void visibilityLayout(boolean isEmpty){
         rvChatsList.setVisibility(isEmpty?
                 ConstraintLayout.GONE : ConstraintLayout.VISIBLE);
          altText.setVisibility(isEmpty?
                  ConstraintLayout.VISIBLE : ConstraintLayout.GONE);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChatsList = view.findViewById(R.id.rvChatsList);
        altText = view.findViewById(R.id.tv_alt_no_chats);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(refreshListener);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        completeChats = new ArrayList<>();
        setRecyclerView();

        scrollListener.resetState();
        // The call to the parse server, list of chats is made at ONRESUME.

    }


    private void setRecyclerView() {
        chatListAdapter = new ChatsListAdapter(this.getContext(), completeChats);
        rvChatsList.setAdapter(chatListAdapter);
        layoutManager= new LinearLayoutManager(this.getContext());
        rvChatsList.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(totalItemsCount>=LIMIT_QUERY){
                    queryParseChats(page, querySBChats);
                }
                // If initially there was not enough items to fill the view  ( page 1)
                // there is not necesary call again the infinite scroll page. ( WE GOT LAST PAGE )
            }
        };
        rvChatsList.addOnScrollListener(scrollListener);
    }


    public void queryParseChats(int page, FindCallback<Chat> findCallback) {
        ParseQuery<Chat> query = new ParseQuery<Chat>(Chat.class);
        query.setLimit(LIMIT_QUERY);
        query.setSkip(50*(page-1));
        query.include("helper");
        query.include("reciever");
        boolean isHelper =ParseUser.getCurrentUser().getBoolean("helper");
        query.whereEqualTo(isHelper ?
                Constants.CHAT_HELPER_DELETED : Constants.CHAT_RECEIVER_DELETED, false);
        query.whereEqualTo(isHelper ?
                "helper":"reciever",ParseUser.getCurrentUser());
        scrollListener.resetState();
        query.findInBackground(findCallback);
    }


    private void addByTimestamp(ArrayList<CompleteChat> completeChats, CompleteChat chat){
        //TODO
        chat.setTimestampLast( getLastTimestampLong(chat.getGroupChannel()));
        int addAt=completeChats.size(); // default case, add to the tail. (or head if empty)

        for(int i=0;i<completeChats.size();i++){
            if(chat.getTimestampLast()>completeChats.get(i).getTimestampLast()){// bigger --> newer
                addAt = i;
                break;
            }
        }
        completeChats.add(addAt,chat);
        chatListAdapter.notifyItemInserted(addAt);
        rvChatsList.scrollToPosition(0);

    }


    private long getLastTimestampLong(GroupChannel gc){
        BaseMessage lastM = gc.getLastMessage();
        if(lastM==null) return 0;
        return lastM.getCreatedAt();
    }


    @Override
    public void onResume(){
        super.onResume();
        completeChats.clear();
        chatListAdapter.notifyDataSetChanged();
        queryParseChats(1, querySBChats);
    }
}
