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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    protected EditText editHelperBio;
    protected Button saveChanges;
    protected Button takePic;
    protected Button choosePic;
    protected ImageView avatarPic;
    protected RecyclerView rvTags;
    public final String TAG = "Helper Profile Edit:";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    protected String AVATAR_FIELD = "avatar";
    protected File photoFile;
    public static final int CHOOSE_AVATAR_REQUEST = 333;
    protected  List<Tag> tags;
    protected TagsAdapter tagsAdapter;

    protected View.OnClickListener saveChangesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ParseUser user = ParseUser.getCurrentUser();
            //update parse server user with avatar photo
            if(photoFile!=null) {
                editPhoto(user);
            }
            editBio(user);
            editTags(user);
            switchFragments();
        }
    };

    protected View.OnTouchListener  touchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event){
            rvTags.setVisibility(ConstraintLayout.GONE);
            return false;
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
        setAndPopulateRvTags();
        getAllTags();
        ((MainActivity)getContext()).currentCentralFragment = this;
    }

    private void assignViewsAndListeners(View view) {
        rvTags = view.findViewById(R.id.rvTags_HelperEditProfile);
        editHelperBio = view.findViewById(R.id.et_EditBio_HelperEditProfile);
        editHelperBio.setText(ParseUser.getCurrentUser().getString(Constants.HELPER_BIO_FIELD));
        editHelperBio.setOnTouchListener(touchListener);
        saveChanges = view.findViewById(R.id.btnSaveChange_Reciever_edit_profile);
        saveChanges.setOnClickListener(saveChangesListener);
        avatarPic = view.findViewById(R.id.ivAvatarPic_helpereditprofile);
        ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(Constants.AVATAR_FIELD);
        Bitmap bm = Utils.convertFileToBitmap(avatarFile);
        avatarPic.setImageBitmap(bm);
        takePic = view.findViewById(R.id.btnTakePic_helpereditprofile);
        takePic.setOnClickListener(takePicListener);
        choosePic = view.findViewById(R.id.btnChoosePic_helpereditprofile);
        choosePic.setOnClickListener(choosePicListener);

    }

    public void editBio(ParseUser user){
        user.put(Constants.HELPER_BIO_FIELD, editHelperBio.getText().toString());
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

    public void editTags(ParseUser user){
        //save all tags on server for parse user
        for(int i = 0; i < tagsAdapter.selectedTags.size(); i++){
            HelperTags helperTags = new HelperTags();
            helperTags.setHelperTags(user, tagsAdapter.selectedTags.get(i));
            helperTags.saveInBackground(new SaveCallback() {
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
            ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile(AVATAR_FIELD);
            Bitmap bm = Utils.convertFileToBitmap(avatarFile);
            avatarPic.setImageBitmap(bm);
        }
    }

    private void setAndPopulateRvTags() {
        tags = new ArrayList<>();
        tagsAdapter = new TagsAdapter(getContext(), tags);
        rvTags.setAdapter(tagsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTags.setLayoutManager(layoutManager);
    }

    private void getAllTags() {
        ParseQuery<Tag> postsQuery = new ParseQuery<Tag>(Tag.class);
        postsQuery.setLimit(50);
        /*We decided load all tags (and on code select which ones match with the search FOR LATER)
         * we are concern that it could be lots of information on the database and we would need to set
         * a limit of rows. In this case our tags are 50 tops. */
        postsQuery.findInBackground(new FindCallback<Tag>() {
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
        });
    }
    private void setRvWithCurrentUserTags(){


    }

    public RecyclerView getRvTags() {
        return rvTags;
    }

    public void setRvTags(RecyclerView rvTags) {
        this.rvTags = rvTags;
    }
}
