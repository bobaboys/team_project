package com.example.mentalhealthapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.mentalhealthapp.Fragments.ChatOverviewListFragment;
import com.example.mentalhealthapp.Fragments.HelperProfileFragment;
import com.example.mentalhealthapp.Fragments.ReceiverProfileFragment;
import com.example.mentalhealthapp.Fragments.RecieverSearchPageFragment;
import com.example.mentalhealthapp.Fragments.ReflectFragment;
import com.example.mentalhealthapp.R;
import com.parse.ParseUser;
import com.sendbird.android.User;

import chatApp.ChatApp;
import chatApp.ConnectionHandle;



public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public final String HELPER_FIELD = "helper";

    public ViewPager getmPager() {
        return mPager;
    }

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    private static int NUM_PAGES;
    private  BottomNavigationView bottomNavigationView;
    private  BottomNavigationView bottomHelperNavView;


    public  boolean HelperYes;

    public Fragment[] getFragments() {
        return fragments;
    }

    public void setFragments(Fragment[] fragments) {
        this.fragments = fragments;
    }

    private Fragment[] fragments;

    public void setCurrentFragment(Fragment f){
        int page = mPager.getCurrentItem();
        for(int i=0;i<fragments.length;i++) {
            fragments[i] = i==page ? f : null;
        }
        mPager.setCurrentItem(page);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HelperYes = ParseUser.getCurrentUser().getBoolean(HELPER_FIELD);
        NUM_PAGES = HelperYes ? 3:4;
        fragments = new  Fragment[NUM_PAGES];
        final boolean helper = ParseUser.getCurrentUser().getBoolean(HELPER_FIELD);
        bottomHelperNavView = findViewById(R.id.nav_helper_view);
        bottomNavigationView = findViewById(R.id.nav_view);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);

        if(helper){
            bottomNavigationView.setVisibility(View.GONE);
        }else{
            bottomHelperNavView.setVisibility(View.GONE);
        }

        ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer(ParseUser.getCurrentUser().getObjectId(), new ConnectionHandle() {
            @Override
            public void onSuccess(String TAG, User user) {
                ChatApp chatApp = ChatApp.getInstance();
                chatApp.setSendBirdUser(user);

                //set default
                if(helper){
                    setFragmentHelper();
                    bottomHelperNavView.setSelectedItemId(R.id.navigation_helper_home);
                }else{
                    setFragmentReceiver();
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                }
            }

            @Override
            public void onFailure(String TAG, Exception e) {
                e.printStackTrace();
                //TODO offline view ?
            }
        });
    }

    public void setFragmentHelper() {
        bottomHelperNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int page;
                switch (item.getItemId()) {
                    case R.id.navigation_helper_home:
                        page = 0;
                        break;
                    case R.id.navigation_helper_reflect:
                        page = 1;
                        break;
                    case R.id.navigation_helper_profile:
                        page = 2;
                        break;
                    default:
                        page = 0;
                        break;
                }
                mPager.setCurrentItem(page);
                return true;
            }
        });
    }


    public void setFragmentReceiver(){
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int page;
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            page=0;
                            break;
                        case R.id.navigation_reflect:
                            page=1;
                            break;
                        case R.id.navigation_chat:
                            page=2;
                            break;
                        case R.id.navigation_profile:
                            page=3;
                            break;
                        default:
                            page=0;
                            break;
                    }

                    mPager.setCurrentItem(page);

                    return true;
                }
            });
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(HelperYes) {
                    switch (position){
                        case 0:
                            fragments[position]= new ChatOverviewListFragment();
                            break;
                        case 1:
                            fragments[position]= new ReflectFragment();
                            break;
                        case 2:
                            fragments[position]= new HelperProfileFragment();
                            break;

                        default:
                            fragments[position]= new ChatOverviewListFragment();
                            break;
                    }
                if(fragments[position]==null){
                    switch (position) {
                        case 0:
                            fragments[position] = new RecieverSearchPageFragment();
                            break;
                        case 1:
                            fragments[position] = new ReflectFragment();
                            break;
                        case 2:
                            fragments[position] = new ChatOverviewListFragment();
                            break;
                        case 3:
                            fragments[position] = new ReceiverProfileFragment();
                            break;

                        default:
                            fragments[position] = new RecieverSearchPageFragment();
                            break;

                    }
                }

            }
            return fragments[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}

