package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HelperBiosActivity extends AppCompatActivity {

    protected HelperBiosAdapter biosAdapter;
    protected RecyclerView rvBios;
    protected List<ParseUser> mBios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_bios);

        rvBios = findViewById(R.id.rvHelperBios);
        mBios = new ArrayList<>();
        setRecyclerView();


        loadBios();
    }

    private void setRecyclerView() {
        biosAdapter = new HelperBiosAdapter(this, mBios);
        rvBios.setAdapter(biosAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBios.setLayoutManager(layoutManager);
    }

    private void loadBios() {
        ParseQuery<ParseUser> postsQuery = new ParseQuery<ParseUser>(ParseUser.class);
        postsQuery.setLimit(20); //TODO: change 20
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
