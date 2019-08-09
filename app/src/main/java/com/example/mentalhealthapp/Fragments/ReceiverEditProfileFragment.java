package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.AvatarImagesActivity;
import com.example.mentalhealthapp.activities.MainActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import Utils.Utils;

import static android.app.Activity.RESULT_OK;

public class ReceiverEditProfileFragment extends Fragment {
    private  Button saveChanges;
    private  Button takePic;
    private  Button choosePic;
    private  ImageView avatarPic;
    private  final String TAG = "Receiver Profile Edit:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private  String photoFileName = "photo.jpg";
    private  String AVATAR_FIELD = "avatar";
    private  File photoFile; //TODO THIS IS FILE WHICH GOES TO SERVER.
    public static final int CHOOSE_AVATAR_REQUEST = 333;
    private  MediaPlayer buttonClickSound;
    ImageView back;


    private View.OnClickListener onBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).setCurrentFragment(new ReceiverProfileFragment());
        }
    };


    protected View.OnClickListener saveChangesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            saveChanges.startAnimation(animation);
            if(photoFile!=null) editPhoto(ParseUser.getCurrentUser());
            ((MainActivity)getActivity()).setCurrentFragment(new ReceiverProfileFragment());
        }
    };


    protected View.OnClickListener takePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            takePic.startAnimation(animation);
            onLaunchCamera();
        }
    };


    protected View.OnClickListener choosePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            choosePic.startAnimation(animation);
            startActivityForResult(new Intent(getContext(), AvatarImagesActivity.class),
                    CHOOSE_AVATAR_REQUEST);
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_reciever_edit_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonClickSound = MediaPlayer.create(getContext(),
                R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        setViewComponents(view);
        setListeners();
        Utils.setProfileImage(avatarPic);
    }


    private void setViewComponents(View view){
        saveChanges = view.findViewById(R.id.btnSaveChange_reciever_edit_profile);
        takePic = view.findViewById(R.id.btnTakePic_reciever_edit_profiile);
        avatarPic = view.findViewById(R.id.ivAvatar_reciever_edit_profile);
        choosePic = view.findViewById(R.id.btnChoosePic_reciever_edit_profile);
        back = view.findViewById(R.id.iv_back_main_btn);
        back.setVisibility(ConstraintLayout.VISIBLE);
    }


    private void setListeners(){
        saveChanges.setOnClickListener(saveChangesListener);
        takePic.setOnClickListener(takePicListener);
        choosePic.setOnClickListener(choosePicListener);
        back.setOnClickListener(onBack);
    }


    private void editPhoto(ParseUser user) {
        user.put(AVATAR_FIELD, new ParseFile(photoFile));
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
                Bitmap takenImage = Utils.rotateBitmapOrientation(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                avatarPic.setImageBitmap(takenImage);
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    takenImage.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == CHOOSE_AVATAR_REQUEST && resultCode == RESULT_OK){
            //get pic from parse user and set image view
            ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(AVATAR_FIELD);
            Bitmap bm = Utils.convertFileToBitmap(avatarFile);
            avatarPic.setImageBitmap(bm);
        }
    }


}
