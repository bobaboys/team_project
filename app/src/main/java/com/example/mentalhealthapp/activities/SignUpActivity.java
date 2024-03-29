package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private RadioGroup radioGroup;
    private Button signup;
    private  File photoFile;
    public final String TAG = "Sign Up Activity:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private  String photoFileName = "photo.jpg";
    private final String HELPER_FIELD = "helper";
    private final String AVATAR_FIELD = "avatar";
    private String CLICKED_AVATAR_KEY = "clicked_avatar";
    private  MediaPlayer buttonClickSound;

    static final int CHOOSE_AVATAR_REQUEST = 333;
    private ImageView avatarPic;
    private ImageButton takeAvatarPic;
    private Button chooseAvatar;
    private ParseFile parseFile;
    private  ParseUser user;
    private String username, password , email;
    private Boolean helper;


    private SaveCallback saveProfilePicCallback=new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if(e!=null){
                Log.d(TAG, "Error while saving");
                e.printStackTrace();
                return;
            }
        }
    };


    private SignUpCallback signUpCallback =new SignUpCallback() {
        @Override
        public void done(ParseException e) {
            if(e == null){
                Log.d(TAG, "Sign up successful");
                //adding photo to parse user
                user.put(AVATAR_FIELD, parseFile);
                user.saveInBackground(saveProfilePicCallback);
                Intent intent = new Intent(SignUpActivity.this,
                            helper? HelperSignUpBioActivity.class:
                                    MainActivity.class);
                startActivity(intent);

            }else{
                switch(e.getCode()){
                    case ParseException.USERNAME_TAKEN:{
                        Toast.makeText(SignUpActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case ParseException.EMAIL_TAKEN:{
                        Toast.makeText(SignUpActivity.this, "Email is already taken", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                checkEmailValid(email);
                Log.e(TAG, "Sign up failure", e);
                e.printStackTrace();
            }
        }
    };


    private final View.OnClickListener takeAvatarPicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.bounce);
            takeAvatarPic.startAnimation(animation);
            onLaunchCamera();
        }
    };


    private View.OnClickListener chooseAvatarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.bounce);
            chooseAvatar.startAnimation(animation);
            Intent intent = new Intent(SignUpActivity.this, AvatarImagesActivity.class);
            startActivityForResult(intent, CHOOSE_AVATAR_REQUEST);
        }
    };


    private final View.OnClickListener signUpBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.bounce);
            signup.startAnimation(animation);
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
            email = emailInput.getText().toString();
            helper = radioGroup.getCheckedRadioButtonId() == R.id.radioBtnHelper_signup;
            if(parseFile==null){
                Toast.makeText(SignUpActivity.this,"Please choose a profile picture!", Toast.LENGTH_SHORT).show();
                return;
            }
            signUp(username, password, email, helper, parseFile);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        buttonClickSound = MediaPlayer.create(this, R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        AssignViewsAndListeners();
        Toast.makeText(this, "Your username will be public! Choose accordingly", Toast.LENGTH_SHORT).show();
    }


    public void checkEmailValid(final String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!email.matches(emailPattern)){
            Toast.makeText(getApplicationContext(), "Email address is invalid", Toast.LENGTH_SHORT).show();
        }
    }


    public void signUp(final String username, String password, final String email, final Boolean helper, final ParseFile parseFile){
        user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(HELPER_FIELD, helper);
        user.signUpInBackground(signUpCallback);
    }


    private void AssignViewsAndListeners() {
        usernameInput = findViewById(R.id.etUsername_signup);
        passwordInput = findViewById(R.id.etPassword_signup);
        emailInput = findViewById(R.id.etEmail_signup);
        radioGroup = findViewById(R.id.radioGroup);
        signup = findViewById(R.id.btnSignup_signup);
        signup.setOnClickListener(signUpBtnListener);

        takeAvatarPic = findViewById(R.id.btnTakePic_signUpActivity);
        takeAvatarPic.setOnClickListener(takeAvatarPicListener);
        chooseAvatar = findViewById(R.id.btnPickAvatar_signUpActivity);
        chooseAvatar.setOnClickListener(chooseAvatarListener);
        avatarPic = findViewById(R.id.ivAvatarPic_SignUpActivity);
        avatarPic.setImageResource(R.drawable.blank_profile_picture);

    }


    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if code is same as code which we started activity with
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
                //load taken image into image view
                //Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap takenImage = Utils.Utils.rotateBitmapOrientation(photoFile.getAbsolutePath());
                avatarPic.setImageBitmap(takenImage);

                //assign taken photo to parse file
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                takenImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] picData = stream.toByteArray();
                parseFile = new ParseFile("image.png", picData);
                photoFile = null;
            }

        if(requestCode == CHOOSE_AVATAR_REQUEST && resultCode == RESULT_OK){
            //get pic from intent and set image view
            Integer avatar = data.getIntExtra(CLICKED_AVATAR_KEY, R.drawable.icons8_bear_100);
            avatarPic.setImageResource(avatar);

            //assign chosen avatar to parseFile
            Bitmap avatarBitmap = (Bitmap) BitmapFactory.decodeResource(this.getResources(), avatar);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            avatarBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] picData = stream.toByteArray();
            parseFile = new ParseFile("image.png", picData);
            photoFile = null;

        }
    }

}
