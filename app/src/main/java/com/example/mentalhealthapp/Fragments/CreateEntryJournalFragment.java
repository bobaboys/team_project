package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.Journal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CreateEntryJournalFragment extends Fragment {
    private  TextView date;
    private  EditText journalEntry;
    private  Button save;
    private  String dateOfEntry;
    private  boolean alreadyExists;
    private  Journal existingEntry;
    private  MediaPlayer buttonClickSound;
    private  boolean checkedIfExist;
    private ImageButton takeJournalPic;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;


    private SaveCallback onBackPressed = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if(e!=null){
                e.printStackTrace();
                return;
            }
            (CreateEntryJournalFragment.this.getActivity()).onBackPressed();
        }
    };


    private FindCallback<Journal> setJournalEntryCallback = new FindCallback<Journal>() {
        @Override
        public void done(List<Journal> objects, ParseException e) {
            alreadyExists = objects.size()!=0;
            if(objects.size()==0)return;
            journalEntry.setText( objects.get(0).getJournalEntry());
            existingEntry = objects.get(0);
        }
    };

    protected View.OnClickListener takePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            save.startAnimation(animation);


        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            }
        }
    }

    protected View.OnClickListener saveNewEntryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            save.startAnimation(animation);
            if(alreadyExists){
                //existingEntry.setJournalEntry(journalEntry.getText().toString());
                Log.d("save entry", "journal entry exists");
                existingEntry.put("journalEntry", journalEntry.getText().toString());
                existingEntry.saveInBackground(onBackPressed);
            }else{
                //save the new journal entry to the parse server
                Log.d("save entry", "journal doesn't yet exist and a new entry is being created");
                createOnDatabaseJournalEntry(onBackPressed);
            }
        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receiver_createjournal, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        existingEntry = new Journal();

        buttonClickSound = MediaPlayer.create(getContext(), R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        setViewComponents(view);
        getBundleArguments();

        queryEntriesByUserDate(setJournalEntryCallback);
        date.setText(dateOfEntry);
        save.setOnClickListener(saveNewEntryListener);
        takeJournalPic.setOnClickListener(takePicListener);
    }


    private  void setViewComponents(View view){
        date = view.findViewById(R.id.tv_date_createJournal);
        journalEntry = view.findViewById(R.id.et_addEntry_createJournal);
        save = view.findViewById(R.id.btn_saveEntry_createJournal);
        takeJournalPic = view.findViewById(R.id.btnTakePic_createJournalFragment);
    }


    private void getBundleArguments(){
        Bundle bundle = getArguments();
        dateOfEntry = bundle.getString("date");
        alreadyExists = bundle.getBoolean("alreadyExists");
        checkedIfExist = bundle.getBoolean("checkedIfExist");
    }


    private void queryEntriesByUserDate(FindCallback<Journal> findCallback){
        ParseQuery<Journal> query = ParseQuery.getQuery(Journal.class);
        query.include(Constants.USER_FIELD);
        query.include("date");
        query.whereEqualTo(Constants.USER_FIELD, ParseUser.getCurrentUser());
        query.whereEqualTo("date", dateOfEntry);
        query.findInBackground(findCallback);
    }


    private void createOnDatabaseJournalEntry(SaveCallback saveCallback){
        Journal journal = new Journal();
        journal.setJournalUser(ParseUser.getCurrentUser());
        journal.setDate(dateOfEntry);
        journal.setJournalEntry(journalEntry.getText().toString());
        journal.saveInBackground(saveCallback);
    }
}
