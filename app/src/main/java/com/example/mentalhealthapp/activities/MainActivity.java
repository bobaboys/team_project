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
import android.widget.TextView;

import com.example.mentalhealthapp.Fragments.ChatOverviewListFragment;
import com.example.mentalhealthapp.Fragments.HelperProfileFragment;
import com.example.mentalhealthapp.Fragments.RecieverProfileFragment;
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
    public BottomNavigationView bottomNavigationView;
    public BottomNavigationView bottomHelperNavView;

    public Fragment currentCentralFragment;
    private  int numPages;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    final ParseUser currentUser = ParseUser.getCurrentUser();
    boolean helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView mTextMessage;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = findViewById(R.id.message);
        helper = currentUser.getBoolean(HELPER_FIELD);

        numPages=helper?3:4;
        bottomHelperNavView = findViewById(R.id.nav_helper_view);
        bottomNavigationView = findViewById(R.id.nav_view);
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
        chatApp.connectToServer(currentUser.getObjectId(), new ConnectionHandle() {
            @Override
            public void onSuccess(String TAG, User user) {
                ChatApp chatApp = ChatApp.getInstance();
                chatApp.setSendBirdUser(user);
                setFragment(helper);
                //set default
                bottomHelperNavView.setSelectedItemId(helper?
                            R.id.navigation_helper_home
                            : R.id.navigation_home);
            }

            @Override
            public void onFailure(String TAG, Exception e) {
                e.printStackTrace();
                //TODO offline view ?
            }
        });
    }

    public void setFragment(final boolean helper){
        if(helper){
            bottomHelperNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.navigation_helper_home:
                            fragment = new ChatOverviewListFragment();
                            break;
                        case R.id.navigation_helper_reflect:
                            fragment = new ReflectFragment();
                            break;
                        case R.id.navigation_helper_profile:
                            fragment = new HelperProfileFragment();
                            break;
                        default:
                            fragment = new ChatOverviewListFragment();
                            break;
                    }
                    FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    replaceFragment(fragment);
                    fragmentTransaction.commit();
                    return true;
                }
            });
        }else{
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            fragment = new RecieverSearchPageFragment();
                            break;
                        case R.id.navigation_reflect:
                            fragment = new ReflectFragment();
                            break;
                        case R.id.navigation_chat:
                            fragment = new ChatOverviewListFragment();
                            break;
                        case R.id.navigation_profile:
                            fragment = new RecieverProfileFragment();
                            break;
                        default:
                            fragment = new RecieverSearchPageFragment();
                            break;
                    }

                    FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    replaceFragment(fragment);
                    fragmentTransaction.commit();
                    return true;
                }
            });
        }
    }


    public void replaceFragment(Fragment f){
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        if(currentCentralFragment == null || ! currentCentralFragment.getClass().equals(f.getClass())){
            // fragments are from different classes,
            // different fragments, must change fragment
            currentCentralFragment = f;
            ft.replace(R.id.pager, f);
            // or ft.add(R.id.your_placeholder, new FooFragment());
            // Complete the changes added above
            ft.commit();
        }
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    if(helper)
                        return new ChatOverviewListFragment();
                    else
                        return new RecieverSearchPageFragment();
                case 1:
                    return new ReflectFragment();
                case 2:
                    if(helper)
                        return new HelperProfileFragment();
                    else
                        return new ChatOverviewListFragment();
                case 3:
                        return new RecieverProfileFragment();
                default:
                    if(helper)
                        return new ChatOverviewListFragment();
                    else
                        return new RecieverSearchPageFragment();
            }

        }


        @Override
        public int getCount() {
            return numPages;
        }
    }
}

