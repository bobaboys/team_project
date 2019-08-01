package com.example.mentalhealthapp.adapters;

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
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.OpenChatActivity;
import com.example.mentalhealthapp.models.Chat;
import com.example.mentalhealthapp.models.CompleteChat;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import org.parceler.Parcels;

import java.util.List;

import Utils.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder>  {

    List<CompleteChat> channels;
    Context context;

    public ChatsListAdapter(Context context, List<CompleteChat> channels) {

        this.context = context;
        this.channels = channels;
    }


    @NonNull
    @Override
    public ChatsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_overview, viewGroup, false);
        return new ChatsListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ChatsListAdapter.ViewHolder viewHolder, final int i) {
        CompleteChat chat = channels.get(i);
        viewHolder.bind(chat);
    }


    @Override
    public int getItemCount() {
        if (channels == null) {
            return 0;
        }
        return channels.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, lastMessage, timeStamp;
        ImageView chatUserPic;
        ConstraintLayout itemChat;
        CompleteChat channel;
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
            if(channel.chat == null) return; // there is not chatParse on the item, click does nothing. TODO toast?
            if(addresseeParse==null)addresseeParse = obtainFromParseAddressee();
            Intent i = new Intent(context, OpenChatActivity.class);
            i.putExtra("clicked_helper", addresseeParse);
            i.putExtra("group_channel", channel.groupChannel.getUrl());//TODO
            context.startActivity(i);

        }


        public void bind(final CompleteChat complete ) {

            channel = complete;  //This Chat object is gonna be called in other methods

            // getting the adressee information (in parseServer) to populate the chat overview.
            addresseeParse = obtainFromParseAddressee();
            title.setText( .getUsername()); // Username comes from Parse.

            getAndBindParseProfilePhoto( addresseeParse);
            BaseMessage  lastM = channel.groupChannel.getLastMessage();
            bindAccordingTypeOfMessage( lastM,  addresseeParse);
        }

        public ParseUser obtainFromParseAddressee() {
            boolean currentIsHelperParse = ParseUser.getCurrentUser().getBoolean("helper");
             return currentIsHelperParse? channel.chat.getParseUser("reciever") : channel.chat.getParseUser("helper") ;
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

        public void bindAccordingTypeOfMessage(BaseMessage lastM, ParseUser addresseeParse){
            if(lastM!=null){
                try{
                    UserMessage lastMUser = (UserMessage) lastM;
                    String senderId = lastMUser.getSender().getUserId();
                    String authorAndMessage = senderId.equals(ParseUser.getCurrentUser().getObjectId())
                            ? "You: " :
                            addresseeParse.getUsername()+": ";
                    authorAndMessage += lastMUser.getMessage();
                    lastMessage.setText(authorAndMessage);
                }catch (ClassCastException e){
                     // Message is an Attachment
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
