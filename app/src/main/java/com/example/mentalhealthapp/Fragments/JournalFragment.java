package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.adapters.JournalAdapter;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.Journal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import Utils.Utils;

public class JournalFragment extends Fragment {

    private ArrayList<Journal> entries;
    private RecyclerView recyclerView;
    private JournalAdapter journalAdapter;
    private ImageView back;


    View.OnClickListener onBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).setCurrentFragment(new ReflectFragment());
        }
    };


    FindCallback<Journal> populateJournalByTime = new FindCallback<Journal>() {
        @Override
        public void done(List<Journal> objects, ParseException e) {
            if(e!=null){
                e.printStackTrace();
                return;
            }
            for(Journal journal : objects){
                addByTimestamp(entries, journal);
            }
            journalAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_journal);
        back = view.findViewById(R.id.iv_back_main_btn);
        back.setVisibility(ConstraintLayout.VISIBLE);
        back.setOnClickListener(onBack);
        entries = new ArrayList<>();
        setRecyclerView();
        queryJournalEntries(populateJournalByTime);

    }


    private void setRecyclerView() {
        journalAdapter = new JournalAdapter(this.getContext(), entries, this);
        recyclerView.setAdapter(journalAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }


    private void queryJournalEntries(FindCallback<Journal> findCallback){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Journal> query = ParseQuery.getQuery(Journal.class);
        query.include(Constants.USER_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, currentUser);
        query.findInBackground(findCallback);
    }


    private void addByTimestamp(ArrayList<Journal> entries, Journal journal){
        //TODO
        int addAt=entries.size(); // default case, add to the tail. (or head if empty)

        for(int i=0;i<entries.size();i++){
            if(Utils.milisFromDateSrt(journal.getDate())  >
                    Utils.milisFromDateSrt(entries.get(i).getDate())){// bigger --> newer
                addAt = i;
                break;
            }
        }
        entries.add(addAt,journal);
        journalAdapter.notifyItemInserted(addAt);
        recyclerView.scrollToPosition(0);
    }
}
