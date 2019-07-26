package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.mentalhealthapp.R;

public class ReceiverJournalEditFragment extends Fragment {
    protected EditText etItemText;
    protected int position;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receiver_journaledit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        etItemText = (EditText) view.findViewById(R.id.etItemText);
//        // set the text field's content from the intent
//        etItemText.setText(getIntent().getStringExtra(ITEM_TEXT));
//        // track the position of the item in the list
//        position = getIntent().getIntExtra(ITEM_POSITION, 0);
    }
}
