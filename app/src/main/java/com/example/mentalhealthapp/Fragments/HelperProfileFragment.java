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

public class HelperProfileFragment extends Fragment {

    protected TextView testProf;
    protected Button logOutbtn;
    protected TextView helperProfileBio;
    protected ImageView helperProfileAvatar;
    protected TextView helperProfileTags;
    protected TextView helperProfileChats;
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
        currentUser = ParseUser.getCurrentUser();

        logOutbtn.setOnClickListener(logoutBtnListener);
    }

}
