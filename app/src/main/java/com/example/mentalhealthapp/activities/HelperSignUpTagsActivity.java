package com.example.mentalhealthapp.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mentalhealthapp.Fragments.SelectTagsSignUpFragment;
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

    private LinearLayout container;
    private MediaPlayer buttonClickSound;
    private Fragment currentFragment;
    private TextView title;

    SaveCallback genericSaveCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e != null) {
                Log.d(TAG, "Error while saving");
                e.printStackTrace();
                return;
            }
        }
    };

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentFragment.getClass().equals(SelectTagsSignUpFragment.class)) {
                buttonClickSound.start();
                final Animation animation = AnimationUtils.loadAnimation(HelperSignUpTagsActivity.this, R.anim.bounce);
                submit.startAnimation(animation);
                saveTagsOnParse();
                Intent intent = new Intent(HelperSignUpTagsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    };


    private void saveTagsOnParse(){
        SelectTagsSignUpFragment selectTagsSignUpFragment = (SelectTagsSignUpFragment) currentFragment;
        for (int i = 0; i < selectTagsSignUpFragment.getTagsAdapter().getSelectedTags().size(); i++) {
            HelperTags helperTags = new HelperTags();
            helperTags.setHelperTags(ParseUser.getCurrentUser(),  selectTagsSignUpFragment.getTagsAdapter().getSelectedTags().get(i));
            helperTags.saveInBackground(genericSaveCallback);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_sign_up_tags);
        setViewElements();
        replaceFragment(new SelectTagsSignUpFragment());


    }

    private void setViewElements(){
        buttonClickSound = MediaPlayer.create(this, R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        container = findViewById(R.id.ly_container);
        title = findViewById(R.id.title_select_tags);
        submit = findViewById(R.id.btnSubmit_Helper);
        submit.setOnClickListener(submitListener);
    }

    private void setVisibleElements(Fragment newFragment){
        boolean isRvVisible = newFragment.getClass().equals(SelectTagsSignUpFragment.class);
        submit.setVisibility(isRvVisible ? ConstraintLayout.VISIBLE : ConstraintLayout.GONE);
        title.setVisibility(isRvVisible ? ConstraintLayout.VISIBLE : ConstraintLayout.GONE);
    }

    public void replaceFragment(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        setVisibleElements(newFragment);
        if(currentFragment==null)
            transaction.add(R.id.ly_container, newFragment);
        else
            transaction.replace(R.id.ly_container, newFragment);
        currentFragment =newFragment;
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
