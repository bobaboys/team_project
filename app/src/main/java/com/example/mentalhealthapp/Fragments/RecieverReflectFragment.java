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

public class RecieverReflectFragment extends Fragment {

    protected TextView journalDate;
    protected CalendarView calendarView;
    protected Button createEntry;
    protected Button allEntries;
    protected String date;
    public final String CREATE_KEY = "Create Entry";
    public final String EDIT_KEY = "View Entry";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        journalDate = view.findViewById(R.id.myDate);
        calendarView = view.findViewById(R.id.calendarView);
        createEntry = view.findViewById(R.id.btn_createEntry);
        allEntries = view.findViewById(R.id.btnAllEntries_Journal);
        date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(month + 1 < 10){
                    date = "0" + (month + 1) + "/" + dayOfMonth + "/" + year;
                }else{
                    date = (month + 1) + "/" + dayOfMonth + "/" + year;
                }
                journalDate.setText(date);
                alreadyExists();
            }
        });

        allEntries.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Fragment fragment = new ReceiverJournalFragment();
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                fragment.setArguments(bundle);
                switchToAnotherFragment(fragment);
            }
        });

        createEntry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Fragment fragment = new ReceiverCreateEntryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                if(createEntry.getText().equals(CREATE_KEY)){
                    bundle.putBoolean("alreadyExists", false);
                }else{
                    bundle.putBoolean("alreadyExists", true);
                }
                fragment.setArguments(bundle);
                switchToAnotherFragment(fragment);
            }
        });
    }

    private void alreadyExists(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Journal> query = ParseQuery.getQuery(Journal.class);
        query.include(Constants.USER_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, currentUser);
        query.findInBackground(new FindCallback<Journal>() {
            @Override
            public void done(List<Journal> objects, ParseException e) {
                if(e == null){
                    createEntry.setText(CREATE_KEY);
                    for(int i = 0; i < objects.size(); i++){
                        if(RecieverReflectFragment.this.date.equals(objects.get(i).getDate())){
                            createEntry.setText(EDIT_KEY);
                            break;
                        }
                    }
                }else{
                    //something went wrong
                    Log.e("populating journal", "failure");
                }
            }
        });
    }

    private void switchToAnotherFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
