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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.LoginActivity;
import com.example.mentalhealthapp.models.Constants;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

import Utils.Utils;

public class RecieverProfileFragment extends Fragment {

    protected Button btnLogOut;
    protected ImageView recProfileAvatar;
    protected ParseUser currentUser;
    protected FloatingActionButton editRecieverProfile;
    protected TextView username;
    protected MediaPlayer buttonClickSound;

    public final String TAG = "Reciever Profiile:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    String AVATAR_FIELD = "avatar";

    protected View.OnClickListener logOutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
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
        buttonClickSound = MediaPlayer.create(getContext(), R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        recProfileAvatar = view.findViewById(R.id.ivAvatar_reciever_profile);
        ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(Constants.AVATAR_FIELD);
        Bitmap bm = Utils.convertFileToBitmap(avatarFile);
        recProfileAvatar.setImageBitmap(bm);
        currentUser = ParseUser.getCurrentUser();
        btnLogOut = view.findViewById(R.id.btnLogout_ProfileRec);
        btnLogOut.setOnClickListener(logOutBtnListener);
        editRecieverProfile = view.findViewById(R.id.fab_Edit_RecieverProfile);
        username = view.findViewById(R.id.tv_username_receiverProfile);
        username.setText(currentUser.getUsername());
        editRecieverProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickSound.start();
                Fragment fragment = new ReceiverEditProfileFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.pager, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

}
