package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.LoginActivity;
import com.example.mentalhealthapp.R;
import com.parse.ParseUser;

public class RecieverProfileFragment extends Fragment {

    protected TextView testChat;
    protected Button btnLogOut;
    protected ImageView recProfileAvatar;
    protected TextView recProfileTags;
    protected TextView recProfileChats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reciever_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testChat = view.findViewById(R.id.recprofileTest);
        recProfileAvatar = view.findViewById(R.id.ivAvatar_reciever_profile);
        recProfileTags = view.findViewById(R.id.tvMyTags_reciever_profile);
        recProfileChats = view.findViewById(R.id.tvMyChats_reciever_profile);

        btnLogOut = view.findViewById(R.id.btnLogout_ProfileRec);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
