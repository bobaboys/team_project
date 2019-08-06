package com.example.mentalhealthapp.models;

import com.sendbird.android.GroupChannel;

public class CompleteChat{
    private Chat chat;
    private GroupChannel groupChannel;
    private long timestampLast;
    public CompleteChat(){}

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public GroupChannel getGroupChannel() {
        return groupChannel;
    }

    public void setGroupChannel(GroupChannel groupChannel) {
        this.groupChannel = groupChannel;
    }

    public long getTimestampLast() {
        return timestampLast;
    }

    public void setTimestampLast(long timestampLast) {
        this.timestampLast = timestampLast;
    }
}