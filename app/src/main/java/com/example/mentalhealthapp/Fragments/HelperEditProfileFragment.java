package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.mentalhealthapp.HelperTags;
import com.example.mentalhealthapp.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class HelperEditProfileFragment extends Fragment {

    protected EditText editHelperBio;
    protected CheckBox red;
    protected CheckBox orange;
    protected CheckBox yellow;
    protected CheckBox green;
    protected CheckBox blue;
    protected CheckBox purple;
    protected Button saveChanges;

    protected static final String TAG = "tag";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editHelperBio = view.findViewById(R.id.et_EditBio_HelperEditProfile);
        red = view.findViewById(R.id.checkBox_Red_HelperEditProfile);
        orange = view.findViewById(R.id.checkBox_Orange_HelperEditProfile);
        yellow = view.findViewById(R.id.checkBox_Yellow_HelperEditProfile);
        green = view.findViewById(R.id.checkBox_Green_HelperEditProfile);
        blue = view.findViewById(R.id.checkBox_Blue_HelperEditProfile);
        purple = view.findViewById(R.id.checkBox_Purple_HelperEditProfile);
        saveChanges = view.findViewById(R.id.btn_SaveChanges_HelperEditProfile);

        final ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
        checkboxes.add(red);
        checkboxes.add(orange);
        checkboxes.add(yellow);
        checkboxes.add(green);
        checkboxes.add(blue);
        checkboxes.add(purple);

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                editBio(user);
                editTags(user, checkboxes);
                switchFragments();
            }
        });
    }

    public void editBio(ParseUser user){
        user.put("helperBio", editHelperBio.getText().toString());
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.d(TAG, "Error while saving");
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public void editTags(ParseUser user, ArrayList<CheckBox> checkboxes){
        for(int i = 0; i < checkboxes.size(); i++){
            CheckBox box = checkboxes.get(i);
            if(checkboxes.get(i).isChecked()){
                CharSequence text = box.getText();
                HelperTags tags = new HelperTags();
                tags.setHelperTags(user, text.toString());
                tags.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d(TAG, "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                    }
                }); //this saves it onto the server
            }
        }
    }



    public void switchFragments(){
        Fragment fragment = new HelperProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
