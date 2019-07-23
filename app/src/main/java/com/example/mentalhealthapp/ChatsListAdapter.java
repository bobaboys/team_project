package com.example.mentalhealthapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.model.Chat;
import com.example.mentalhealthapp.model.User;
import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Utils.Utils;
import chatApp.ChatApp;
import chatApp.CreateChatHandle;
import okhttp3.internal.Util;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder>  {

    List<Chat> chats;
    RecyclerView rvChats;
    Context context;
    public ChatsListAdapter(Context context, List<Chat> chats) {

        this.context = context;
        this.chats = chats;
    }


    @NonNull
    @Override
    public ChatsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_overview, viewGroup, false);
        return new ChatsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsListAdapter.ViewHolder viewHolder, final int i) {
        Chat chat = chats.get(i);
        viewHolder.bind(chat);
    }

    @Override
    public int getItemCount() {
        if (chats == null) {
            return 0;
        }
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, lastMessage, timeStamp;
        ImageView chatUserPic;

        public ViewHolder(View view) {
            super(view);
            title = itemView.findViewById(R.id.tv_chat_title);
            lastMessage = itemView.findViewById(R.id.tv_chat_lastMessage);
            timeStamp = itemView.findViewById(R.id.tv_chat_timestamp);
        }


        public void bind(final Chat chat) {
            //TODO with urlChat, call SBird, get GChannel, users and populate item
            //TODO dont get chats yet, dont know whys
            ChatApp.getChat(chat.getChatUrl(), new CreateChatHandle() {
                @Override
                public void onSuccess(String TAG, GroupChannel groupChannel) {

                    // getting the adressee information (in parseServer) to populate the chat overview.
                    boolean currentisHelperParse = ParseUser.getCurrentUser().getBoolean("helper");
                    ParseUser adresseeParse = currentisHelperParse? chat.getReciever(): chat.getHelper() ;
                    title.setText(adresseeParse.getUsername()); // Username comes from Parse.

                    //Wtih Adressee ParseUser get info from SendBird.
                    List<Member> members =groupChannel.getMembers();
                    Member adresseeSendB = getAdressee(members,adresseeParse.getObjectId());
                    //adresseeSendB.getProfileUrl() GLIDE
                    //TODO populate image, get lastMessage author and content.


                    BaseMessage  lastM = groupChannel.getLastMessage();
                    Map<String, List<String>> metadata = lastM.getAllMetaArray();
                    List<String> autors = metadata.get("author");
                    timeStamp.setText(Utils.getTimeStamp(lastM.getUpdatedAt()));
                    lastMessage.setText(lastM.getData());

                }

                @Override
                public void onFailure(String TAG, Exception e) {
                    Log.d(TAG,"Imposible populate chat overview");
                }
            });

        }

        public Member getAdressee(List<Member> members, String id){
            for(Member m: members){
                if(m.getUserId().equals(id)){
                    return m;
                }
            }
            return null;
        }
    }
}
