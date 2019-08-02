package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    ArrayList<CompleteChat> completeChats;
    RecyclerView rvChatsList;
    ChatsListAdapter chatListAdapter;
    LinearLayoutManager layoutManager;
    EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvChatsList = view.findViewById(R.id.rvChatsList);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                completeChats.clear();
                chatListAdapter.notifyDataSetChanged();
                getCompleteChats(0);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        completeChats = new ArrayList<>();
        setRecyclerView();
        completeChats.clear();
        chatListAdapter.notifyDataSetChanged();
        scrollListener.resetState();
        getCompleteChats(1);//TODO EXPLAIN THIS. STARTS IN 0? IN 1?

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
                    getCompleteChats(page);
                }
                // If initially there was not enough items to fill the view  ( page 1)
                // there is not necesary call again the infinite scroll page. ( WE GOT LAST PAGE )
            }
        };
        rvChatsList.addOnScrollListener(scrollListener);
    }

    public void getCompleteChats(int page) {
        ParseQuery<Chat> query = new ParseQuery<Chat>(Chat.class);
        query.setLimit(LIMIT_QUERY);
        query.setSkip(50*(page-1));
        query.include("helper");
        query.include("reciever");
        if(MainActivity.HelperYes){
            query.whereEqualTo(Constants.CHAT_HELPER_DELETED, false);
        }
        else{
            query.whereEqualTo(Constants.CHAT_RECEIVER_DELETED, false);
        }
        query.whereEqualTo(MainActivity.HelperYes?"helper":"reciever",ParseUser.getCurrentUser());
        scrollListener.resetState();
        query.findInBackground(new FindCallback<Chat>() {
            @Override
            public void done(List<Chat> objects, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                for (Chat chat : objects) {
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
                swipeContainer.setRefreshing(false);
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
        if(lastM==null) return 0;
        return lastM.getCreatedAt();
    }
}
