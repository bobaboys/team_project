package com.example.mentalhealthapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mentalhealthapp.Fragments.HelperProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class AvatarImagesAdapter extends RecyclerView.Adapter<AvatarImagesAdapter.ViewHolder> {


    Context mContext;
    List<Integer> avatarImages = new ArrayList<>();
    RecyclerView rvAvatarPics;

    public AvatarImagesAdapter(Activity context, List<Integer> avatarPics){
        mContext = context;
        avatarImages = avatarPics;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_avatar_pic, viewGroup, false);
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
        Glide.with(mContext).load(avatar).asBitmap().centerCrop().into(target);
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
            rootView = itemView;
            avatarPic = itemView.findViewById(R.id.ivAvatarPic_itemAvatarPic);
            rvAvatarPics = itemView.findViewById(R.id.rvAvatarImages_AvatarImages);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Integer avatar = avatarImages.get(position);
                Intent intent = new Intent(mContext, HelperProfileFragment.class);
                //TODO: WRAP IMAGE AND SEND IT
                mContext.startActivity(intent);
            }
        }

    }

}

