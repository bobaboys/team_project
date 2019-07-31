package com.example.mentalhealthapp.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Utils.Utils;
import chatApp.ChatApp;
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import pub.devrel.easypermissions.EasyPermissions;


public class OpenChatActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

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
    ImageView profilePic, appLogo, back;
    ImageButton record;
    MediaRecorder mediaRecorder;
    String mFileName;
    boolean mStartRecording;
    String audioId;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        //Send text msg btn Listener
        @Override
        public void onClick(View v) {

            final String chatMessageString = chatMessage.getText().toString();
            //find correct chat from group channel string
            groupChannel.sendUserMessage(chatMessageString, new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(OpenChatActivity.this, "Message not sent", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        return;
                    }
                    //You add this new message to the lowest part of the chat
                    messages.add(userMessage);
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    rv_chatBubbles.scrollToPosition(messages.size() - 1);
                }
            });
        }
    };


    View.OnClickListener backListener = new View.OnClickListener() {
        // Back btn (top of the chat) listener
        @Override
        public void onClick(View v) {
            OpenChatActivity.super.onBackPressed();
        }
    };


    View.OnClickListener recordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRecord(mStartRecording);
            mStartRecording = !mStartRecording;
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void startRecording() {
        requestPermission();
        if(!permissionToRecordAccepted) return;
        Date d = new Date();
        audioId = ParseUser.getCurrentUser().getUsername().toLowerCase() + d.getTime();

        // Set the file location for the audio
        mFileName =  Environment.getExternalStorageDirectory() + File.separator ;
        mFileName += audioId + ".3gp";
        startRecording2();
    }

    public void startRecording2() {
        // Create the recorder
        mediaRecorder = new MediaRecorder();
        //mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //mediaRecorder.setMaxDuration(60000);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            //Log.e(Constants.AUDIO_RECORD_FAIL_TAG, "prepare() failed");
            e.printStackTrace();
        }
        mediaRecorder.start();

    }


    public void stopRecording() {
        //stop recording
        if (mediaRecorder == null) return;
        stopRecording(mediaRecorder);
    }


    public void stopRecording(MediaRecorder mediaRecorder) {
        // Stop the recording of the audio
        mediaRecorder.stop();
        mediaRecorder.release();
        sendFile();
        mediaRecorder = null;
    }



    private void sendFile() {
        FileMessageParams fmp = new FileMessageParams();
        File file = new File(mFileName);
        fmp.setFileName(audioId).setFile(file);
        groupChannel.sendFileMessage(fmp, new BaseChannel.SendFileMessageHandler() {
            @Override
            public void onSent(FileMessage fileMessage, SendBirdException e) {
                //TODO
                //You add this new message to the lowest part of the chat
                messages.add(fileMessage);
                chatAdapter.notifyItemInserted(messages.size() - 1);
                rv_chatBubbles.scrollToPosition(messages.size() - 1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_chat);
        emergency = false;
        mStartRecording = true;
        if (getIntent().getStringExtra("emergency") != null) {
            //coming from emergency button
            emergency = true;
        }

        groupChannelStr = getIntent().getStringExtra("group_channel");
        messages = new ArrayList<>();
        mediaRecorder = null;

        assignViewsAndListener();
        setRecyclerView();
        getConnectionAndChat();
    }


    private void getConnectionAndChat() {
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


    private void setChannelHandler(String groupChannelStr) {
        SendBird.addChannelHandler(groupChannelStr, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                messages.add(baseMessage);
                chatAdapter.notifyItemInserted(messages.size() - 1);
                rv_chatBubbles.scrollToPosition(messages.size() - 1);
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
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        back = findViewById(R.id.iv_back_btn);
        back.setOnClickListener(backListener);

        record = findViewById(R.id.ib_record_audio);
        record.setOnClickListener(recordListener);
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
            public void onResult(List<BaseMessage> ms, SendBirdException e) {
                if (e != null) {    // Error.
                    e.printStackTrace();
                    return;
                }
                for (BaseMessage message : ms) {
                    messages.add(0, message);
                    chatAdapter.notifyItemInserted(0);
                    rv_chatBubbles.scrollToPosition(messages.size() - 1);
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
        if (emergency) {
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(OpenChatActivity.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }
}


