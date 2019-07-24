package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.mentalhealthapp.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private RadioGroup radioGroup;
    private Button signup;
    private final String HELPER_FIELD = "helper";

    private final View.OnClickListener signUpBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            String email = emailInput.getText().toString();
            Boolean helper = radioGroup.getCheckedRadioButtonId() == R.id.radioBtnHelper_signup;
            signUp(username, password, email, helper);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        AssignViewsAndListeners();
    }

    public void signUp(String username, String password, String email, final Boolean helper){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(HELPER_FIELD, helper);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("SignUpActivity", "Sign up successful");
                    //Intent intent = new Intent(SignUpActivity.this, helper? HelperSignUpBioActivity.class : MainActivity.class);
                    Intent intent;
                    if(helper){
                        intent = new Intent(SignUpActivity.this, HelperSignUpBioActivity.class);
                        startActivity(intent);
                    }else{
                        intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }else{
                    Log.e("SignUpActivity", "Sign up failure", e);
                    e.printStackTrace();
                }
            }
        });
    }

    private void AssignViewsAndListeners() {
        usernameInput = findViewById(R.id.etUsername_signup);
        passwordInput = findViewById(R.id.etPassword_signup);
        emailInput = findViewById(R.id.etEmail_signup);
        radioGroup = findViewById(R.id.radioGroup);
        signup = findViewById(R.id.btnSignup_signup);
        signup.setOnClickListener(signUpBtnListener);
    }
}
