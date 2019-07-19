package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mentalhealthapp.Fragments.HelperChatsFragment;
import com.example.mentalhealthapp.Fragments.HelperProfileFragment;
import com.example.mentalhealthapp.Fragments.HelperReflectFragment;
import com.example.mentalhealthapp.Fragments.HelperSearchPageFragment;
import com.example.mentalhealthapp.Fragments.RecieverChatsFragment;
import com.example.mentalhealthapp.Fragments.RecieverProfileFragment;
import com.example.mentalhealthapp.Fragments.RecieverReflectFragment;
import com.example.mentalhealthapp.Fragments.RecieverSearchPageFragment;
import com.parse.ParseUser;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;

import chatApp.ChatApp;
import chatApp.ConnectionHandle;
import chatApp.CreateChatHandle;
import chatApp.GetStringHandle;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public final String HELPER_FIELD = "helper";
    public BottomNavigationView bottomNavigationView;
    public TextView currPage;

    Fragment currentCentralFragment;
    final ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView mTextMessage;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = findViewById(R.id.message);

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
                replaceFragment(fragment);
                return true;
            }
        });
        //set default
        bottomNavigationView.setSelectedItemId(R.id.navigation_chat);

        connectUserToChat();
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
                Toast.makeText(MainActivity.this, "Chat connection successful!", Toast.LENGTH_LONG).show();
                createChatAndFirstMessage(chatApp);
            }
            @Override
            public void onFailure(String TAG, Exception e){
                Log.e(TAG,"Chat connection failed");
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Chat failed!", Toast.LENGTH_LONG).show();
            }
        });
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
                message(chatApp, groupChannel);
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
                    Log.e(TAG,"Message sent failure:");
                }
            });
        }
    }
    public void replaceFragment(Fragment f){
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        if(currentCentralFragment == null || !currentCentralFragment.getClass().equals(f.getClass())){
            // fragments are from different classes,
            // different fragments, must change fragment
            currentCentralFragment = f;
            ft.replace(R.id.flContainer_main, f);
            // or ft.add(R.id.your_placeholder, new FooFragment());
            // Complete the changes added above
            ft.commit();
        }
    }
}
