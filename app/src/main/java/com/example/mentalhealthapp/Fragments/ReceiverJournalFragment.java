package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
//TODO: if time allows, finish creating the journal fragment

public class ReceiverJournalFragment extends Fragment {

    public static final int EDIT_REQUEST_CODE = 20;
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter; //class within Android widget package
    ListView lvItems;
    EditText newEntry;
    Button addEntry;

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
        readItems();
        itemsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter); //wire the adapter to the view

//        items.add("First todo item");
//        items.add("Second todo item");
        setupListViewListener();

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemText = newEntry.getText().toString();
                itemsAdapter.add(itemText);
                writeItems();
                newEntry.setText("");
                Toast.makeText(getContext(), "Journal entry added!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems(); //TODO get rid of this
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // first parameter is the context, second is the class of the activity to launch
//                Intent i = new Intent(getContext(), ReceiverJournalEditFragment.class);
//                // put "extras" into the bundle for access in the edit activity
//                i.putExtra(ITEM_TEXT, items.get(position));
//                i.putExtra(ITEM_POSITION, position);
//                // brings up the edit activity with the expectation of a result
//                startActivityForResult(i, EDIT_REQUEST_CODE);
                Fragment fragment = new ReceiverJournalEditFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer_main, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    //todo delete the bottom three methods once you connect to parse server

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
