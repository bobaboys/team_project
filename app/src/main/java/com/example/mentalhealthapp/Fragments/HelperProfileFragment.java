package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.HelperTags;
import com.example.mentalhealthapp.LoginActivity;
import com.example.mentalhealthapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HelperProfileFragment extends Fragment {

    protected TextView testProf;
    protected Button logOutbtn;
    protected TextView helperProfileBio;
    protected ImageView helperProfileAvatar;
    protected TextView helperProfileTags;
    protected TextView helperProfileChats;
    //protected FloatingActionButton editHelperProfile;
    ParseUser currentUser;

    protected View.OnClickListener logoutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testProf = view.findViewById(R.id.profileTest);
        helperProfileBio =view.findViewById(R.id.tvMyBio_helper_profile);
        helperProfileAvatar = view.findViewById(R.id.ivAvatar_helper_profile);
        helperProfileTags = view.findViewById(R.id.tvMyTags_helper_profile);
        helperProfileChats = view.findViewById(R.id.tvMyChats_helper_profile);
        logOutbtn = view.findViewById(R.id.btnLogout_ProfileHelper);
        //editHelperProfile = view.findViewById(R.id.f);

        currentUser = ParseUser.getCurrentUser();
        helperProfileBio.setText(currentUser.getString("helperBio"));
        logOutbtn.setOnClickListener(logoutBtnListener);
/*        editHelperProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTags();
                Fragment fragment = new HelperEditProfileFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });*/
        populateTags();
    }

    public void clearTags(){
        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include("user");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                if(e==null){
                    for(HelperTags object : objects){
                        object.deleteInBackground();
                    }
                }else{
                    Log.e("HelperProfileFragment", "failure in clearing tags");
                }
            }
        });
    }

    public void populateTags(){

        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include("user");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                String colors = "";
                if(e==null){
                    for(HelperTags tag : objects){
                        colors = colors + tag.getTag() + " ";
                    }
                    helperProfileTags.setText(colors);

                }else{
                    Log.e("HelperProfileFragment", "failure in populating tags");
                }
            }
        });
    }

}
