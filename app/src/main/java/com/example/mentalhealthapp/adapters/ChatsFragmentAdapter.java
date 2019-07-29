package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.os.VibrationEffect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Chat;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sendbird.android.SendBird;
import com.sendbird.android.Sender;
import com.sendbird.android.UserMessage;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ChatsFragmentAdapter extends RecyclerView.Adapter<ChatsFragmentAdapter.ViewHolder> {
    private Context context;
    private List<UserMessage> messages;
    RecyclerView rvOpenChat;
    boolean isMyMessage;
    private ParseUser addressee;

    public ChatsFragmentAdapter(Context context, List<UserMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<UserMessage> list) {
        messages.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.both_messages, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        UserMessage message = messages.get(i);
        Sender sender = message.getSender();
        String senderId = sender.getUserId();
        String currentUserId = SendBird.getCurrentUser().getUserId();
        isMyMessage = senderId.equals(currentUserId);
        if(isMyMessage){
            viewHolder.myMessageLayout.setVisibility(LinearLayout.VISIBLE);
            viewHolder.yourMessageLayout.setVisibility(LinearLayout.GONE);
        }else{
            viewHolder.myMessageLayout.setVisibility(LinearLayout.GONE);
            viewHolder.yourMessageLayout.setVisibility(LinearLayout.VISIBLE);
        }
        viewHolder.bind(message);
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
        public ImageView profileSender;
        LinearLayout myMessageLayout, yourMessageLayout;

        public ViewHolder(View view) {
            super(view);
            myMessageLayout = view.findViewById(R.id.ly_my_msg);
            yourMessageLayout = view.findViewById(R.id.ly_sender_msg);

            rvOpenChat = itemView.findViewById(R.id.rv_open_chat);

            name = view.findViewById(R.id.tv_author_message);
            profileSender = view.findViewById(R.id.iv_profile_pic_message);
            myBody = view.findViewById(R.id.my_message_body);
            body = view.findViewById(R.id.message_body);
        }


        public void bind(final UserMessage message){
            if(isMyMessage){
                myBody.setText(message.getMessage());
            }else{
                body.setText(message.getMessage());
                if(addressee==null)return;
                name.setText(addressee.getUsername());
                try {

                    int radius = 50; // corner radius, higher value = more rounded
                    int margin = 0; // crop margin, set to 0 for corners with no crop
                    //get pic from parse user and set image view
                    ParseFile avatarPic = addressee.getParseFile("avatar");
                    Glide.with(context)
                            .load(avatarPic.getFile())
                            .bitmapTransform(new RoundedCornersTransformation(context, radius, margin))
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
