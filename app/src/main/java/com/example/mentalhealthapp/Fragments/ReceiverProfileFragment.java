package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.LoginActivity;
import com.example.mentalhealthapp.models.Constants;
import com.parse.ParseUser;

import Utils.Utils;

public class ReceiverProfileFragment extends Fragment {

    protected Button btnLogOut;
    protected ImageView recProfileAvatar;
    protected FloatingActionButton editRecieverProfile;
    protected TextView username;
    protected MediaPlayer buttonClickSound;

    public final String TAG = "Reciever Profiile:";

    protected View.OnClickListener logOutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    };


    private View.OnClickListener editProfileListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            Utils.switchToAnotherFragment(new ReceiverEditProfileFragment(),
                    getActivity().getSupportFragmentManager(),
                    R.id.flContainer_main);
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
