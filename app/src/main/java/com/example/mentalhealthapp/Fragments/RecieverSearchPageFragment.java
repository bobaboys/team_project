package com.example.mentalhealthapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

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

import Utils.Utils;

public class RecieverSearchPageFragment extends Fragment {
    public static String TAG = "RecieverSearchPageFragment";

    FloatingActionButton searchForHelpers;
    List<Tag> tags;
    RecyclerView rvTags;
    TagsAdapter tagsAdapter;
    android.widget.SearchView tagsSearch;
    private MediaPlayer buttonClickSound;

    FindCallback<Tag> addAllTagsToAdapterCallback = new FindCallback<Tag>() {
        @Override
        public void done(List<Tag> objects, ParseException e) {
            if (e != null) {
                Log.e(TAG, "Error loading tags from parse server");
                e.printStackTrace();
                return;
            }
            tags.addAll(objects);
            tagsAdapter.setTagsListFull();
            tagsAdapter.notifyDataSetChanged();
        }
    };


    View.OnClickListener searchButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            searchForHelpers.startAnimation(animation);
            TagsParcel selectedTagsParcel= new TagsParcel();
            selectedTagsParcel.selectedTags =tagsAdapter.selectedTags;

            Fragment fragment = new HelperBiosFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedTags", Parcels.wrap(selectedTagsParcel));
            fragment.setArguments(bundle);
            Utils.switchToAnotherFragment(fragment,
                    getActivity().getSupportFragmentManager(),
                    R.id.flContainer_main);
        }
    };


    android.widget.SearchView.OnQueryTextListener searchViewListener = new android.widget.SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            tagsAdapter.getFilter().filter(newText);
            return false;
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reciever_searchpage, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onViewCreated(view, savedInstanceState);
        buttonClickSound = MediaPlayer.create(getContext(),
                R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        rvTags = view.findViewById(R.id.rvTagsSearch);
        searchForHelpers = view.findViewById(R.id.fb_search_for_helpers);
        tags = new ArrayList<>();
        searchForHelpers.setOnClickListener(searchButtonListener);
        setRecyclerView();
        queryAllTags(addAllTagsToAdapterCallback);
        setSearchView(view);
        hideKeyboardFrom(getContext(),view);
    }


    private void setSearchView(View view){
        tagsSearch = view.findViewById(R.id.searchView_tags);
        tagsSearch.setOnQueryTextListener(searchViewListener);
        tagsSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tagsSearch.onActionViewExpanded();
        tagsSearch.setQueryHint("Search for issues you want help with");
    }


    private void setRecyclerView() {
        tagsAdapter = new TagsAdapter(this.getContext(), tags);
        rvTags.setAdapter(tagsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        rvTags.setLayoutManager(layoutManager);
    }


    private void queryAllTags(FindCallback<Tag> findCallback) {
        ParseQuery<Tag> postsQuery = new ParseQuery<Tag>(Tag.class);
        postsQuery.setLimit(150);
        postsQuery.addDescendingOrder("Category");
        /*We decided load all tags (and on code select which ones match with the search FOR LATER)
         * we are concern that it could be lots of information on the database and we would need to set
         * a limit of rows. In this case our tags are 150 tops. */
        postsQuery.findInBackground(findCallback);
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
