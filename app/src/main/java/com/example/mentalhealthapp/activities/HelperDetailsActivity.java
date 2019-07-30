package com.example.mentalhealthapp.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Chat;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.User;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

import Utils.Utils;
import chatApp.ChatApp;
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;


public class HelperDetailsActivity extends AppCompatActivity {
    public TextView helperBio;
    public TextView helperTags;
    public TextView textHelperTags;
    public TextView textHelperBio;
    public TextView helperUsername;
    public Button openChat;
    public ImageView helperAvatarPic;
    public ParseUser clickedHelper;
    ParseUser currentUser = ParseUser.getCurrentUser();

    public View.OnClickListener openChatBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            ChatApp.createChat(currentUser, clickedHelper, false, new CreateChatHandle() {
                @Override
                public void onSuccess(String TAG, final GroupChannel groupChannel) {
                    // Check if row already exist
                    ParseQuery<Chat> query  = new ParseQuery<Chat>(Chat.class);
                    query.whereEqualTo("chatUrl",groupChannel.getUrl());
                    query.findInBackground(new FindCallback<Chat>() {
                        @Override
                        public void done(List<Chat> objects, ParseException e) {
                            if(objects.size() == 0){// no rows, create new one.
                                createChatParse(groupChannel);
                            }else {// there is a row with the same info. skip create a new one.
                                openChatFragment(groupChannel);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(String TAG, Exception e) {
                    Log.e("OPEN CHAT:", "chat open failed");
                    e.printStackTrace();
                    Toast.makeText(HelperDetailsActivity.this, "can't open chat", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    public void createChatParse(final GroupChannel groupChannel){
        Chat chat =  new Chat();
        chat.put("helper", ParseObject.createWithoutData("_User", clickedHelper.getObjectId()) );
        chat.put("reciever", ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId()));
        chat.put("chatUrl", groupChannel.getUrl());

        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Log.d("USERS", clickedHelper.getUsername());
                    openChatFragment(groupChannel);
                }else{
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        helperTags = findViewById(R.id.tvTags_helperdetails);
        textHelperBio = findViewById(R.id.tvHelperBioText_HelperDetails);
        textHelperTags = findViewById(R.id.tvHelpersTagsText_HelperDetails);
        openChat = findViewById(R.id.btnChat_helperdetails);
        openChat.setOnClickListener(openChatBtnListener);
        helperAvatarPic = findViewById(R.id.ivHelperDetails);
    }

    public void populateDetails(){
        clickedHelper = (ParseUser) getIntent().getParcelableExtra("clicked_bio");
        helperBio.setText(clickedHelper.getString(Constants.HELPER_BIO_FIELD));
        helperUsername.setText(clickedHelper.getString(Constants.USERNAME_FIELD));
        ParseFile avatarFile = clickedHelper.getParseFile(Constants.AVATAR_FIELD);
        Bitmap bm = Utils.convertFileToBitmap(avatarFile);
        helperAvatarPic.setImageBitmap(bm);
        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include("user");
        query.include("Tag");
        query.whereEqualTo("user", clickedHelper);
        query.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                String strListOfTags = "";
                if(e==null){
                    for(int i = 0; i < objects.size(); i++){
                        HelperTags helperTag = objects.get(i);
                        //second to last tag
                        if(i == objects.size() - 2){
                            strListOfTags += ((Tag)helperTag.get("Tag")).get("Tag")+ ", and ";
                            continue;
                        }
                        //last tag
                        if(i == objects.size() - 1){
                            strListOfTags += ((Tag)helperTag.get("Tag")).get("Tag");
                            break;
                        }
                        strListOfTags += ((Tag)helperTag.get("Tag")).get("Tag")+ ", ";
                        //TODO POPULATE WITH CARDS INSTEAD OF STR ONLY
                    }
                    helperTags.setText(strListOfTags);

                }else{
                    Log.e("HelperDetailsActivity", "failure");
                }
            }
        });
    }

    private void connectUserToChat() {
        //connects logged in or new user to chat server
        String currUserObjID = currentUser.getObjectId();
        final ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer(currUserObjID, new  ConnectionHandle(){
            @Override
            public void onSuccess(String TAG, User user){
                //call new intent to start chat
                Log.d(TAG, "Connection successful with user: " + user);
                Toast.makeText(HelperDetailsActivity.this, "Chat connection successful!", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(String TAG, Exception e){
                Log.e(TAG,"Chat connection failed");
                e.printStackTrace();
                Toast.makeText(HelperDetailsActivity.this, "Chat failed!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openChatFragment(GroupChannel groupChannel){
        Intent intent = new Intent(HelperDetailsActivity.this, OpenChatActivity.class);
        intent.putExtra("clicked_helper",clickedHelper);
        //pass current group channel url to next activity
        String groupChannelUrl = groupChannel.getUrl();
        intent.putExtra("group_channel", groupChannelUrl);
        startActivity(intent);
        Log.d("OPEN CHAT:", "chat open successful");
    }

}
