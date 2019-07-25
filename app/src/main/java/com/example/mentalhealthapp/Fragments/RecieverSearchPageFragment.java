package com.example.mentalhealthapp.fragments;

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

import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.TagsAdapter;
import com.example.mentalhealthapp.models.Tag;
import com.example.mentalhealthapp.models.TagsParcel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

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
        searchForHelpers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagsParcel selectedTagsParcel= new TagsParcel();
                selectedTagsParcel.selectedTags =tagsAdapter.selectedTags;

                Fragment resultFragment = new HelperBiosFragment();
                ((MainActivity)getActivity()).replaceFragment(resultFragment);

                //passing to result of list of helpers
                Bundle bundle = new Bundle();
                bundle.putParcelable("selectedTags", Parcels.wrap(selectedTagsParcel));
                resultFragment.setArguments(bundle);
            }
        });
        setRecyclerView();
        getAllTags();


    }

    private void setRecyclerView() {
        tagsAdapter = new TagsAdapter(this.getContext(), tags);
        rvTags.setAdapter(tagsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        rvTags.setLayoutManager(layoutManager);
    }
    private void getAllTags() {
        //TODO SEARCH BAR  (?)
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
