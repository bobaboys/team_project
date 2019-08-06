package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.activities.HelperDetailsActivity;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Constants;
import com.example.mentalhealthapp.models.UserWithTags;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import Utils.Utils;

public class HelperBiosAdapter extends RecyclerView.Adapter<HelperBiosAdapter.ViewHolder> {

    public final String HELPER_BIO_KEY = "helperBio";
    public final String USERNAME_KEY = "username";

    private Context context;
    private List<UserWithTags> bios;

    public HelperBiosAdapter(Context context, List<UserWithTags> bios) {
        this.context = context;
        //TODO FEED WITH TAGS.
        this.bios = bios;
    }


    // Add a list of items -- change to type used
    public void addAll(List<UserWithTags> list) {
        bios.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_helper_bios, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        UserWithTags bio = bios.get(i);
        viewHolder.bind(bio);
    }

    @Override
    public int getItemCount() {
        return bios.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvBio, tvUsername;
        private ImageView helperPic;

        public ViewHolder(View view) {
            super(view);
            tvUsername =itemView.findViewById(R.id.tvBio_username);
            tvBio = itemView.findViewById(R.id.tvBio_HelperBios);
            helperPic = itemView.findViewById(R.id.ivHelperPic_HelperBios);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position!=RecyclerView.NO_POSITION){
                ParseUser helper = bios.get(position).getUser();
                Intent intent = new Intent(context, HelperDetailsActivity.class);
                intent.putExtra("clicked_bio", helper);
                context.startActivity(intent);
            }
        }

        public void bind(final UserWithTags bio){
            if(bio.getUser().getString(HELPER_BIO_KEY)!=null&&bio.getUser().getBoolean("helper")){
                tvUsername.setText(bio.getUser().getString(USERNAME_KEY));
                tvBio.setText(bio.getUser().getString(HELPER_BIO_KEY));
                ParseFile avatarFile = bio.getUser().getParseFile(Constants.AVATAR_FIELD);
                Bitmap bm = Utils.convertFileToBitmap(avatarFile);
                helperPic.setImageBitmap(bm);
            }
        }
    }
}

