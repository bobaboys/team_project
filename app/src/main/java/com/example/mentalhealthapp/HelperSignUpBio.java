package com.example.mentalhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;

public class HelperSignUpBio extends AppCompatActivity {

    public final String HELPER_BIO_FIELD = "helperBio";
    public final String USER_FIELD ="User";
    TextView HelperBioPrompt;
    EditText HelperBioInput;
    Button Submit;
    ParseUser currentUser;
    View.OnClickListener submitBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String bioInput = HelperBioInput.getText().toString();
            ParseUser user = ParseUser.getCurrentUser();
            user.put(HELPER_BIO_FIELD,bioInput);
            user.saveInBackground();
            Intent intent = new Intent(HelperSignUpBio.this, HelperSignUpTags.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_sign_up_bio);

        HelperBioPrompt = findViewById(R.id.tvBioPrompt_helperSignUpBios);
        HelperBioInput = findViewById(R.id.tvBioText_HelperSignUpBio);
        Submit = findViewById(R.id.btnSubmit_HelperSignUpBios);
        Submit.setOnClickListener(submitBtnListener);
    }
}
