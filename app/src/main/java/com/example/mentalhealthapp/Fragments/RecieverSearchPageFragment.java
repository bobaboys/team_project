package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.TagsAdapter;
import com.example.mentalhealthapp.model.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class RecieverSearchPageFragment extends Fragment {
    public static String TAG = "RecieverSearchPageFragment";

    FloatingActionButton searchForHelpers;
    List<Tag> tags;
    RecyclerView rvTags;
    TagsAdapter tagsAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reciever_searchpage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvTags = view.findViewById(R.id.rvTagsSearch);
        searchForHelpers = view.findViewById(R.id.fb_search_for_helpers);
        tags = new ArrayList<>();

        setRecyclerView();
        getAllTags();


    }

    public void onClick(){
        //TODO Click of Floating Action Button. search for helpers and show list of helpers fragment/activity
    }
    private void setRecyclerView() {
        tagsAdapter = new TagsAdapter(this.getContext(), tags);
        rvTags.setAdapter(tagsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        rvTags.setLayoutManager(layoutManager);
    }
    private void getAllTags() {
        //TODO SEARCH BAR REGEX
        ParseQuery<Tag> postsQuery = new ParseQuery<Tag>(Tag.class);
        postsQuery.setLimit(50);
        /*We decided load all tags (and on code select which ones match with the search FOR LATER)
         * we are concern that it could be lots of information on the database and we would need to set
         * a limit of rows. In this case our tags are 50 tops. */
        postsQuery.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error loading tags from parse server");
                    e.printStackTrace();
                    return;
                }
                tags.addAll(objects);
                tagsAdapter.notifyDataSetChanged();
            }
        });
    }
}
