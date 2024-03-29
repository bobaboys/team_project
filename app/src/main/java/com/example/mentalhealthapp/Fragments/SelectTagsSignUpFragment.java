package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.TagsAdapter;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SelectTagsSignUpFragment  extends Fragment {

    private RecyclerView rvTags;
    private TagsAdapter tagsAdapter;
    private List<Tag> tags;
    protected static final String TAG = "tag: ";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_tags_sign_up, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setAndPopulateRvTags(view);
        getAllTags();

    }

    private void setAndPopulateRvTags(View view) {
        rvTags = view.findViewById(R.id.rvTags_SignUpTags);
        rvTags.setVisibility(ConstraintLayout.VISIBLE);
        tags = new ArrayList<>();
        tagsAdapter = new TagsAdapter(getContext(), tags);
        rvTags.setAdapter(tagsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTags.setLayoutManager(layoutManager);
    }

    private void getAllTags() {
        ParseQuery<Tag> postsQuery = new ParseQuery<Tag>(Tag.class);
        postsQuery.setLimit(100);
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
    public TagsAdapter getTagsAdapter() {
        return tagsAdapter;
    }
}
