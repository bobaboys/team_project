package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.parse.ParseUser;

public class HelperSignUpBioActivity extends AppCompatActivity {

    public final String HELPER_BIO_FIELD = "helperBio";
    public final String NAME_FIELD = "name";

    private TextView HelperBioPrompt;
    private EditText HelperBioInput;
    private EditText NameInput;
    private TextView NamePrompt;
    private Button Submit;
    private  MediaPlayer buttonClickSound;

    View.OnClickListener submitBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(HelperSignUpBioActivity.this, R.anim.bounce);
            Submit.startAnimation(animation);
            String bioInput = HelperBioInput.getText().toString();
            String nameInput = NameInput.getText().toString();
            if(bioInput == null){
                Toast.makeText(HelperSignUpBioActivity.this, "Please enter a bio!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(nameInput == null){
                Toast.makeText(HelperSignUpBioActivity.this, "Please enter a name!", Toast.LENGTH_SHORT).show();
                return;
            }
            saveBioInBackground(nameInput, bioInput);
            Intent intent = new Intent(HelperSignUpBioActivity.this, HelperSignUpTagsActivity.class);
            startActivity(intent);
        }
    };


    private void saveBioInBackground(String nameInput, String bioInput){
        ParseUser user = ParseUser.getCurrentUser();
        user.put(NAME_FIELD,nameInput);
        user.put(HELPER_BIO_FIELD,bioInput);
        user.saveInBackground();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_sign_up_bio);
        buttonClickSound = MediaPlayer.create(this, R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        setViewComponents();
        Submit.setOnClickListener(submitBtnListener);
    }


    private void setViewComponents(){
        HelperBioPrompt = findViewById(R.id.tvBioPrompt_helperSignUpBios);
        HelperBioInput = findViewById(R.id.tvBioText_HelperSignUpBio);
        NamePrompt = findViewById(R.id.tvYourName_helperSignUpBio);
        NameInput= findViewById(R.id.etYourName_helperSignUpBio);
        Submit = findViewById(R.id.btnSubmit_HelperSignUpBios);
    }
}
