package com.example.mentalhealthapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.activities.OpenChatActivity;
import com.example.mentalhealthapp.models.Chat;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import java.util.List;

import Utils.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
        ConstraintLayout itemChat;
        String chatUrl;
        Chat chatParse;
        ParseUser addresseeParse;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.tv_chat_title);
            lastMessage = view.findViewById(R.id.tv_chat_lastMessage);
            timeStamp = view.findViewById(R.id.tv_chat_timestamp);
            chatUserPic = view.findViewById(R.id.iv_chat_image);
            itemChat = view.findViewById(R.id.item_chat);

            itemChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChat();
                }
            });
        }


        public void openChat(){
            if(chatParse == null) return; // there is not chatParse on the item, click does nothing. TODO toast?
            if(chatUrl == null) chatUrl= chatParse.getString("chatUrl");
            if(addresseeParse==null)addresseeParse = obtainFromParseAddressee();
            Intent i = new Intent(context, OpenChatActivity.class);
            i.putExtra("group_channel", chatUrl );
            i.putExtra("clicked_helper", addresseeParse);
            context.startActivity(i);

        }


        public void bind(final Chat chat) {

            chatParse = chat;  //This Chat object is gonna be called in other methods.
            chatUrl= chatParse.getString("chatUrl");

            //Method called when want to select an specific channel
            GroupChannel.getChannel(chatUrl, new GroupChannel.GroupChannelGetHandler() {

                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {

                    if (e != null) {    // Error.
                        e.printStackTrace();
                        Log.d("CHAT_OVERVIEW","Imposible populate chat overview");
                        return;
                    }

                    // getting the adressee information (in parseServer) to populate the chat overview.
                    addresseeParse = obtainFromParseAddressee();
                    title.setText(addresseeParse.getUsername()); // Username comes from Parse.

                    getAndBindParseProfilePhoto( addresseeParse);
                    BaseMessage  lastM = groupChannel.getLastMessage();
                    bindAccordingTypeOfMessage( lastM,  addresseeParse);
                }
            });
        }

        public ParseUser obtainFromParseAddressee() {
            boolean currentIsHelperParse = ParseUser.getCurrentUser().getBoolean("helper");
             return currentIsHelperParse? chatParse.getParseUser("reciever") : chatParse.getParseUser("helper") ;
        }

        public void getAndBindParseProfilePhoto( ParseUser addresseeParse){

            if(addresseeParse==null)return;

            try {
                int radius = 30; // corner radius, higher value = more rounded
                int margin = 10; // crop margin, set to 0 for corners with no crop
                //get pic from parse user and set image view
                ParseFile avatarPic = addresseeParse.getParseFile("avatar");
                Glide.with(context)
                        .load(avatarPic.getFile())
                        .bitmapTransform(new RoundedCornersTransformation(context, radius, margin))
                        .into(chatUserPic);
            }catch (ParseException e){
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }


        public Member getAdressee(List<Member> members, String id){
            for(Member m: members){
                if(m.getUserId().equals(id)){
                    return m;
                }
            }
            return null;
        }

        public void bindAccordingTypeOfMessage(BaseMessage lastM, ParseUser addresseeParse){
            if(lastM!=null){
                if( lastM.getMentionType().name().equals("USERS")){ // Message is a Text Message.
                    UserMessage lastMUser = (UserMessage) lastM;
                    String senderId = lastMUser.getSender().getUserId();
                    String authorAndMessage = senderId.equals(ParseUser.getCurrentUser().getObjectId())
                            ? "You: " :
                            addresseeParse.getUsername()+": ";
                    authorAndMessage += lastMUser.getMessage();
                    lastMessage.setText(authorAndMessage);
                }else{ // Message is an Attachment
                    FileMessage lastMFile = (FileMessage) lastM;
                    String senderId = lastMFile.getSender().getUserId();
                    String authorAndAttachMessage = senderId.equals(ParseUser.getCurrentUser().getObjectId())
                            ? "You Sent an attachment" :
                            addresseeParse.getUsername()+" sent an attachment";
                    lastMessage.setText(authorAndAttachMessage);
                }
                timeStamp.setText(Utils.getTimeStamp(lastM.getCreatedAt()));
            }else{//There is not messages in the conversation.
                lastMessage.setText("");
                timeStamp.setText("");
            }
        }
    }
}
