package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.activities.OpenChatActivity;
import com.example.mentalhealthapp.models.Chat;
import com.example.mentalhealthapp.models.CompleteChat;
import com.example.mentalhealthapp.models.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.UserMessage;

import java.util.List;

import Utils.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder>  {

    private List<CompleteChat> channels;
    private Context context;

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
        if (channels == null) return 0;
        return channels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, lastMessage, timeStamp;
        private ImageView chatUserPic;
        private ConstraintLayout itemChat;
        private CompleteChat channel;
        private ParseUser addresseeParse;
        private ImageView noReadedNotf;
        private boolean isCurrentHelper;


        View.OnLongClickListener eraseChatListener = new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(final View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Warning!");
                alert.setMessage("You are about to delete this chat. Do you want to continue?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isHelper = ParseUser.getCurrentUser().getBoolean("helper");
                        hideChatForUser(isHelper ?
                                    Constants.CHAT_HELPER_DELETED :Constants.CHAT_RECEIVER_DELETED);
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            }
        };

        View.OnClickListener openChatListener =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat();
            }
        };

        public ViewHolder(View view) {
            super(view);
            isCurrentHelper = ParseUser.getCurrentUser().getBoolean("helper");
            setComponents(view);
            itemChat.setOnClickListener(openChatListener);
            itemChat.setOnLongClickListener(eraseChatListener);
        }

        private void setComponents(View view){
            title = view.findViewById(R.id.tv_chat_title);
            lastMessage = view.findViewById(R.id.tv_chat_lastMessage);
            timeStamp = view.findViewById(R.id.tv_chat_timestamp);
            chatUserPic = view.findViewById(R.id.iv_chat_image);
            itemChat = view.findViewById(R.id.item_chat);
            noReadedNotf = view.findViewById(R.id.iv_circle_unreaded_message);
        }

        private void hideChatForUser(final String whichHelperDeleted) {
            ParseQuery<Chat> query = new ParseQuery<Chat>(Chat.class);
            query.whereEqualTo("chatUrl", channel.getChat().getString("chatUrl"));
            query.findInBackground(new FindCallback<Chat>() {
                @Override
                public void done(List<Chat> objects, ParseException e) {
                    Chat currChat = objects.get(0);
                    currChat.put(whichHelperDeleted, true);
                    currChat.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            channels.remove(channel);
                            notifyDataSetChanged();
                        }
                    });

                }
            });
        }

        //change to private for this class
        //public methods on top
        public void openChat(){
            if(channel.getChat() == null) return; // there is not chatParse on the item, click does nothing. TODO toast?
            if(addresseeParse==null)addresseeParse = obtainFromParseAddressee();
            Intent i = new Intent(context, OpenChatActivity.class);
            i.putExtra("clicked_helper", addresseeParse);
            i.putExtra("group_channel", channel.getGroupChannel().getUrl());//TODO
            context.startActivity(i);
        }


        public void bind(final CompleteChat complete ) {
            channel = complete;
            addresseeParse = obtainFromParseAddressee();
            title.setText(addresseeParse.getUsername());
            getAndBindParseProfilePhoto( addresseeParse);
            BaseMessage  lastM = channel.getGroupChannel().getLastMessage();
            bindAccordingTypeOfMessage( lastM,  addresseeParse, channel.getChat().getLong(isCurrentHelper?
                    "lastCheckedHelper" : "lastChecked" ));
        }

        public ParseUser obtainFromParseAddressee() {
             return isCurrentHelper?
                     channel.getChat().getParseUser("reciever") :
                     channel.getChat().getParseUser("helper") ;
        }

        public void getAndBindParseProfilePhoto( ParseUser addresseeParse){
            if(addresseeParse==null)return;
            try {
                //get pic from parse user and set image view
                ParseFile avatarPic = addresseeParse.getParseFile("avatar");
                Glide.with(context)
                        .load(avatarPic.getFile())
                        .bitmapTransform(new RoundedCornersTransformation(context, 120, 10))
                        .into(chatUserPic);
            }catch (ParseException e){
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        public void bindAccordingTypeOfMessage(BaseMessage lastM, ParseUser addresseeParse, long lastChecked){
            if(lastM==null) {
                lastMessage.setText("");
                timeStamp.setText("");
                return;
            }
            boolean isMyMessage;
            try{
                isMyMessage=setTextUserMessage(lastM);
            }catch (ClassCastException e){
                isMyMessage=setTextFileMessage(lastM);
            }
            timeStamp.setText(Utils.getTimeStamp(lastM.getCreatedAt()));
            if(lastM.getCreatedAt() > lastChecked &&  ! isMyMessage) bindMessageNotRead();
            else bindMessageRead();
        }

        private boolean setTextUserMessage(BaseMessage lastM){
            UserMessage lastMUser = (UserMessage) lastM;
            boolean isMyMessage =lastMUser.getSender().getUserId().equals(ParseUser.getCurrentUser().getObjectId());
            String authorAndMessage = isMyMessage
                    ? "You: " :
                    addresseeParse.getUsername()+": ";
            authorAndMessage += lastMUser.getMessage();
            lastMessage.setText(authorAndMessage);
            return  isMyMessage;
        }


        private boolean setTextFileMessage(BaseMessage lastM){
            // Message is an Attachment
            FileMessage lastMFile = (FileMessage) lastM;
            boolean isMyMessage =lastMFile.getSender().getUserId().equals(ParseUser.getCurrentUser().getObjectId());
            String authorAndAttachMessage = isMyMessage
                    ? "You sent an attachment" :
                    addresseeParse.getUsername()+" sent an attachment";
            lastMessage.setText(authorAndAttachMessage);
            return  isMyMessage;
        }


        private void bindMessageNotRead(){
            timeStamp.setTextColor(context.getResources().getColor(R.color.black));
            lastMessage.setTextColor(context.getResources().getColor(R.color.black));
            timeStamp.setTypeface(null, Typeface.BOLD);
            lastMessage.setTypeface(null, Typeface.BOLD);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            noReadedNotf.setVisibility(ConstraintLayout.VISIBLE);
        }

        private void bindMessageRead(){
            timeStamp.setTextColor(context.getResources().getColor(android.R.color.secondary_text_light));
            timeStamp.setTypeface(null, Typeface.NORMAL);
            lastMessage.setTypeface(null, Typeface.NORMAL);
            lastMessage.setTextColor(context.getResources().getColor(android.R.color.secondary_text_light));
            noReadedNotf.setVisibility(ConstraintLayout.GONE);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        }
    }
}
