package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.HelperDetailsActivity;
import com.parse.ParseUser;
import com.sendbird.android.SendBird;
import com.sendbird.android.UserMessage;

import java.util.List;

public class ChatsFragmentAdapter extends RecyclerView.Adapter<ChatsFragmentAdapter.ViewHolder> {
    private Context context;
    private List<UserMessage> messages;
    RecyclerView rvOpenChat;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_messages, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        UserMessage message = messages.get(i);
        viewHolder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView myMessage;
        TextView theirMessage;

        public ViewHolder(View view) {
            super(view);
            rvOpenChat = itemView.findViewById(R.id.rv_open_chat);
            myMessage = itemView.findViewById(R.id.messages_Sender);
            theirMessage = itemView.findViewById(R.id.messages_receiver);
            //rvOpenChat.setVisibility(View.GONE);
        }

/*        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position!=RecyclerView.NO_POSITION){
                ParseUser bio = bios.get(position);
                Intent intent = new Intent(context, HelperDetailsActivity.class);
//                intent.putExtra("clicked_bio", Parcels.wrap(bio));
                context.startActivity(intent);
            }
        }*/


        public void bind(final UserMessage message){
//            ParseUser user = ParseUser.getCurrentUser();
//            SendBird.getCurrentUser();
            if(message.getSender().equals(SendBird.getCurrentUser())){
                myMessage.setVisibility(View.VISIBLE);
                myMessage.setText(message.getMessage());
                theirMessage.setVisibility(View.GONE);
            }else{
                theirMessage.setVisibility(View.VISIBLE);
                theirMessage.setText(message.getMessage());
                myMessage.setVisibility(View.GONE);
            }
        }
    }
}
