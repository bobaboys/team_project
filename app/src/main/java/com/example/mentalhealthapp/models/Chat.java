package com.example.mentalhealthapp.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Chat")
public class Chat extends ParseObject {

    public static final String HELPER_KEY = "helper";
    public static final String RECEIVER_KEY = "reciever";
    public static final String LAST_CHECKED_RECEIVER_KEY = "lastChecked";
    public static final String LAST_CHECKED_HELPER_KEY = "lastCheckedHelper";
    public static final String RECEIVER_DELETED_KEY = "receiverDeleted";
    public static final String HELPER_DELETED_KEY = "helperDeleted";

    public void setHelperKey(ParseUser helper){ put(HELPER_KEY, helper);}

    public void setReceiverKey(ParseUser receiver){ put(RECEIVER_KEY, receiver);}

    public void setLastCheckedReceiverKey(long lastCheckedReceiver){put(LAST_CHECKED_RECEIVER_KEY, lastCheckedReceiver);}

    public void setLastCheckedHelperKey(long lastCheckedHelper){put(LAST_CHECKED_HELPER_KEY, lastCheckedHelper);}

    public void setReceiverDeletedKey(boolean isReceiverDeleted){put(RECEIVER_DELETED_KEY, isReceiverDeleted);}

    public void setHelperDeletedKey(boolean isHelperDeletedKey){put(HELPER_DELETED_KEY, isHelperDeletedKey);}


    public Chat(){}

}
