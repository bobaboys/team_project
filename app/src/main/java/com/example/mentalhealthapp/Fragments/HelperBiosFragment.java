package com.example.mentalhealthapp.Fragments;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mentalhealthapp.HelperBiosAdapter;
import com.example.mentalhealthapp.HelperTags;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.model.TagsParcel;
import com.example.mentalhealthapp.model.UserWithTags;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class HelperBiosFragment extends Fragment {

    protected HelperBiosAdapter biosAdapter;
    protected RecyclerView rvBios;
    protected List<UserWithTags> mBios;
    protected TagsParcel tags;

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

        rvBios = view.findViewById(R.id.rvHelperBios);
        mBios = new ArrayList<>();
        setRecyclerView();
        loadBios();
    }

    private void setRecyclerView() {
        biosAdapter = new HelperBiosAdapter(this.getContext(), mBios);
        rvBios.setAdapter(biosAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        rvBios.setLayoutManager(layoutManager);
    }

    private void loadBios() {
        final ParseQuery<HelperTags> helpersTagsQ = new ParseQuery<>(HelperTags.class);
        helpersTagsQ.setLimit(20); //TODO: change 20
        helpersTagsQ.include("user");//TODO PROBLEMS WITH QUERIES
        //helpersTagsQ.whereContainedIn("Tag", tags.selectedTags); // looks for helpers with these tags.
        helpersTagsQ.findInBackground(new FindCallback<HelperTags>() {
            @Override
            public void done(List<HelperTags> objects, ParseException e) {
                if(e!=null){
                    Log.e("Helper Bio Activity", "error with bio");
                    e.printStackTrace();
                    return;
                }
                ArrayList<UserWithTags> helpersWithAllTags= new ArrayList<>();
                for(int i = 0; i < objects.size(); i++){
                    HelperTags helper = objects.get(i);
                    //TODO para cada usuario con tag, hacer un nuevo a un userWith tags o en su defecto a;adir solo tag a UWT existente
                    int j=searchUser(helpersWithAllTags,helper.getUser());
                    if(j==-1){//User not found on list.
                        // Make new UserWT and add first tag
                        UserWithTags uwt = new UserWithTags();
                        uwt.user = helper.getUser();
                        uwt.tags.add(helper.getTag());
                        //Add new user with tags to arraylist
                        helpersWithAllTags.add(uwt);

                    }else{
                        // user was found, add tag to it.
                        helpersWithAllTags.get(j).tags.add(helper.getTag());
                    }
                }
                biosAdapter.addAll(helpersWithAllTags);
                biosAdapter.notifyDataSetChanged();
            }
        });
    }

    public int searchUser(ArrayList<UserWithTags> helpersWithAllTags, ParseUser user){
        if (user.getObjectId() == null) {
            for (int i = 0; i < helpersWithAllTags.size(); i++)
                if (helpersWithAllTags.get(i).user.getObjectId()==null)
                    return i;
        } else {
            for (int i = 0; i < helpersWithAllTags.size(); i++)
                if (user.getObjectId().equals(helpersWithAllTags.get(i).user.getObjectId()))
                    return i;
        }
        return -1;
    }
}
