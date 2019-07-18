package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

public class HelperSignUpTags extends AppCompatActivity {

    protected static final String HELPER_TAGS_PARSE_OBJECT = "HelperTags";
    protected static final String USER_PARSE_CLASS = "user";
    protected static final String COLOR_PARSE_ATTRIBUTE = "Color";
    protected static final String TAG = "tag";

    CheckBox red;
    CheckBox orange;
    CheckBox yellow;
    CheckBox green;
    CheckBox blue;
    CheckBox purple;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_sign_up_tags);

        red = findViewById(R.id.checkBoxRed_Helper);
        orange = findViewById(R.id.checkBoxOrange_Helper);
        yellow = findViewById(R.id.checkBoxYellow_Helper);
        green = findViewById(R.id.checkBoxGreen_Helper);
        blue = findViewById(R.id.checkBoxBlue_Helper);
        purple = findViewById(R.id.checkBoxPurple_Helper);

        final ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
        checkboxes.add(red);
        checkboxes.add(orange);
        checkboxes.add(yellow);
        checkboxes.add(green);
        checkboxes.add(blue);
        checkboxes.add(purple);

        submit = findViewById(R.id.btnSubmit_Helper);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                //ArrayList<String> checked = new ArrayList<String>();
                for(int i = 0; i < checkboxes.size(); i++){
                    CheckBox box = checkboxes.get(i);
                    if(checkboxes.get(i).isChecked()){
                        CharSequence text = box.getText();
                            ParseObject tags = ParseObject.create(HELPER_TAGS_PARSE_OBJECT);
                            tags.put(USER_PARSE_CLASS, user);
                            tags.put(COLOR_PARSE_ATTRIBUTE, text);
                            tags.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e!=null){
                                        Log.d(TAG, "Error while saving");
                                        e.printStackTrace();
                                        return;
                                    }
                                }
                            }); //this saves it onto the server
                    }
                }
            }
        });
    }


}
