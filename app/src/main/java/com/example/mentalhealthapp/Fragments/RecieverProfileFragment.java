package com.example.mentalhealthapp.Fragments;

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
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mentalhealthapp.activities.LoginActivity;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

import Utils.Utils;

import static android.app.Activity.RESULT_OK;

public class RecieverProfileFragment extends Fragment {

    protected TextView testChat;
    protected Button btnLogOut;
    protected ImageView recProfileAvatar;
    protected TextView recProfileTags;
    protected TextView recProfileChats;
    protected ParseUser currentUser;
    protected Button btnTakePic;
    protected Button btnChoosePic;
    public final String TAG = "Reciever Profiile:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    String AVATAR_FIELD = "avatar";

    protected View.OnClickListener logOutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    };
    protected View.OnClickListener takePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onLaunchCamera();
            //update parse server user with avatar photo
            ParseFile parseFile = new ParseFile(photoFile);
            ParseUser current = ParseUser.getCurrentUser();
            current.put(AVATAR_FIELD, parseFile);

        }
    };

    protected View.OnClickListener choosePicListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: fill in 
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reciever_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testChat = view.findViewById(R.id.recprofileTest);
        recProfileAvatar = view.findViewById(R.id.ivAvatar_reciever_profile);
        ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(Constants.AVATAR_FIELD);
        Bitmap bm = Utils.convertFileToBitmap(avatarFile);
        recProfileAvatar.setImageBitmap(bm);
        recProfileTags = view.findViewById(R.id.tvMyTags_reciever_profile);
        recProfileChats = view.findViewById(R.id.tvMyChats_reciever_profile);
        currentUser = ParseUser.getCurrentUser();
        btnLogOut = view.findViewById(R.id.btnLogout_ProfileRec);
        btnLogOut.setOnClickListener(logOutBtnListener);
        btnTakePic = view.findViewById(R.id.btnTakePic_receiverprofile);
        btnTakePic.setOnClickListener(takePicListener);
        btnChoosePic = view.findViewById(R.id.btnChoosePhoto_recieverprofile);
        btnChoosePic.setOnClickListener(choosePicListener);
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
                recProfileAvatar.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
