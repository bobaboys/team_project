package com.example.mentalhealthapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.MessagesChatAdapter;
import com.example.mentalhealthapp.models.Chat;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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


public class OpenChatActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private EditText chatMessage;
    private Button sendBtn;
    private ParseUser chatRecipientUser;
    private RecyclerView rv_chatBubbles;
    private MessagesChatAdapter chatAdapter;
    private ArrayList<BaseMessage> messages;
    private GroupChannel groupChannel;
    private String groupChannelStr;
    private Boolean emergency;
    private TextView senderName;
    private  ImageView profilePic, appLogo, back;
    private ImageButton record;
    private MediaRecorder mediaRecorder;
    private String mFileName;
    private boolean mStartRecording;
    private String audioId;
    private boolean permissionToRecordAccepted = false;
    private  MediaPlayer buttonClickSound;

    View.OnClickListener sendBtnListener = new View.OnClickListener() {
        //Send text msg btn Listener
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final String chatMessageString = chatMessage.getText().toString();
            chatMessage.setText("");
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
            if (emergency) {
                //remove chat channel and log out anonymous user
                ParseUser.getCurrentUser().deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("Emergency User", "deleted");
                    }
                });
                ParseUser.logOut();
                OpenChatActivity.super.onBackPressed();
            }else{
                ParseQuery<Chat> q = new ParseQuery<Chat>(Chat.class);
                q.whereEqualTo("chatUrl",groupChannel.getUrl());
                q.findInBackground(new FindCallback<Chat>() {
                    @Override
                    public void done(List<Chat> objects, ParseException e) {
                        Chat currChat = objects.get(0);
                        boolean isCurrentHelper = ParseUser.getCurrentUser().getBoolean("helper");
                        currChat.put(isCurrentHelper?"lastCheckedHelper":"lastChecked", new Date().getTime());
                        currChat.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                OpenChatActivity.super.onBackPressed();
                            }
                        });

                    }
                });
            }

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
        record.setImageTintList(ColorStateList.valueOf(getResources().getColor(
                start ? R.color.colorAccent : R.color.gray_record )));
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
        buttonClickSound = MediaPlayer.create(this, R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
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
                    .bitmapTransform(new RoundedCornersTransformation(this, 120, 0))
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
        chatAdapter = new MessagesChatAdapter(this, messages);
        chatAdapter.setAddressee(chatRecipientUser);
        rv_chatBubbles.setAdapter(chatAdapter);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        rv_chatBubbles.setLayoutManager(layout);
    }

    protected void populateChat(GroupChannel groupChannel) {
        PreviousMessageListQuery prevMessageListQuery = groupChannel.createPreviousMessageListQuery();
        prevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> ms, SendBirdException e) {
                if (e != null) {
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
            ParseUser user = ParseUser.getCurrentUser();
            user.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("Emergency User", "deleted");
                }
            });
            ParseUser.logOut();
            Intent intent = new Intent(OpenChatActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
            return;
        }else{
            ParseQuery<Chat> q = new ParseQuery<Chat>(Chat.class);
            q.whereEqualTo("chatUrl",groupChannel.getUrl());
            q.findInBackground(new FindCallback<Chat>() {
                @Override
                public void done(List<Chat> objects, ParseException e) {
                    Chat currChat = objects.get(0);

                    currChat.put("lastChecked", new Date().getTime());
                    currChat.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            OpenChatActivity.super.onBackPressed();
                        }
                    });

                }
            });

        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(OpenChatActivity.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }
}


