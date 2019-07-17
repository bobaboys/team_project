package com.example.mentalhealthapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private RadioGroup radioGroup;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameInput = findViewById(R.id.etUsername_signup);
        passwordInput = findViewById(R.id.etPassword_signup);
        emailInput = findViewById(R.id.etEmail_signup);
        radioGroup = findViewById(R.id.radioGroup);
        signup = findViewById(R.id.btnSignup_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                final String email = emailInput.getText().toString();
                final Boolean helper;

                if (radioGroup.getCheckedRadioButtonId() == R.id.radioBtnHelper_signup){
                    helper = true;
                }else{
                    helper = false;
                }

                signUp(username, password, email, helper);
            }
        });
    }

    private void signUp(String username, String password, String email, Boolean helper){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("helper", helper);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("SignUpActivity", "Sign up successful");
                    final Intent intent = new Intent (SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.e("SignUpActivity", "Sign up failure", e);
                    e.printStackTrace();
                }
            }
        });
    }
}
