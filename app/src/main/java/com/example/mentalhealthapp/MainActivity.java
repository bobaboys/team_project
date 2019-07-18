package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;


import com.example.mentalhealthapp.Fragments.HelperReflectFragment;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import chatApp.ChatApp;
import chatApp.MasterHandle;

import android.view.MenuItem;
import android.widget.TextView;

import com.example.mentalhealthapp.Fragments.HelperChatsFragment;
import com.example.mentalhealthapp.Fragments.HelperProfileFragment;
import com.example.mentalhealthapp.Fragments.HelperReflectFragment;
import com.example.mentalhealthapp.Fragments.RecieverChatsFragment;
import com.example.mentalhealthapp.Fragments.RecieverProfileFragment;
import com.example.mentalhealthapp.Fragments.RecieverReflectFragment;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextMessage;

    public BottomNavigationView bottomNavigationView;
    public boolean helper;
    public TextView currPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO is this correct?


        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.nav_view);
        currPage = findViewById(R.id.currPageName_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                if(helper){
                    switch (item.getItemId()) {
                        case R.id.navigation_reflect:
                            fragment = new HelperReflectFragment();
                            currPage.setText("Reflect");
                            break;
                        case R.id.navigation_chat:
                            fragment = new HelperChatsFragment();
                            currPage.setText("Chat");
                            break;
                        case R.id.navigation_profile:
                            fragment = new HelperProfileFragment();
                            currPage.setText("Profile");
                            break;
                        default:
                            fragment = new HelperChatsFragment();
                            currPage.setText("Chat");
                            break;
                    }
                }
                else{
                    switch (item.getItemId()) {
                        case R.id.navigation_reflect:
                            fragment = new RecieverReflectFragment();
                            currPage.setText("Reflect");
                            break;
                        case R.id.navigation_chat:
                            fragment = new RecieverChatsFragment();
                            currPage.setText("Chat");
                            break;
                        case R.id.navigation_profile:
                            fragment = new RecieverProfileFragment();
                            currPage.setText("Profile");
                            break;
                        default:
                            fragment = new RecieverChatsFragment();
                            currPage.setText("Chat");
                            break;
                    }
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer_main, fragment).commit();
                return true;
            }
        });
        //set default
        bottomNavigationView.setSelectedItemId(R.id.navigation_chat);


        /*Dummy chat starts here*/
        final ChatApp chatApp = new ChatApp();
        chatApp.startChatApp(this);
        ChatApp.connectToServer( "newUser2", new  MasterHandle(){
            public void onSuccess(String TAG, User user){
                Log.d(TAG, "Connection successful with user: " + user);
            }
            public void onFailure(String TAG, Exception e){
                e.printStackTrace();
            }
            public void onSuccess(String TAG){}
            public void onSuccess(String TAG, GroupChannel groupChannel){}
            public void onSuccess(String TAG, String channelUrl){}
            public void onSuccess(String TAG, List<User> list){}
        });
        final GroupChannel[] chat = new GroupChannel[1];// We need an space to store the chat. Async TODO explain this good.
        ChatApp.createChat( "newUser2",  "newUser",false, new MasterHandle(){
            public void onSuccess(String TAG, User user){ }
            public void onFailure(String TAG, Exception e){
                e.printStackTrace();
            }
            public void onSuccess(String TAG){}
            public void onSuccess(String TAG, GroupChannel groupChannel){
                chat[0] = groupChannel;
                Log.d(TAG, "New conversation : ");
                //chatApp.sendMessageText( groupChannel,   "Hola", final MasterHandle handle);
            }
            public void onSuccess(String TAG, String channelUrl){}
            public void onSuccess(String TAG, List<User> list){}
        });
        if(chat[0]!=null){
            chatApp.sendMessageText(chat[0], "Hola", new MasterHandle() {
                @Override
                public void onSuccess(String TAG) {

                }

                @Override
                public void onSuccess(String TAG, User user) {

                }

                @Override
                public void onSuccess(String TAG, GroupChannel groupChannel) {

                }

                @Override
                public void onSuccess(String TAG, String message) {
                    Log.d(TAG,"Message sent:"+ message);
                }

                @Override
                public void onSuccess(String TAG, List<User> list) {

                }

                @Override
                public void onFailure(String TAG, Exception e) {

                }
            });
        }

        /*Dummy chat ends here*/
    }
}
