package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.SendBird;
import com.sendbird.android.Sender;
import com.sendbird.android.UserMessage;

import java.io.File;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.internal.Util;

public class ChatsFragmentAdapter extends RecyclerView.Adapter<ChatsFragmentAdapter.ViewHolder> {
    private Context context;
    private List<BaseMessage> messages;
    RecyclerView rvOpenChat;
    boolean isMyMessage, isTextMessage;
    private ParseUser addressee;

    public ChatsFragmentAdapter(Context context, List<BaseMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<BaseMessage> list) {
        messages.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_types_message, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        BaseMessage message = messages.get(i);
        isTextMessage=message.getMentionType().name().equals("USERS");
        if(isTextMessage){
            UserMessage userMessage = (UserMessage) message;
            Sender sender = userMessage.getSender();
            isMyMessage = isThisMyMessage(sender);
            viewHolder.myAudioLayout.setVisibility(LinearLayout.GONE);
            viewHolder.yourAudioLayout.setVisibility(LinearLayout.GONE);
            if(isMyMessage){
                viewHolder.myMessageLayout.setVisibility(LinearLayout.VISIBLE);
                viewHolder.yourMessageLayout.setVisibility(LinearLayout.GONE);
            }else{
                viewHolder.myMessageLayout.setVisibility(LinearLayout.GONE);
                viewHolder.yourMessageLayout.setVisibility(LinearLayout.VISIBLE);
            }

        }else {
            viewHolder.yourMessageLayout.setVisibility(LinearLayout.GONE);
            viewHolder.myMessageLayout.setVisibility(LinearLayout.GONE);

            FileMessage fileMessage = (FileMessage) message;
            Sender sender = fileMessage.getSender();
            isMyMessage = isThisMyMessage(sender);
            if(isMyMessage){

                viewHolder.myAudioLayout.setVisibility(LinearLayout.VISIBLE);
                viewHolder.yourAudioLayout.setVisibility(LinearLayout.GONE);
            }else{
                viewHolder.myAudioLayout.setVisibility(LinearLayout.GONE);
                viewHolder.yourAudioLayout.setVisibility(LinearLayout.VISIBLE);
            }
        }
        viewHolder.bind(message);

    }


    public boolean isThisMyMessage(Sender sender) {
        String senderId = sender.getUserId();
        String currentUserId = SendBird.getCurrentUser().getUserId();
        return  senderId.equals(currentUserId);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public void setAddressee(ParseUser addressee) {
        this.addressee = addressee;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView body, name, myBody;
        public ImageView profileSender, playYou, playMine;
        public SeekBar sbYour, sbMine;
        LinearLayout myMessageLayout, yourMessageLayout, yourAudioLayout, myAudioLayout;
        FileMessage fileMessage;


        View.OnClickListener audioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String audioUrl =  fileMessage.getUrl();
                Utils.Utils.playAudioFromUrl(audioUrl, context);
            }
        };

        public ViewHolder(View view) {
            super(view);
            myMessageLayout = view.findViewById(R.id.ly_my_msg);
            yourMessageLayout = view.findViewById(R.id.ly_sender_msg);
            yourAudioLayout = view.findViewById(R.id.ly_your_audio);
            myAudioLayout = view.findViewById(R.id.ly_my_audio);

            rvOpenChat = itemView.findViewById(R.id.rv_open_chat);

            name = view.findViewById(R.id.tv_author_message);
            profileSender = view.findViewById(R.id.iv_profile_pic_message);
            myBody = view.findViewById(R.id.my_message_body);
            body = view.findViewById(R.id.message_body);

            playYou = view.findViewById(R.id.iv_play_your_audio);
            playMine = view.findViewById(R.id.iv_play_my_audio);
            sbYour = view.findViewById(R.id.sb_your_audio);
            sbMine = view.findViewById(R.id.sb_my_audio);

            playMine.setOnClickListener(audioListener);
            playYou.setOnClickListener(audioListener);
        }


        public void bind(final BaseMessage message){
            if(isTextMessage){
                UserMessage um = (UserMessage) message;
                bindTextMsg(um);
            }else{
                //TODO SEND AUDIO, RECIEVE AUDIO, PLAY AUDIO. HOW I STORE THE URI OF THE AUDIO? HOW DO I PLAY IT?
                fileMessage = (FileMessage) message;
            }


        }

        private void bindTextMsg(UserMessage message){
            if(isMyMessage){
                myBody.setText(message.getMessage());
            }else{
                body.setText(message.getMessage());
                if(addressee==null)return;
                name.setText(addressee.getUsername());
                try {
                    ParseFile avatarPic = addressee.getParseFile("avatar");
                    Glide.with(context)
                            .load(avatarPic.getFile())
                            .bitmapTransform(new RoundedCornersTransformation(context, 50, 0))
                            .into(profileSender);
                }catch (ParseException e){
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
