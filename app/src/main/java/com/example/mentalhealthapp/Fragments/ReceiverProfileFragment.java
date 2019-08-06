package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.LoginActivity;
import com.example.mentalhealthapp.activities.MainActivity;
import com.parse.ParseUser;

import Utils.Utils;

public class ReceiverProfileFragment extends Fragment {

    private  Button btnLogOut;
    private  ImageView recProfileAvatar;
    private  FloatingActionButton editRecieverProfile;
    private  TextView username;
    private  MediaPlayer buttonClickSound;

    public final String TAG = "Reciever Profiile:";

    protected View.OnClickListener logOutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            btnLogOut.startAnimation(animation);
            ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    };


    private View.OnClickListener editProfileListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            ((MainActivity)getActivity()).setCurrentFragment(new ReceiverEditProfileFragment());
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reciever_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutElements(view);
        setListeners();

        buttonClickSound = MediaPlayer.create(getContext(), R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        Utils.setProfileImage(recProfileAvatar);
        username.setText(ParseUser.getCurrentUser().getUsername());
    }


    private void  setLayoutElements(View view){
        recProfileAvatar = view.findViewById(R.id.ivAvatar_reciever_profile);
        username = view.findViewById(R.id.tv_username_receiverProfile);
        btnLogOut = view.findViewById(R.id.btnLogout_ProfileRec);
        editRecieverProfile = view.findViewById(R.id.fab_Edit_RecieverProfile);
    }


    private void setListeners(){
        btnLogOut.setOnClickListener(logOutBtnListener);
        editRecieverProfile.setOnClickListener(editProfileListener);
    }
}
