package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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

    public BottomNavigationView bottomNavigationView;
    public TextView currPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        final boolean helper = currentUser.getBoolean("helper");

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
                            currPage.setText("Home");
                            break;
                        case R.id.navigation_reflect:
                            fragment = new HelperReflectFragment();
                            currPage.setText("Reflect");
                            break;
                        case R.id.navigation_chat:
                            fragment = new HelperChatsFragment();
                            currPage.setText("Chats");
                            break;
                        case R.id.navigation_profile:
                            fragment = new HelperProfileFragment();
                            currPage.setText("Profile");
                            break;
                        default:
                            fragment = new HelperChatsFragment();
                            currPage.setText("Home");
                            break;
                    }
                }
                else{
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            fragment = new RecieverSearchPageFragment();
                            currPage.setText("Home");
                            break;
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
                            fragment = new RecieverSearchPageFragment();
                            currPage.setText("Home");
                            break;
                    }
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer_main, fragment).commit();
                return true;
            }
        });
        //set default
        bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
    }

}
