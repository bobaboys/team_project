package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.parse.ParseUser;

public class HelperSignUpBioActivity extends AppCompatActivity {

    public final String HELPER_BIO_FIELD = "helperBio";
    TextView HelperBioPrompt;
    EditText HelperBioInput;
    Button Submit;
    View.OnClickListener submitBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String bioInput = HelperBioInput.getText().toString();
            ParseUser user = ParseUser.getCurrentUser();
            user.put(HELPER_BIO_FIELD,bioInput);
            user.saveInBackground();
            Intent intent = new Intent(HelperSignUpBioActivity.this, HelperSignUpTagsActivity.class);
            startActivity(intent);
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
