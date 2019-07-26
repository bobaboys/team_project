package com.example.mentalhealthapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
    protected File photoFile;
    public final String TAG = "Sign Up Activity:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private final String HELPER_FIELD = "helper";
    private final String AVATAR_FIELD = "avatar";
    String CLICKED_AVATAR_KEY = "clicked_avatar";

    static final int CHOOSE_AVATAR_REQUEST = 333;
    private ImageView avatarPic;
    private ImageButton takeAvatarPic;
    private Button chooseAvatar;
    private ParseFile parseFile;

    private final View.OnClickListener takeAvatarPicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onLaunchCamera();
        }
    };

    private View.OnClickListener chooseAvatarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SignUpActivity.this, AvatarImagesActivity.class);
            startActivityForResult(intent, CHOOSE_AVATAR_REQUEST);
        }
    };

    private final View.OnClickListener signUpBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            String email = emailInput.getText().toString();
            Boolean helper = radioGroup.getCheckedRadioButtonId() == R.id.radioBtnHelper_signup;
            if(photoFile!=null){
                parseFile = new ParseFile(photoFile);
            }
            signUp(username, password, email, helper, parseFile);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        AssignViewsAndListeners();
    }

    public void checkEmailValid(final String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!email.matches(emailPattern)){
            Toast.makeText(getApplicationContext(), "Email address is invalid", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(final String username, String password, final String email, final Boolean helper, final ParseFile parseFile){
        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put(HELPER_FIELD, helper);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("SignUpActivity", "Sign up successful");

                    //adding photo to parse user
                    user.put(AVATAR_FIELD, parseFile);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e!=null){
                                Log.d(TAG, "Error while saving");
                                e.printStackTrace();
                                return;
                            }
                        }
                    });

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
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                avatarPic.setImageBitmap(takenImage);
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

    public Bitmap convertFileToBitmap(ParseFile picFile){
        if(picFile == null){
            return null;
        }
        try {
            byte[] image = picFile.getData();
            if(image!=null){
                Bitmap pic = BitmapFactory.decodeByteArray(image, 0, image.length);
                return pic;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
