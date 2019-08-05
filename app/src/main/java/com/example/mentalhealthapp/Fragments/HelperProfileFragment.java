package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.LoginActivity;
import com.example.mentalhealthapp.adapters.SelectedTagsAdapter;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import Utils.Utils;

public class HelperProfileFragment extends Fragment {

    protected Button logOutbtn;
    protected TextView helperProfileBio;
    protected ImageView helperProfileAvatar;
    protected TextView helperName;
    protected FloatingActionButton editHelperProfile;
    protected MediaPlayer buttonClickSound;
    protected GridView tagsGridView;
    protected ArrayList<String> allHelperTags = new ArrayList<>();;
    protected SelectedTagsAdapter profTagsAdapter;


    protected View.OnClickListener logoutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    };


    protected  View.OnClickListener editListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Utils.switchToAnotherFragment(new HelperEditProfileFragment(),
                    getActivity().getSupportFragmentManager() ,
                    R.id.flContainer_main);
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
        buttonClickSound = MediaPlayer.create(getContext(), R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        assignViewsAndListeners(view);
        populateGridViewTags();
    }


    private void assignViewsAndListeners(View view) {
        helperProfileBio =view.findViewById(R.id.tvMyBio_helper_profile);
        helperProfileAvatar = view.findViewById(R.id.ivAvatar_helper_profile);
        tagsGridView = view.findViewById(R.id.gv_tags_helper_prof);
        helperName = view.findViewById(R.id.tv_username_helperProfile);
        logOutbtn = view.findViewById(R.id.btnLogout_ProfileHelper);
        editHelperProfile = view.findViewById(R.id.fab_Edit_HelperProfile);

        Bitmap bm = Utils.convertFileToBitmap(
                ParseUser.getCurrentUser().getParseFile(Constants.AVATAR_FIELD));
        helperProfileAvatar.setImageBitmap(bm);

        helperName.setText(ParseUser.getCurrentUser().getString(Constants.NAME_FIELD));
        helperProfileBio.setText(ParseUser.getCurrentUser().getString(Constants.HELPER_BIO_FIELD));
        helperProfileBio.setText(ParseUser.getCurrentUser().getString(Constants.HELPER_BIO_FIELD));

        logOutbtn.setOnClickListener(logoutBtnListener);
        editHelperProfile.setOnClickListener(editListener);
    }


    public void populateGridViewTags(){
        allHelperTags.clear();
        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include(Constants.USER_FIELD);
        query.include(Constants.TAG_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                if(e==null){
                    for(int i = 0; i < objects.size(); i++){
                        Object strTag;
                        HelperTags helperTag = objects.get(i);
                        strTag = ((Tag)helperTag.get("Tag")).get("Tag");
                        allHelperTags.add(strTag.toString());
                    }
                    profTagsAdapter = new SelectedTagsAdapter(getContext(),allHelperTags);
                    tagsGridView.setAdapter(profTagsAdapter);
                }else{
                    Log.e("HelperProfileFragment", "failure in populating tags");
                }
            }
        });
    }
}