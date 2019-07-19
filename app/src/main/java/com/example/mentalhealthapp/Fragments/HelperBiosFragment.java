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
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.model.TagsParcel;
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
    protected List<ParseUser> mBios;
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
        ParseQuery<ParseUser> postsQuery = new ParseQuery<ParseUser>(ParseUser.class);
        postsQuery.setLimit(20); //TODO: change 20
        //TODO QUERY WITH TAGS
        postsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e!=null){
                    Log.e("Helper Bio Activity", "error with bio");
                    e.printStackTrace();
                    return;
                }
                else{
                    mBios.addAll(objects);
                    biosAdapter.notifyDataSetChanged();
                    for(int i = 0; i < objects.size(); i++){
                        ParseUser user = objects.get(i);
                        Log.d("HelperBios Activity", "BIO =" + objects.get(i).getBoolean("helper"));
                    }
                }
            }
        });
    }
}
