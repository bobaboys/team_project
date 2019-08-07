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
import android.widget.Button;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.adapters.HelperBiosAdapter;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.TagsParcel;
import com.example.mentalhealthapp.models.UserWithTags;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HelperBiosFragment extends Fragment {

    private HelperBiosAdapter biosAdapter;
    private RecyclerView rvBios;
    private List<UserWithTags> mBios;
    private TagsParcel tags;
    private TextView alternateText, title;
    private Button back;


    View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).setCurrentFragment(new RecieverSearchPageFragment());
        }
    };


    FindCallback<HelperTags> loadBios= new FindCallback<HelperTags>() {
        @Override
        public void done(List<HelperTags> objects, ParseException e) {
            if(e!=null){
                Log.e("Helper Bio Activity", "error with bio");
                e.printStackTrace();
                return;
            }
            visibilityLayout(objects.size()==0);
            if(objects.size()!=0) fillListOfBios(objects);
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper_bios, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting list of tags selected by reciever.
        Bundle bundle = getArguments();
        tags = (TagsParcel) Parcels.unwrap( bundle.getParcelable("selectedTags"));

        setViewComponents(view);
        mBios = new ArrayList<>();

        back.setOnClickListener(backListener);
        setRecyclerView();
        queryHelpersAccordingTags(loadBios);
    }


    private void setViewComponents(View view){
        rvBios = view.findViewById(R.id.rvHelperBios);
        alternateText = view.findViewById(R.id.tv_alternate_text);
        title = view.findViewById(R.id.tv_title_bios_list);
        back = view.findViewById(R.id.btn_alternate_back);
    }


    private void setRecyclerView() {
        biosAdapter = new HelperBiosAdapter(this.getContext(), mBios);
        rvBios.setAdapter(biosAdapter);
        rvBios.setLayoutManager( new LinearLayoutManager(this.getContext()));
    }


    private void queryHelpersAccordingTags(FindCallback<HelperTags> findCallback) {
        final ParseQuery<HelperTags> helpersTagsQ = new ParseQuery<>(HelperTags.class);
        helpersTagsQ.setLimit(150);
        helpersTagsQ.include("user");
        helpersTagsQ.whereContainedIn("Tag", tags.selectedTags); // looks for helpers with these tags.
        helpersTagsQ.findInBackground(findCallback);
    }


    private void visibilityLayout(boolean isEmpty){
        rvBios.setVisibility(isEmpty?
                ConstraintLayout.GONE : ConstraintLayout.VISIBLE);
        back.setVisibility(isEmpty?
                ConstraintLayout.VISIBLE : ConstraintLayout.GONE);
        alternateText.setVisibility(isEmpty?
                ConstraintLayout.VISIBLE : ConstraintLayout.GONE);
        title.setVisibility(isEmpty?
                ConstraintLayout.GONE : ConstraintLayout.VISIBLE);
    }


    private void fillListOfBios(List<HelperTags> helperTagsList){
        ArrayList<UserWithTags> helpersWithAllTags= new ArrayList<>();
        for(int i = 0; i < helperTagsList.size(); i++){
            int j=searchUser(helpersWithAllTags,helperTagsList.get(i).getUser());
            if(j==-1){//User not found on list.
                helpersWithAllTags.add(
                        createUserWithAllTags(helperTagsList.get(i)));

            }else{
                // user was found, add tag to it.
                helpersWithAllTags.get(j).getTags().add(helperTagsList.get(i).getTag());
            }
        }
        biosAdapter.addAll(helpersWithAllTags);
        biosAdapter.notifyDataSetChanged();
    }


    private UserWithTags createUserWithAllTags(HelperTags helperTag){
        UserWithTags uwt = new UserWithTags();
        uwt.setUser(helperTag.getUser());
        uwt.getTags().add(helperTag.getTag());
        return uwt;
    }

    public int searchUser(ArrayList<UserWithTags> helpersWithAllTags, ParseUser user){
        if(user==null)return -1;
        for (int i = 0; i < helpersWithAllTags.size(); i++) {
            if (user.getObjectId().equals(helpersWithAllTags.get(i).getUser().getObjectId())) return i;
        }
        return -1;
    }
}
