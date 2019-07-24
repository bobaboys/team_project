package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private Button signUpBtn;
    private ParseUser currentUser;

    private final View.OnClickListener loginBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String username = usernameInput.getText().toString();
            final String password = passwordInput.getText().toString();
            login(username, password, currentUser);
        }
    };

    private final View.OnClickListener signupBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        currentUser = ParseUser.getCurrentUser();

        if(currentUser != null){
            Intent intent = new Intent (LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        AssignViewsAndListeners();
    }

    public void login(String username, String password, ParseUser user){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e==null){
                    Log.d("LoginActivity", "Login successful!");
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.e("LoginActivity", "Login failure");
                    Toast.makeText(LoginActivity.this,"Login failed!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void AssignViewsAndListeners() {
        usernameInput = findViewById(R.id.etUsername_login);
        passwordInput = findViewById(R.id.etPassword_login);
        loginBtn = findViewById(R.id.btnLogin_login);
        signUpBtn = findViewById(R.id.btnSignup_login);
        loginBtn.setOnClickListener(loginBtnListener);
        signUpBtn.setOnClickListener(signupBtnListener);
    }
}
