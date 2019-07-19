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
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
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
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;
import chatApp.GetStringHandle;

import android.view.MenuItem;
import android.widget.TextView;

import com.example.mentalhealthapp.Fragments.HelperChatsFragment;
import com.example.mentalhealthapp.Fragments.HelperProfileFragment;
import com.example.mentalhealthapp.Fragments.HelperReflectFragment;
import com.example.mentalhealthapp.Fragments.HelperSearchPageFragment;
import com.example.mentalhealthapp.Fragments.RecieverChatsFragment;
import com.example.mentalhealthapp.Fragments.RecieverProfileFragment;
import com.example.mentalhealthapp.Fragments.RecieverReflectFragment;
import com.example.mentalhealthapp.Fragments.RecieverSearchPageFragment;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextMessage;

    public final String HELPER_FIELD = "helper";

    public BottomNavigationView bottomNavigationView;
    public TextView currPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO is this correct?


        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        final boolean helper = currentUser.getBoolean(HELPER_FIELD);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.nav_view);
        currPage = findViewById(R.id.currPageName_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                if(helper){
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            fragment = new HelperSearchPageFragment();
                            currPage.setText(R.string.home);
                            break;
                        case R.id.navigation_reflect:
                            fragment = new HelperReflectFragment();
                            currPage.setText(R.string.reflect);
                            break;
                        case R.id.navigation_chat:
                            fragment = new HelperChatsFragment();
                            currPage.setText(R.string.chats);
                            break;
                        case R.id.navigation_profile:
                            fragment = new HelperProfileFragment();
                            currPage.setText(R.string.profile);
                            break;
                        default:
                            fragment = new HelperChatsFragment();
                            currPage.setText(R.string.home);
                            break;
                    }
                }
                else{
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            fragment = new RecieverSearchPageFragment();
                            currPage.setText(R.string.home);
                            break;
                        case R.id.navigation_reflect:
                            fragment = new RecieverReflectFragment();
                            currPage.setText(R.string.reflect);
                            break;
                        case R.id.navigation_chat:
                            fragment = new RecieverChatsFragment();
                            currPage.setText(R.string.chats);
                            break;
                        case R.id.navigation_profile:
                            fragment = new RecieverProfileFragment();
                            currPage.setText(R.string.profile);
                            break;
                        default:
                            fragment = new RecieverSearchPageFragment();
                            currPage.setText(R.string.home);
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
        final ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer( "newUser3", new  ConnectionHandle(){
            @Override
            public void onSuccess(String TAG, User user){
                Log.d(TAG, "Connection successful with user: " + user);
                createChatAndFirstMessage(chatApp);
            }
            @Override
            public void onFailure(String TAG, Exception e){
                e.printStackTrace();
            }
        });
        /*Dummy chat ends here*/

    }

    public void createChatAndFirstMessage(final ChatApp chatApp){
        chatApp.createChat( "newUser3",  "newUser2",false, new CreateChatHandle(){
            @Override
            public void onFailure(String TAG, Exception e){
                e.printStackTrace();
            }
            @Override
            public void onSuccess(String TAG, GroupChannel groupChannel){
                Log.d(TAG, "New conversation : ");
                message( chatApp, groupChannel);
                //chatApp.sendMessageText( groupChannel,   "Hola", final MasterHandle handle);
            }
        });

    }
    public void message(ChatApp chatApp,final GroupChannel chat){
        if(chat!=null){
            chatApp.sendMessageText(chat, "Hola amigos mios", new GetStringHandle() {
                @Override
                public void onSuccess(String TAG, String message) {
                    Log.d(TAG,"Message sent:"+ message);
                    SendBird.addChannelHandler(chat.getUrl(), new SendBird.ChannelHandler(){
                        @Override
                        public void onMessageReceived(BaseChannel var1, BaseMessage var2){
                            
                        }
                    });
                }

                @Override
                public void onFailure(String TAG, Exception e) {

                }
            });
        }
    }

}
