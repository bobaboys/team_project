package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.Journal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ReceiverCreateEntryFragment extends Fragment {
    protected TextView date;
    protected EditText journalEntry;
    protected Button save;
    protected String dateOfEntry;
    protected Boolean alreadyExists;
    protected Journal existingEntry;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receiver_createjournal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        date = view.findViewById(R.id.tv_date_createJournal);
        journalEntry = view.findViewById(R.id.et_addEntry_createJournal);
        save = view.findViewById(R.id.btn_saveEntry_createJournal);
        existingEntry = new Journal();

        Bundle bundle = getArguments();
        dateOfEntry = bundle.getString("date");
        alreadyExists = bundle.getBoolean("alreadyExists");
        if(alreadyExists){
            ParseUser currentUser = ParseUser.getCurrentUser();
            ParseQuery<Journal> query = ParseQuery.getQuery(Journal.class);
            query.include(Constants.USER_FIELD);
            query.include("date");
            query.whereEqualTo(Constants.USER_FIELD, currentUser);
            query.whereEqualTo("date", dateOfEntry);
            query.findInBackground(new FindCallback<Journal>() {
                @Override
                public void done(List<Journal> objects, ParseException e) {
                    for(int i = 0; i < objects.size(); i++){
                        existingEntry = objects.get(0);
                        journalEntry.setText(existingEntry.getJournalEntry());
                    }
                }
            });
        }
        date.setText(dateOfEntry);
        save.setOnClickListener(saveNewEntryListener);
    }

    protected View.OnClickListener saveNewEntryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(alreadyExists){
                existingEntry.setJournalEntry(journalEntry.getText().toString());
                existingEntry.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d("Existing journal entry", "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                    }
                });
            }else{
                //save the new journal entry to the parse server
                Journal journal = new Journal();
                journal.setJournalUser(ParseUser.getCurrentUser());
                journal.setDate(dateOfEntry);
                journal.setJournalEntry(journalEntry.getText().toString());
                journal.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d("New journal entry", "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                    }
                });
            }
        }
    };
}
