package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mentalhealthapp.model.Tag;


public class TagDetailsFragment extends Fragment {
    Tag tag;
    TextView title, description, link;
    public TagDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        Bundle bundle = getArguments();
        tag = (Tag) bundle.getParcelable("tag");
        title = view.findViewById(R.id.tv_tag_title);
        description = view.findViewById(R.id.tv_tag_details_content);
        link = view.findViewById(R.id.tv_tag_link);

        title.setText(tag.getString("Tag"));
        description.setText(tag.getString("Description"));
        link.setText(tag.getString("Link"));


    }
}
