package com.example.mentalhealthapp.Fragments;

import android.media.MediaPlayer;
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
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.Journal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import Utils.Utils;

public class ReflectFragment extends Fragment {

    protected TextView journalDate;
    protected CalendarView calendarView;
    protected Button createEntry;
    protected Button allEntries;
    protected String date;
    public final String CREATE_KEY = "Create Entry";
    public final String EDIT_KEY = "View Entry";
    protected MediaPlayer buttonClickSound;
    private boolean entryExist, checkedIfExist;

    public void setEntryExist(boolean entryExist) {
        this.entryExist = entryExist;
    }


    FindCallback<Journal> checkIfEntryExist = new FindCallback<Journal>() {
        public void done(List<Journal> objects, ParseException e) {
            if(e == null){
                setIfEntryExist(objects);
            }else{
                //something went wrong
                Log.e("populating journal", "failure");
            }
        }
    };


    private CalendarView.OnDateChangeListener daySelectedListener= new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            String dZero=dayOfMonth<10?"0":"";
            String mZero=month < 9?"0":"";
            date = mZero + (month + 1) + "/"
                    +dZero+ dayOfMonth + "/" + year;
            journalDate.setText(date);
            getAllEntriesFromServer( checkIfEntryExist);
        }
    };


    private View.OnClickListener allEntriesListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            Fragment fragment = new JournalFragment();
            Utils.switchToAnotherFragment(fragment,
                    getActivity().getSupportFragmentManager(),
                    R.id.flContainer_main);
        }
    };


    private View.OnClickListener createEntryListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(!checkedIfExist) getAllEntriesFromServer( checkIfEntryExist);

            buttonClickSound.start();
            Fragment fragment = new CreateEntryJournalFragment();
            Bundle bundle = new Bundle();
            bundle.putString("date", date);
            bundle.putBoolean("checkedIfExist", checkedIfExist);
            bundle.putBoolean("alreadyExists", entryExist);

            fragment.setArguments(bundle);
            Utils.switchToAnotherFragment(fragment,
                    getActivity().getSupportFragmentManager(),
                    R.id.flContainer_main);

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        entryExist = false;
        checkedIfExist = false;
        setViewComponents(view);
        date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        setListeners();
    }

    private void setListeners(){
        calendarView.setOnDateChangeListener(daySelectedListener);
        allEntries.setOnClickListener(allEntriesListener);
        createEntry.setOnClickListener(createEntryListener);
    }

    private void getAllEntriesFromServer(FindCallback<Journal> findCallback) {
        ParseQuery<Journal> query = ParseQuery.getQuery(Journal.class);
        query.include(Constants.USER_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, ParseUser.getCurrentUser());

        query.findInBackground(findCallback);
        checkedIfExist = true;
    }


    private void setIfEntryExist(List<Journal> objects) {
        createEntry.setText(CREATE_KEY);
        ReflectFragment.this.setEntryExist(false);
        for(int i = 0; i < objects.size(); i++){
            if(ReflectFragment.this.date.equals(objects.get(i).getDate())){
                createEntry.setText(EDIT_KEY);
                setEntryExist(true);
                break;
            }
        }
    }


    private void setViewComponents(View view){
        buttonClickSound = MediaPlayer.create(getContext(), R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        journalDate = view.findViewById(R.id.myDate);
        calendarView = view.findViewById(R.id.calendarView);
        createEntry = view.findViewById(R.id.btn_createEntry);
        allEntries = view.findViewById(R.id.btnAllEntries_Journal);
    }
}
