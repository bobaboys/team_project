package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mentalhealthapp.R;

import java.util.ArrayList;
//TODO: if time allows, finish creating the journal fragment

public class ReceiverJournalFragment extends Fragment {

    public static final int EDIT_REQUEST_CODE = 20;
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter; //class within Android widget package
    ListView lvItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receiver_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvItems = (ListView) view.findViewById(R.id.lvItems);
        itemsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter); //wire the adapter to the view
    }

}
