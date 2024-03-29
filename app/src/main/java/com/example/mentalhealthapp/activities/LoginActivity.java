package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Chat;
import com.example.mentalhealthapp.models.Constants;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.User;

import java.util.List;

import chatApp.ChatApp;
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;


public class LoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button signUpBtn;
    private Button emergencyBtn;
    private GroupChannel groupChannel;
    protected ParseUser currentUser;
    protected ParseUser onCallNeedHelp;
    private ParseUser onCallHelper;
    protected MediaPlayer buttonClickSound;

    private final View.OnClickListener loginBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bounce);
            loginBtn.startAnimation(animation);
            final String username = usernameInput.getText().toString();
            final String password = passwordInput.getText().toString();
            ParseUser.logInInBackground(username, password, logInCallback);
        }
    };

    LogInCallback logInCallback = new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
            if(e==null){
                Log.d("LoginActivity", "Login successful!");
                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                Log.e("LoginActivity", "Login failure");
                Toast.makeText(LoginActivity.this,"Invalid username and/or password", Toast.LENGTH_LONG).show();
                usernameInput.setText("");
                passwordInput.setText("");
                e.printStackTrace();
            }
        }
    };

    private final View.OnClickListener signupBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bounce);
            signUpBtn.startAnimation(animation);
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    };

    private final View.OnClickListener emergencyBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bounce);
            emergencyBtn.startAnimation(animation);
            //assigns need help, on call, and starts chat
            ParseAnonymousUtils.logIn(assignOnCallNeedHelp);
        }
    };

    private FindCallback assignOnCallHelper= new FindCallback<ParseUser>() {
        @Override
        public void done(List<ParseUser> objects, ParseException e) {
            for(int i = 0; i < objects.size(); i++){
                onCallHelper = objects.get(0);
            }
        }
    };

    private SaveCallback openChat =  new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if(e==null){
                Log.d("USERS", onCallHelper.getUsername());
                openChatFragment(groupChannel);
            }else{
                e.printStackTrace();
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonClickSound = MediaPlayer.create(this, R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent (LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        AssignViewsAndListeners();
    }

    private void startAnonymousChat() {
        ChatApp.startChatApp(LoginActivity.this);
        ChatApp.connectToServer(onCallNeedHelp.getObjectId(), new ConnectionHandle() {
            @Override
            public void onSuccess(String TAG, User user) {
                //create chat between need help and on call
                ChatApp.createChat(onCallNeedHelp, onCallHelper, false, new CreateChatHandle() {
                    @Override
                    public void onSuccess(String TAG, final GroupChannel groupChannel) {
                        // Check if row already exist
                        LoginActivity.this.groupChannel = groupChannel;
                        ParseQuery<Chat> query  = new ParseQuery<Chat>(Chat.class);
                        query.whereEqualTo("chatUrl",groupChannel.getUrl());
                        query.findInBackground(new FindCallback<Chat>() {
                            @Override
                            public void done(List<Chat> objects, ParseException e) {
                                if(objects.size() == 0){// no rows, create new one.
                                    createChatParse(groupChannel, openChat);
                                }else {// there is a row with the same info. skip create a new one.
                                    openChatFragment(groupChannel);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String TAG, Exception e) {
                        Log.e("OPEN CHAT:", "chat open successful");
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "can't open chat", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(String TAG, Exception e) {
                Toast.makeText(LoginActivity.this, "can't connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    LogInCallback assignOnCallNeedHelp =new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e!=null){
                    Log.e("LoginActivity", "Login failure");
                    Toast.makeText(LoginActivity.this,"emergency log in failed", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Log.d("LoginActivity", "emergency login successful!");
                onCallNeedHelp = user;
                user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            queryParseUserOnCall(assignOnCallHelper);
                        }
                    });
            }
        };

    private void queryParseUserOnCall(FindCallback findCallback) {
        //assign on call helper
        ParseQuery<ParseUser> query2 = ParseQuery.getQuery("_User");
        query2.include(Constants.USERNAME_FIELD);
        query2.whereEqualTo(Constants.USERNAME_FIELD, "ON_CALL");
        query2.findInBackground(findCallback);
        startAnonymousChat();
    }


    private void AssignViewsAndListeners() {
        usernameInput = findViewById(R.id.etUsername_login);
        passwordInput = findViewById(R.id.etPassword_login);
        loginBtn = findViewById(R.id.btnLogin_login);
        signUpBtn = findViewById(R.id.btnSignup_login);
        loginBtn.setOnClickListener(loginBtnListener);
        signUpBtn.setOnClickListener(signupBtnListener);
        emergencyBtn = findViewById(R.id.btnEmergency_login);
        emergencyBtn.setOnClickListener(emergencyBtnListener);
    }

    public void createChatParse( GroupChannel groupChannel,SaveCallback saveCallback){
        Chat chat =  new Chat();
        chat.put("helper", ParseObject.createWithoutData("_User", onCallHelper.getObjectId()) );
        chat.put("reciever", ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId()));
        chat.put("chatUrl", groupChannel.getUrl());
        chat.saveInBackground(saveCallback);

    }

    public void openChatFragment(GroupChannel groupChannel){
        Intent intent = new Intent(LoginActivity.this, OpenChatActivity.class);
        intent.putExtra("clicked_helper",onCallHelper);
        //pass current group channel url to next activity
        String groupChannelUrl = groupChannel.getUrl();
        intent.putExtra("group_channel", groupChannelUrl);
        intent.putExtra("emergency", "emergency");
        startActivity(intent);
        Log.d("OPEN CHAT:", "chat open successful");
    }
}
