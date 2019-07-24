package com.example.mentalhealthapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.AvatarImagesActivity;
import com.example.mentalhealthapp.models.HelperTags;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class HelperEditProfileFragment extends Fragment {

    protected EditText editHelperBio;
    protected CheckBox red;
    protected CheckBox orange;
    protected CheckBox yellow;
    protected CheckBox green;
    protected CheckBox blue;
    protected CheckBox purple;
    protected Button saveChanges;
    protected Button takePic;
    protected Button choosePic;
    protected ImageView avatarPic;
    protected ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
    public final String TAG = "Helper Profile Edit:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    String AVATAR_FIELD = "avatar";
    File photoFile;
    String CLICKED_AVATAR_KEY = "clicked_avatar";
    static final int CHOOSE_AVATAR_REQUEST = 333;
    ParseUser currUser;

    protected View.OnClickListener saveChangesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ParseUser user = ParseUser.getCurrentUser();
            //update parse server user with avatar photo
            if(photoFile!=null) {
                editPhoto(user);
            }
            editBio(user);
            editTags(user, checkboxes);
            switchFragments();
        }
    };

    protected View.OnClickListener takePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onLaunchCamera();
        }
    };

    protected View.OnClickListener choosePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), AvatarImagesActivity.class);
            startActivityForResult(intent, CHOOSE_AVATAR_REQUEST);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_helper_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViewsAndListeners(view);
        currUser = ParseUser.getCurrentUser();
    }

    private void assignViewsAndListeners(View view) {
        editHelperBio = view.findViewById(R.id.et_EditBio_HelperEditProfile);
        red = view.findViewById(R.id.checkBox_Red_HelperEditProfile);
        orange = view.findViewById(R.id.checkBox_Orange_HelperEditProfile);
        yellow = view.findViewById(R.id.checkBox_Yellow_HelperEditProfile);
        green = view.findViewById(R.id.checkBox_Green_HelperEditProfile);
        blue = view.findViewById(R.id.checkBox_Blue_HelperEditProfile);
        purple = view.findViewById(R.id.checkBox_Purple_HelperEditProfile);
        saveChanges = view.findViewById(R.id.btn_SaveChanges_HelperEditProfile);

        checkboxes.add(red);
        checkboxes.add(orange);
        checkboxes.add(yellow);
        checkboxes.add(green);
        checkboxes.add(blue);
        checkboxes.add(purple);

        saveChanges.setOnClickListener(saveChangesListener);
        avatarPic = view.findViewById(R.id.ivAvatarPic_helpereditprofile);
        takePic = view.findViewById(R.id.btnTakePic_helpereditprofile);
        takePic.setOnClickListener(takePicListener);
        choosePic = view.findViewById(R.id.btnChoosePic_helpereditprofile);
        choosePic.setOnClickListener(choosePicListener);

    }

    public void editBio(ParseUser user){
        user.put("helperBio", editHelperBio.getText().toString());
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
    }

    public void editTags(ParseUser user, ArrayList<CheckBox> checkboxes){
        for(int i = 0; i < checkboxes.size(); i++){
            CheckBox box = checkboxes.get(i);
            if(checkboxes.get(i).isChecked()){
                CharSequence text = box.getText();
                HelperTags tags = new HelperTags();
                tags.setHelperTags(user, text.toString());
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

    private void editPhoto(ParseUser user) {
        ParseFile parseFile = new ParseFile(photoFile);
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
    }

    public void switchFragments(){
        Fragment fragment = new HelperProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                avatarPic.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == CHOOSE_AVATAR_REQUEST && resultCode == RESULT_OK){
            //get pic from parse user and set image view
            ParseFile avatarFile = currUser.getParseFile(AVATAR_FIELD);
            Bitmap bm = convertFileToBitmap(avatarFile);
            avatarPic.setImageBitmap(bm);

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
