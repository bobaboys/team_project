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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.Journal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ReceiverJournalFragment extends Fragment {

    public static final int EDIT_REQUEST_CODE = 20;
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";
    ArrayList<String> items;
    ArrayList<Journal> entries;
    ArrayAdapter<String> itemsAdapter; //class within Android widget package
    ListView lvItems;
    EditText newEntry;
    Button addEntry;
    protected String date;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receiver_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvItems = (ListView) view.findViewById(R.id.lvItems);
        addEntry = view.findViewById(R.id.btnAddItem_receiver);
        newEntry = view.findViewById(R.id.etNewItem_receiverjournal);
        Bundle bundle = getArguments();
        date = bundle.getString("date");
        readItems();
        itemsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter); //wire the adapter to the view

        populateJournal();
        setupListViewListener();

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemText = newEntry.getText().toString();
                itemsAdapter.add(itemText);
                Journal journal = new Journal();
                journal.setJournalUser(ParseUser.getCurrentUser());
                journal.setJournalEntry(itemText);
                journal.setDate(date);
                journal.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d("Journal entry", "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                    }
                });
                writeItems();
                newEntry.setText("");
                Toast.makeText(getContext(), "Journal entry added!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateJournal(){
        items.clear();
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<Journal> query = ParseQuery.getQuery(Journal.class);
        query.include(Constants.USER_FIELD);
        query.whereEqualTo(Constants.USER_FIELD, currentUser);
        query.findInBackground(new FindCallback<Journal>() {
            @Override
            public void done(List<Journal> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size(); i++){
                        items.add(objects.get(i).getDate() + ": " + objects.get(i).getJournalEntry());
                    }
                }else{
                    //something went wrong
                    Log.e("populating journal", "failure");
                }
            }
        });
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ReceiverJournalEditFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


    // returns the file in which the data is stored
    private File getDataFile() {
        return new File(getContext().getFilesDir(), "todo.txt");
    }

    // read the items from the file system
    private void readItems() {
        try {
            // create the array using the content in the file
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
            // just load an empty list
            items = new ArrayList<>();
        }
    }

    // write the items to the filesystem
    private void writeItems() {
        try {
            // save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
        }
    }



}
