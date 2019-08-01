package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.LoginActivity;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import Utils.Utils;

public class HelperProfileFragment extends Fragment {

    protected Button logOutbtn;
    protected TextView helperProfileBio;
    protected ImageView helperProfileAvatar;
    protected TextView helperProfileTags;
    protected TextView helperName;
    protected FloatingActionButton editHelperProfile;
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
        assignViewsAndListeners(view);
        populateTags();
    }

    private void assignViewsAndListeners(View view) {
        currentUser = ParseUser.getCurrentUser();
        helperProfileBio =view.findViewById(R.id.tvMyBio_helper_profile);
        helperProfileAvatar = view.findViewById(R.id.ivAvatar_helper_profile);
        ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(Constants.AVATAR_FIELD);
        Bitmap bm = Utils.convertFileToBitmap(avatarFile);
        helperProfileAvatar.setImageBitmap(bm);
        helperProfileTags = view.findViewById(R.id.tvMyTags_helper_profile);
        helperName = view.findViewById(R.id.tvHelperName_HelperProfileFragment);
        helperName.setText(currentUser.getString(Constants.NAME_FIELD));
        logOutbtn = view.findViewById(R.id.btnLogout_ProfileHelper);
        editHelperProfile = view.findViewById(R.id.fab_Edit_HelperProfile);
        helperProfileBio.setText(currentUser.getString(Constants.HELPER_BIO_FIELD));
        logOutbtn.setOnClickListener(logoutBtnListener);
        editHelperProfile.setOnClickListener(new View.OnClickListener() {
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
        });
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
        query.include(Constants.USER_FIELD);
        query.include(Constants.TAG_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                String strListOfTags = "";
                if(e==null){
                        for(int i = 0; i < objects.size(); i++){
                            HelperTags helperTag = objects.get(i);
                            //second to last tag
                            if(i == objects.size() - 2){
                                strListOfTags += ((Tag)helperTag.get("Tag")).get("Tag")+ ", and ";
                                continue;
                            }
                            //last tag
                            if(i == objects.size() - 1){
                                strListOfTags += ((Tag)helperTag.get("Tag")).get("Tag");
                                break;
                            }
                            strListOfTags += ((Tag)helperTag.get("Tag")).get("Tag")+ ", ";
                            //TODO POPULATE WITH CARDS INSTEAD OF STR ONLY
                        }


                    helperProfileTags.setText(strListOfTags);

                }else{
                    Log.e("HelperProfileFragment", "failure in populating tags");
                }
            }
        });
    }

}