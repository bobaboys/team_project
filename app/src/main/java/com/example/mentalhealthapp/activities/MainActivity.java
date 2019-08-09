package com.example.mentalhealthapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import java.util.ArrayList;
import chatApp.ChatApp;
import chatApp.ConnectionHandle;



public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public final String HELPER_FIELD = "helper";
    private static float  percentageOfSwipingThreshold = 0.85f; // range [0, 1)
    private int lastPage;
    private boolean calledNextPage;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;
    private boolean chatReceiver;
    private static int NUM_PAGES;
    private  BottomNavigationView bottomNavigationView;
    private  BottomNavigationView bottomHelperNavView;
    private boolean isHelper;
    private ArrayList<Integer> bottomBarHelper ;
    private ArrayList<Integer> bottomBarReceiver;
    private int targetPage;
    private Fragment[] fragments;


    public Fragment[] getFragments() { return fragments; }

    public void setFragments(Fragment[] fragments) { this.fragments = fragments; }

    public boolean isHelper() { return isHelper; }


    ViewPager.OnPageChangeListener swipeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int pixels) {
            // Starting pos same as target? We reach the target or there is not target at all.
            if(targetPage == i && v==0)  { targetPage =-1; }
            // next page not called and we are swiping? call next page.
            if(!calledNextPage && Math.abs(i+v-lastPage)> percentageOfSwipingThreshold){ callNextPage(i+v); }
            // set initial reference for later comparisons.
            if(v==0){
                lastPage = i;
                calledNextPage = false;
            }
        }

        @Override
        public void onPageSelected(int i) {}

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    ConnectionHandle connectionHandle = new ConnectionHandle() {
        @Override
        public void onSuccess(String TAG, User user) {
            ChatApp chatApp = ChatApp.getInstance();
            chatApp.setSendBirdUser(user);
            //set default

            setDefaultViewElements(isHelper);
            if(chatReceiver)bottomNavigationView.setSelectedItemId(bottomBarReceiver.get(2));
        }

        @Override
        public void onFailure(String TAG, Exception e) {
            e.printStackTrace();
        }
    };

    BottomNavigationView.OnNavigationItemSelectedListener helperNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            targetPage= bottomBarHelper.indexOf(item.getItemId());
            mPager.setCurrentItem(targetPage);
            return true;
        }
    };


    BottomNavigationView.OnNavigationItemSelectedListener receiverNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            targetPage = bottomBarReceiver.indexOf(item.getItemId());
            mPager.setCurrentItem(targetPage);
            return true;
        }
    };


    public void setCurrentFragment(Fragment f){
        int page = mPager.getCurrentItem();
        for(int i=0;i<fragments.length;i++) {
            fragments[i] = i==page ? f : null;
        }
        pagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(page);

    }


    private void generateNavBarsArrays(){
        bottomBarHelper = new ArrayList<>() ;
        bottomBarReceiver = new ArrayList<>();
        bottomBarHelper.add(R.id.navigation_helper_home);
        bottomBarHelper.add(R.id.navigation_helper_reflect);
        bottomBarHelper.add(R.id.navigation_helper_profile);

        bottomBarReceiver.add(R.id.navigation_home);
        bottomBarReceiver.add(R.id.navigation_reflect);
        bottomBarReceiver.add(R.id.navigation_chat);
        bottomBarReceiver.add(R.id.navigation_profile);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomHelperNavView = findViewById(R.id.nav_helper_view);
        bottomNavigationView = findViewById(R.id.nav_view);
        isHelper = ParseUser.getCurrentUser().getBoolean(HELPER_FIELD);
        initSwipeParams();
        generateNavBarsArrays();
        chatReceiver = getIntent().getStringExtra("chatFromReceiver") != null && !isHelper;
        startSendBirdSession();
    }

    private void startSendBirdSession(){
        ChatApp chatApp = ChatApp.getInstance();
        chatApp.startChatApp(this);
        chatApp.connectToServer(ParseUser.getCurrentUser().getObjectId(), connectionHandle);
    }

    private void initSwipeParams(){
        lastPage = 0;
        targetPage = 0;
        calledNextPage = false;
        NUM_PAGES = isHelper ? 3:4;
        fragments = new  Fragment[NUM_PAGES];
    }


    private void setDefaultViewElements(boolean helper){
        if(helper){
            bottomHelperNavView.setOnNavigationItemSelectedListener(helperNavListener);
            setSwipeAdapter();
            bottomHelperNavView.setSelectedItemId(R.id.navigation_helper_home);
            bottomNavigationView.setVisibility(View.GONE);
        }else{
            bottomNavigationView.setOnNavigationItemSelectedListener(receiverNavListener);
            setSwipeAdapter();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            bottomHelperNavView.setVisibility(View.GONE);
        }
    }


    private void setSwipeAdapter(){
        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.setOnPageChangeListener(swipeListener);
    }


    private void callNextPage(float currentPos){
        int nextPage = lastPage + ( ((currentPos-lastPage)>0) ? 1:-1);
        if(isHelper){
            bottomHelperNavView.setSelectedItemId(bottomBarHelper.get(
                    (targetPage == -1) ? nextPage : targetPage));
        }else{
            bottomNavigationView.setSelectedItemId( bottomBarReceiver.get(
                    (targetPage == -1) ? nextPage : targetPage));
        }
        calledNextPage = true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        targetPage = 0;
        calledNextPage = false;
        mPager.setCurrentItem(0);
        if(isHelper)
            bottomHelperNavView.setSelectedItemId(bottomBarHelper.get(0));
        else
            bottomNavigationView.setSelectedItemId(bottomBarReceiver.get(0));

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            if(fragments[position]!=null)return fragments[position];
            if(isHelper) {
                    switch (position) {
                        case 0:
                            fragments[position] = new ChatOverviewListFragment();
                            break;
                        case 1:
                            fragments[position] = new ReflectFragment();
                            break;
                        case 2:
                            fragments[position] = new HelperProfileFragment();
                            break;

                        default:
                            fragments[position] = new ChatOverviewListFragment();
                            break;
                    }
                }else{
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
            return fragments[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}

