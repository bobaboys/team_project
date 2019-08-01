package com.example.mentalhealthapp.Fragments;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.ChatsListAdapter;
import com.example.mentalhealthapp.adapters.JournalAdapter;
import com.example.mentalhealthapp.models.Chat;
import com.example.mentalhealthapp.models.CompleteChat;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.Journal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JournalFragment extends Fragment {

    public static final int EDIT_REQUEST_CODE = 20;
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";
    ArrayList<Journal> entries;
    RecyclerView recyclerView;
    JournalAdapter journalAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receiver_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_journal);
        entries = new ArrayList<>();
        setRecyclerView();
        populateJournal();

    }
    private void setRecyclerView() {
        journalAdapter = new JournalAdapter(this.getContext(), entries, this);
        recyclerView.setAdapter(journalAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
    }



    private void populateJournal(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Journal> query = ParseQuery.getQuery(Journal.class);
        query.include(Constants.USER_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, currentUser);
        query.findInBackground(new FindCallback<Journal>() {
            @Override
            public void done(List<Journal> objects, ParseException e) {
                //Todo get objects and add to adapter.

                if(e!=null){
                    e.printStackTrace();
                    return;
                }
                for(Journal j : objects){
                    addByTimestamp(entries, j);
                }
                journalAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addByTimestamp(ArrayList<Journal> list, Journal j){
        //TODO
        int index=list.size(); // default case, add to the tail. (or head if empty)

        for(int i=0;i<list.size();i++){
            if(getDateTimeFormat(j.getDate())  >
                    getDateTimeFormat(list.get(i).getDate())){// bigger --> newer
                index = i;
                break;
            }
        }

        list.add(index,j);
        journalAdapter.notifyItemInserted(index);
        recyclerView.scrollToPosition(0);
    }
    private  long getDateTimeFormat(String strDate){
        try {
            Date date1=new SimpleDateFormat("MM/dd/yyyy").parse(strDate);
            return date1.getTime();
        }catch (Exception e) {
            return 0;
        }
    }

}
