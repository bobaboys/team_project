package com.example.mentalhealthapp.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.SelectedTagsAdapter;
import com.example.mentalhealthapp.models.Chat;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Utils.Utils;
import chatApp.ChatApp;
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;


public class HelperDetailsActivity extends AppCompatActivity {
    public TextView helperBio;
    public GridView helperTags;
    public TextView textHelperTags;
    public TextView textHelperBio;
    public TextView helperUsername;
    public Button openChat;
    public ImageView helperAvatarPic;
    public ParseUser clickedHelper;
    private GroupChannel groupChannel;
    public ArrayList<String> allHelperTags = new ArrayList<>();
    public SelectedTagsAdapter profTagAdapter;
    protected MediaPlayer buttonClickSound;


    public void setGroupChannel(GroupChannel groupChannel) {
        this.groupChannel = groupChannel;
    }


    private ConnectionHandle connectToServerCallback= new  ConnectionHandle(){
        @Override
        public void onSuccess(String TAG, User user){
            //call new intent to start chat
            Log.d(TAG, "Connection successful with user: " + user);
        }
        @Override
        public void onFailure(String TAG, Exception e){
            Log.e(TAG,"Chat connection failed");
            e.printStackTrace();
            Toast.makeText(HelperDetailsActivity.this, "Chat failed!", Toast.LENGTH_LONG).show();
        }
    };

    private FindCallback<HelperTags>getHelperDetailsCallback = new FindCallback<HelperTags>() {
        @Override
        public void done(List<HelperTags> objects, ParseException e) {
            if(e==null){
                for(int i = 0; i < objects.size(); i++){
                    Object strTag;
                    HelperTags helperTag = objects.get(i);
                    strTag = ((Tag)helperTag.get("Tag")).get("Tag");
                    allHelperTags.add(strTag.toString());
                }
                profTagAdapter = new SelectedTagsAdapter(HelperDetailsActivity.this, allHelperTags);
                helperTags.setAdapter(profTagAdapter);
            }else{
                Log.e("HelperProfileFragment", "failure in populating tags");
            }
        }
    };

    private FindCallback<Chat> optionalCreateChatParse= new FindCallback<Chat>() {
        @Override
        public void done(List<Chat> objects, ParseException e) {
            if(objects.size() == 0){// no rows, create new one.
                createChatParse();
            }else {// there is a row with the same info. skip create a new one.
                openChatFragment();
            }
        }
    };


    private SaveCallback saveChatParseCallback= new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if(e==null){
                Log.d("USERS", clickedHelper.getUsername());
                openChatFragment();
            }else{
                e.printStackTrace();
            }
        }
    };


    public View.OnClickListener openChatBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(HelperDetailsActivity.this, R.anim.bounce);
            openChat.startAnimation(animation);
            ChatApp.createChat(ParseUser.getCurrentUser(), clickedHelper, false, new CreateChatHandle() {
                @Override
                public void onSuccess(String TAG, final GroupChannel groupChannel) {
                    // Check if row already exist
                    HelperDetailsActivity.this.setGroupChannel(groupChannel);
                    queryParseChats( optionalCreateChatParse);
                }

                @Override
                public void onFailure(String TAG, Exception e) {
                    Log.e("OPEN CHAT:", "chat open failed");
                    e.printStackTrace();
                    Toast.makeText(HelperDetailsActivity.this, "Can't open chat", Toast.LENGTH_LONG).show();
                }
            });
        }
    };


    private void queryParseChats(FindCallback<Chat> findCallback){
        ParseQuery<Chat> query  = new ParseQuery<Chat>(Chat.class);
        query.whereEqualTo("chatUrl",groupChannel.getUrl());
        query.findInBackground(findCallback);
    }

    public void createChatParse(){
        Chat chat =  new Chat();
        long time = new Date().getTime();
        chat.put("helper", ParseObject.createWithoutData("_User", clickedHelper.getObjectId()) );
        chat.put("reciever", ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId()));
        chat.put("chatUrl", groupChannel.getUrl());
        chat.put(Constants.CHAT_RECEIVER_DELETED,false);
        chat.put("lastChecked",time);
        chat.put("lastCheckedHelper",time );
        chat.put(Constants.CHAT_HELPER_DELETED, false);
        chat.saveInBackground(saveChatParseCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonClickSound = MediaPlayer.create(this, R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        groupChannel = null;
        setContentView(R.layout.activity_helper_details);

        assignViewsAndListeners();
        //now we want to query for all of the tags for the current user and display it under helperTags
        populateDetails();
        ChatApp.getInstance().startChatApp(this);
        connectUserToChat();
    }

    private void assignViewsAndListeners() {
        helperUsername = findViewById(R.id.tvHelperUsername_HelperDetails);
        helperBio = findViewById(R.id.tvBio_helperdetails);
        helperTags = findViewById(R.id.gvHelperTags_helperDetails);
        textHelperBio = findViewById(R.id.tvHelperBioText_HelperDetails);
        textHelperTags = findViewById(R.id.tvHelpersTagsText_HelperDetails);
        openChat = findViewById(R.id.btnChat_helperdetails);
        openChat.setOnClickListener(openChatBtnListener);
        helperAvatarPic = findViewById(R.id.ivHelperDetails);
    }

    public void populateDetails(){
        clickedHelper = (ParseUser) getIntent().getParcelableExtra("clicked_bio");
        helperBio.setText(clickedHelper.getString(Constants.HELPER_BIO_FIELD));
        helperUsername.setText(clickedHelper.getString(Constants.NAME_FIELD));
        Bitmap bm = Utils.convertFileToBitmap(clickedHelper.getParseFile(Constants.AVATAR_FIELD));
        helperAvatarPic.setImageBitmap(bm);
        queryHelperTags(getHelperDetailsCallback);
    }


    private void queryHelperTags(FindCallback<HelperTags> findCallback){
        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include(Constants.USER_FIELD);
        query.include(Constants.TAG_FIELD);
        query.whereEqualTo(Constants.USER_FIELD,clickedHelper);
        query.findInBackground(findCallback);
    }


    private void connectUserToChat() {
        //connects logged in or new user to chat server
        String currUserObjID = ParseUser.getCurrentUser().getObjectId();
        final ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer(currUserObjID, connectToServerCallback);
    }


    public void openChatFragment(){
        Intent intent = new Intent(HelperDetailsActivity.this, OpenChatActivity.class);
        intent.putExtra("clicked_helper",clickedHelper);
        intent.putExtra("group_channel", groupChannel.getUrl());
        startActivity(intent);
        Log.d("OPEN CHAT:", "chat open successful");
    }

}
