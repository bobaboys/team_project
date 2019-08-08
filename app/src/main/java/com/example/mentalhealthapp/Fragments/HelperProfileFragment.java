package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.LoginActivity;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.adapters.SelectedTagsAdapter;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import Utils.Utils;

public class HelperProfileFragment extends Fragment {

    private Button logOutbtn;
    private TextView helperProfileBio;
    private ImageView helperProfileAvatar;
    private TextView helperName;
    private FloatingActionButton editHelperProfile;
    private MediaPlayer buttonClickSound;
    private GridView tagsGridView;
    private ArrayList<String> allHelperTags = new ArrayList<>();;
    private SelectedTagsAdapter profTagsAdapter;

    protected View.OnClickListener logoutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            logOutbtn.startAnimation(animation);
            ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    };


    protected  View.OnClickListener editListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).setCurrentFragment(new HelperEditProfileFragment());
        }
    };


    protected  FindCallback<HelperTags> getTagsCallback = new FindCallback<HelperTags>() {
        @Override
        public void done(List<HelperTags> objects, ParseException e) {
            if(e!=null) {
                Log.e("HelperProfileFragment", "failure in populating tags");
                return;
            }
            addAllTagsToHelper(objects);
            profTagsAdapter = new SelectedTagsAdapter(getContext(),allHelperTags);
            tagsGridView.setAdapter(profTagsAdapter);
        }
    };


    private void addAllTagsToHelper(List<HelperTags>  helperTags){
        for(int i = 0; i < helperTags.size(); i++){
            Object strTag;
            strTag=((Tag)helperTags.get(i).get("Tag")).get("Tag");
            allHelperTags.add(strTag.toString());
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonClickSound = MediaPlayer.create(getContext(),
                R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        assignViews( view);
        Utils.setProfileImage(helperProfileAvatar);
        setListeners();
        setContentOnViews();
        allHelperTags.clear();
        queryHelperTags(getTagsCallback);

    }


    private void setListeners(){
        logOutbtn.setOnClickListener(logoutBtnListener);
        editHelperProfile.setOnClickListener(editListener);
    }


    private void assignViews(View view){
        helperProfileBio =view.findViewById(R.id.tvMyBio_helper_profile);
        helperProfileAvatar = view.findViewById(R.id.ivAvatar_helper_profile);
        tagsGridView = view.findViewById(R.id.gv_tags_helper_prof);
        helperName = view.findViewById(R.id.tv_username_helperProfile);
        logOutbtn = view.findViewById(R.id.btnLogout_ProfileHelper);
        editHelperProfile = view.findViewById(R.id.fab_Edit_HelperProfile);
    }


    private void setContentOnViews(){
        helperName.setText(ParseUser.getCurrentUser().getString(Constants.NAME_FIELD));
        helperProfileBio.setText(ParseUser.getCurrentUser().getString(Constants.HELPER_BIO_FIELD));
        helperProfileBio.setText(ParseUser.getCurrentUser().getString(Constants.HELPER_BIO_FIELD));
    }


    private void queryHelperTags(FindCallback<HelperTags> findCallback){
        ParseQuery<HelperTags> query = ParseQuery.getQuery(HelperTags.class);
        query.include(Constants.USER_FIELD);
        query.include(Constants.TAG_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, ParseUser.getCurrentUser());
        query.findInBackground(findCallback);
    }
}