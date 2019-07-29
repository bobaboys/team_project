package com.example.mentalhealthapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.ChatsFragmentAdapter;
import com.example.mentalhealthapp.models.Constants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.FileMessageParams;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import chatApp.ChatApp;
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class OpenChatActivity extends AppCompatActivity {

    //EditText chatRecipient;
    EditText chatMessage;
    Button sendBtn;
    ParseUser chatRecipientUser;
    RecyclerView rv_chatBubbles;
    ChatsFragmentAdapter chatAdapter;
    ArrayList<BaseMessage> messages;
    GroupChannel groupChannel;
    String groupChannelStr;
    Boolean emergency;
    TextView senderName;
    ImageView profilePic, appLogo,back;
    ImageButton record;
    MediaRecorder mediaRecorder;
    String mFileName;


    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        //Send text msg btn Listener
        @Override
        public void onClick(View v) {

            final String chatMessageString = chatMessage.getText().toString();
            //find correct chat from group channel string
            groupChannel.sendUserMessage(chatMessageString, new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if(e!=null){
                        Toast.makeText(OpenChatActivity.this,"Message not sent",Toast.LENGTH_LONG ).show();
                        e.printStackTrace();
                        return;
                    }
                    //You add this new message to the lowest part of the chat
                    messages.add(userMessage);
                    chatAdapter.notifyItemInserted(messages.size()-1);
                    rv_chatBubbles.scrollToPosition(messages.size()-1);
                }
            });
        }
    };


    View.OnClickListener backListener =new View.OnClickListener() {
        // Back btn (top of the chat) listener
        @Override
        public void onClick(View v) {
            OpenChatActivity.super.onBackPressed();
        }
    };


    View.OnTouchListener recordListener = new View.OnTouchListener() {
        @Override

        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if(action==MotionEvent.ACTION_DOWN){
                startRecord();
            }else if(action==MotionEvent.ACTION_BUTTON_RELEASE){

                //stop recording
                if(mediaRecorder==null)return false;
                stopRecord(mediaRecorder);

                //send recording
                if(mFileName==null)return false;

                FileMessageParams fmp = new FileMessageParams();
                final File file = new File(mFileName);
                fmp.setFile(file).setFileName(mFileName);
                sendFileMsg(fmp);

                mFileName=null;
            }
            return false;
        }
    };


    private  void  sendFileMsg(FileMessageParams fmp ){
        groupChannel.sendFileMessage(fmp, new BaseChannel.SendFileMessageHandler() {
            @Override
            public void onSent(FileMessage fileMessage, SendBirdException e) {
                //TODO
                //You add this new message to the lowest part of the chat
                messages.add(fileMessage);
                chatAdapter.notifyItemInserted(messages.size()-1);
                rv_chatBubbles.scrollToPosition(messages.size()-1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_chat);
        emergency = false;
        if(getIntent().getStringExtra("emergency") != null){
            //coming from emergency button
            emergency = true;
        }

        groupChannelStr = getIntent().getStringExtra("group_channel");
        messages = new ArrayList<>();
        mediaRecorder=null;

        assignViewsAndListener();
        setRecyclerView();
        getConnectionAndChat();
    }


    private void getConnectionAndChat(){
        ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer(ParseUser.getCurrentUser().getObjectId(), new ConnectionHandle() {
            @Override
            public void onSuccess(String TAG, User user) {
                //find correct chat from group channel string
                ChatApp.getChat(groupChannelStr, new CreateChatHandle() {
                    @Override
                    public void onSuccess(String TAG, GroupChannel groupChannel) {
                        OpenChatActivity.this.groupChannel = groupChannel;
                        Toast.makeText(OpenChatActivity.this, "group channel found successfully", Toast.LENGTH_LONG).show();
                        populateChat(groupChannel);
                        sendBtn.setClickable(true); // now you are able to click and send messages.
                        setChannelHandler(groupChannelStr);
                    }

                    @Override
                    public void onFailure(String TAG, Exception e) {
                        Log.e("OPEN_CHAT_ACTIVITY", "accessing channel failed");
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(String TAG, Exception e) {
                e.printStackTrace();
                //TODO offline view ?
            }
        });
    }


    private void setChannelHandler(String groupChannelStr){
        SendBird.addChannelHandler(groupChannelStr, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                messages.add(baseMessage);
                chatAdapter.notifyItemInserted(messages.size()-1);
                rv_chatBubbles.scrollToPosition(messages.size() -1 );
            }
        });
    }


    private void assignViewsAndListener() {
        chatRecipientUser = (ParseUser) getIntent().getParcelableExtra("clicked_helper");
        chatMessage = findViewById(R.id.etChatBox_openChat);
        sendBtn = findViewById(R.id.btnChatboxSend_openChat);
        sendBtn.setClickable(false); // protects clicks before enable connection
        rv_chatBubbles = findViewById(R.id.rv_open_chat);
        sendBtn.setOnClickListener(sendBtnListener);
        senderName = findViewById(R.id.tv_sender_name);
        senderName.setText(chatRecipientUser.getUsername());
        profilePic = findViewById(R.id.iv_profile_pic_mini);
        appLogo = findViewById(R.id.logo_app);
        try {
            ParseFile avatarPic = chatRecipientUser.getParseFile("avatar");
            Glide.with(this)
                    .load(avatarPic.getFile())
                    .bitmapTransform(new RoundedCornersTransformation(this, 50, 0))
                    .into(profilePic);
        }catch (ParseException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        back = findViewById(R.id.iv_back_btn);
        back.setOnClickListener(backListener);

        record = findViewById(R.id.ib_record_audio);
        record.setOnTouchListener(recordListener);
    }

    protected void setRecyclerView() {
        Log.d("setRecyclerView", "attaching adapter");
        chatAdapter = new ChatsFragmentAdapter(this, messages);
        chatAdapter.setAddressee(chatRecipientUser);
        rv_chatBubbles.setAdapter(chatAdapter);
        rv_chatBubbles.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void populateChat(GroupChannel groupChannel) {
        PreviousMessageListQuery prevMessageListQuery = groupChannel.createPreviousMessageListQuery();
        prevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult( List<BaseMessage> ms, SendBirdException e) {
                if (e != null) {    // Error.
                    e.printStackTrace();
                    return;
                }
                for (BaseMessage message : ms) {
                    messages.add(0,message);
                    chatAdapter.notifyItemInserted(0);
                    rv_chatBubbles.scrollToPosition(messages.size() -1 );
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        SendBird.removeChannelHandler(groupChannelStr);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(emergency){
            //remove chat channel and log out anonymous user
            ParseUser.logOut();
            onDestroy();
            Intent intent = new Intent(OpenChatActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        super.onBackPressed();
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    public void startRecord(){
        Date d = new Date();
        String audioId = ParseUser.getCurrentUser().getUsername() + d.getTime();
        OpenChatActivityPermissionsDispatcher.startRecordWithPermissionCheck(this);
// Verify that the device has a mic first
        PackageManager packageManager = this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            // Set the file location for the audio
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/"+audioId+".3gp";
            // Create the recorder
            mediaRecorder = new MediaRecorder();
            // Set the audio format and encoder
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // Setup the output location
            mediaRecorder.setOutputFile(mFileName);
            // Start the recording
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                Log.e(Constants.AUDIO_RECORD_FAIL_TAG, "prepare() failed");
            }
        } else { // no mic on device
            Toast.makeText(this, "This device doesn't have a mic!", Toast.LENGTH_LONG).show();

        }
    }

    public void stopRecord(MediaRecorder mediaRecorder){
        // Stop the recording of the audio
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        OpenChatActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
