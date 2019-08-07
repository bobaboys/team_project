package com.example.mentalhealthapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.AvatarImagesActivity;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.adapters.TagsAdapter;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.HelperTags;
import com.example.mentalhealthapp.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Utils.Utils;

import static android.app.Activity.RESULT_OK;

public class HelperEditProfileFragment extends Fragment {

    private  EditText editHelperBio;
    private  Button saveChanges;
    private  Button takePic;
    private  Button choosePic;
    private  Button editTags;
    private  ImageView avatarPic;
    private  RecyclerView rvTags;
    private  final String TAG = "Helper Profile Edit:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private  String photoFileName = "photo.jpg";
    private  String AVATAR_FIELD = "avatar";
    private  File photoFile;
    public static final int CHOOSE_AVATAR_REQUEST = 333;
    private   List<Tag> tags;
    private  TagsAdapter tagsAdapter;
    private  MediaPlayer buttonClickSound;
    private List<String> lastTagsSelectedString;
    private ImageView back;


    private View.OnClickListener onBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).setCurrentFragment(new HelperProfileFragment());
        }
    };


    protected View.OnClickListener saveChangesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            saveChanges.startAnimation(animation);
            //update parse server user with avatar photo
            if(photoFile!=null) {
                editPhoto(ParseUser.getCurrentUser());
            }
            editBio();
            queryTagsOfUser(updateOnServerTags);
        }
    };


    private FindCallback updateOnServerTags =  new FindCallback<HelperTags>() {
        @Override
        public void done(List<HelperTags> objects, ParseException e) {
            if(e==null){
                for(HelperTags object : objects){
                    object.deleteInBackground();
                }
                writeNewTags();
                ((MainActivity)getActivity()).setCurrentFragment(new HelperEditProfileFragment());
            }else{
                Log.e("HelperProfileFragment", "failure in clearing tags");
            }
        }
    };

    protected View.OnClickListener editTagsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickSound.start();
            final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
            editTags.startAnimation(animation);
            rvTags.setVisibility(ConstraintLayout.VISIBLE);
            editTags.setVisibility(ConstraintLayout.GONE);
        }
    };


    protected View.OnTouchListener touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event){
            rvTags.setVisibility(ConstraintLayout.GONE);
            editTags.setVisibility(ConstraintLayout.VISIBLE);
            return false;
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
            Intent intent = new Intent(getContext(), AvatarImagesActivity.class);
            startActivityForResult(intent, CHOOSE_AVATAR_REQUEST);
        }
    };


    private FindCallback<Tag> addTagsToAdapterCallback = new FindCallback<Tag>() {
        @Override
        public void done(List<Tag> objects, ParseException e) {
            if (e != null) {
                Log.e(TAG, "Error loading tags from parse server");
                e.printStackTrace();
                return;
            }
            tags.addAll(objects);
            tagsAdapter.notifyDataSetChanged();
        }
    };


    private SaveCallback saveCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if(e!=null){
                Log.d(TAG, "Error while saving");
                e.printStackTrace();
                return;
            }
        }
    };

    private FindCallback<HelperTags> obtainTagsOfHelperCallback =  new FindCallback<HelperTags>() {
        @Override
        public void done(List<HelperTags> objects, ParseException e) {
            lastTagsSelectedString = new ArrayList<>();
            for(HelperTags ht : objects){
                Tag t=(Tag)ht.getParseObject(HelperTags.TAG_KEY);
                String x= t.getText();
                lastTagsSelectedString.add(x);
            }
            tagsAdapter.setLastSelectedTags(lastTagsSelectedString);
            queryAllTags(addTagsToAdapterCallback);
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
        buttonClickSound = MediaPlayer.create(getContext(), R.raw.zapsplat_multimedia_game_designed_bubble_pop_034_26300);
        assignViewsAndListeners(view);
        setAndPopulateRvTags();
        queryTagsOfUser(obtainTagsOfHelperCallback);
    }


    private void assignViewsAndListeners(View view) {
        setViewComponents(view);
        setListeners();
        editHelperBio.setText(ParseUser.getCurrentUser().getString(Constants.HELPER_BIO_FIELD));
        Utils.setProfileImage(avatarPic);
    }


    private void setViewComponents(View view){
        rvTags = view.findViewById(R.id.rvTags_HelperEditProfile);
        editHelperBio = view.findViewById(R.id.et_EditBio_HelperEditProfile);
        editTags = view.findViewById(R.id.edit_tags);
        saveChanges = view.findViewById(R.id.btnSaveChange_Reciever_edit_profile);
        avatarPic = view.findViewById(R.id.ivAvatarPic_helpereditprofile);
        choosePic = view.findViewById(R.id.btnChoosePic_helpereditprofile);
        takePic = view.findViewById(R.id.btnTakePic_helpereditprofile);
        back = view.findViewById(R.id.iv_back_main_btn);
        back.setVisibility(ConstraintLayout.VISIBLE);
    }


    private void setListeners(){
        editHelperBio.setOnTouchListener(touchListener);
        editTags.setOnClickListener(editTagsListener);
        saveChanges.setOnClickListener(saveChangesListener);
        takePic.setOnClickListener(takePicListener);
        choosePic.setOnClickListener(choosePicListener);
        back.setOnClickListener(onBack);
    }


    public void editBio(){
        ParseUser.getCurrentUser().put(Constants.HELPER_BIO_FIELD, editHelperBio.getText().toString());
        ParseUser.getCurrentUser().saveInBackground(saveCallback);
    }


    private void editPhoto(ParseUser user) {
        ParseFile parseFile = new ParseFile(photoFile);
        user.put(AVATAR_FIELD, parseFile);
        user.saveInBackground(saveCallback);
    }


    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.mentalhealth", photoFile);
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
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
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
            ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(AVATAR_FIELD);
            Bitmap bm = Utils.convertFileToBitmap(avatarFile);
            avatarPic.setImageBitmap(bm);
        }
    }


    private void setAndPopulateRvTags() {
        tags = new ArrayList<>();
        tagsAdapter = new TagsAdapter(getContext(), tags);
        rvTags.setAdapter(tagsAdapter);
        rvTags.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void queryAllTags(FindCallback<Tag> findCallback) {
        ParseQuery<Tag> postsQuery = new ParseQuery<Tag>(Tag.class);
        postsQuery.findInBackground(findCallback);
    }


    private void queryTagsOfUser(FindCallback<HelperTags> findCallback){
        ParseQuery<HelperTags> q = new ParseQuery<>(HelperTags.class);
        q.include(HelperTags.TAG_KEY);
        q.whereEqualTo(HelperTags.USER_KEY, ParseUser.getCurrentUser());
        q.findInBackground(findCallback);

    }


    public void writeNewTags(){
        //save all tags on server for parse user
        for(int i = 0; i < tagsAdapter.getSelectedTags().size(); i++){
            HelperTags helperTags = new HelperTags();
            helperTags.setHelperTags(ParseUser.getCurrentUser(), tagsAdapter.getSelectedTags().get(i));
            helperTags.saveInBackground(saveCallback);
        }
    }
}
