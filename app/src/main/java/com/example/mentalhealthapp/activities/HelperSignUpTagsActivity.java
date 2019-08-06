package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.adapters.TagsAdapter;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class HelperSignUpTagsActivity extends AppCompatActivity {

    protected static final String TAG = "tag: ";


    private Button submit;
    private RecyclerView rvTags;
    private TagsAdapter tagsAdapter;
    private List<Tag> tags;
    private List<Tag> tagsFull;
    private  MediaPlayer buttonClickSound;

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(HelperSignUpTagsActivity.this, R.anim.bounce);
            submit.startAnimation(animation);
            //save all tags on server for parse user
            for(int i = 0; i < tagsAdapter.getSelectedTags().size(); i++){
                HelperTags helperTags = new HelperTags();
                helperTags.setHelperTags(ParseUser.getCurrentUser(), tagsAdapter.getSelectedTags().get(i));
                helperTags.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            Log.d(TAG, "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                    }
                });
            }

            Intent intent = new Intent(HelperSignUpTagsActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_sign_up_tags);
        buttonClickSound = MediaPlayer.create(this, R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);

        submit = findViewById(R.id.btnSubmit_Helper);
        submit.setOnClickListener(submitListener);

        setAndPopulateRvTags();
        getAllTags();

    }

    private void setAndPopulateRvTags() {
        rvTags = findViewById(R.id.rvTags_SignUpTags);
        tags = new ArrayList<>();
        tagsAdapter = new TagsAdapter(this, tags);
        rvTags.setAdapter(tagsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTags.setLayoutManager(layoutManager);
    }

    private void getAllTags() {
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
