package com.example.mentalhealthapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mentalhealthapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AvatarImagesAdapter extends RecyclerView.Adapter<AvatarImagesAdapter.ViewHolder> {


    Activity mActivity;
    List<Integer> avatarImages = new ArrayList<>();
    RecyclerView rvAvatarPics;
    String CLICKED_AVATAR_KEY = "clicked_avatar";
    ParseUser currUser;
    String AVATAR_FIELD = "avatar";
    public final String TAG = "Helper Profile Edit:";


    public AvatarImagesAdapter(Activity context, List<Integer> avatarPics){
        mActivity = context;
        avatarImages = avatarPics;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_avatar_pic, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Integer avatar = avatarImages.get(i);
        viewHolder.rootView.setTag(avatar);

        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                viewHolder.avatarPic.setImageBitmap(resource);
            }
        };

        viewHolder.avatarPic.setTag(target);
        Glide.with(mActivity).load(avatar).asBitmap().centerCrop().into(target);
    }

    @Override
    public int getItemCount() {
        return avatarImages.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView avatarPic;
        View rootView;

        public ViewHolder(View view) {
            super(view);
            rootView = view;
            avatarPic = view.findViewById(R.id.ivAvatarPic_itemAvatarPic);
            rvAvatarPics = view.findViewById(R.id.rvAvatarImages_AvatarImages);
            avatarPic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mActivity, "clicked!", Toast.LENGTH_LONG).show();
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Integer avatar = avatarImages.get(position);

                //for editing: saving avatar image to parse server under current user avatar image file field
                if(ParseUser.getCurrentUser()!=null) {
                    Bitmap avatarBitmap = (Bitmap) BitmapFactory.decodeResource(mActivity.getResources(), avatar);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    avatarBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] picData = stream.toByteArray();

                    ParseFile imageFile = new ParseFile("image.png", picData);
                    currUser.put(AVATAR_FIELD, imageFile);
                    currUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d(TAG, "Error while saving");
                                e.printStackTrace();
                                return;
                            }
                        }
                    });
                }
                //for sign up: send avatar id back to sign up intent
                else{
                    Intent resultData = new Intent();
                    resultData.putExtra(CLICKED_AVATAR_KEY,avatar);
                    mActivity.setResult(Activity.RESULT_OK, resultData);
                    mActivity.finish();
                }

            }
        }

    }

}

