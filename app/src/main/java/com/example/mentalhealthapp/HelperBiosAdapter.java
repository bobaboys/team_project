package com.example.mentalhealthapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

public class HelperBiosAdapter extends RecyclerView.Adapter<HelperBiosAdapter.ViewHolder> {

    public final String HELPER_BIO_FIELD = "helperBio";
    private Context context;
    private List<ParseUser> bios;
    RecyclerView rvPosts;

    public HelperBiosAdapter(Context context, List<ParseUser> bios) {
        this.context = context;
        this.bios = bios;
    }

    public void clear() {
        bios.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ParseUser> list) {
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
        ParseUser bio = bios.get(i);
        viewHolder.bind(bio);
    }

    @Override
    public int getItemCount() {
        return bios.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvBio;
        ImageView helperPic;

        public ViewHolder(View view) {
            super(view);
            tvBio = itemView.findViewById(R.id.tvBio_HelperBios);
            helperPic = itemView.findViewById(R.id.ivHelperPic_HelperBios);
            rvPosts = itemView.findViewById(R.id.rvHelperBios);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position!=RecyclerView.NO_POSITION){
                ParseUser bio = bios.get(position);
                Intent intent = new Intent(context, HelperDetails.class);
//                intent.putExtra("clicked_bio", Parcels.wrap(bio));
                context.startActivity(intent);
            }
        }

        public void bind(final ParseUser bio){
            if(bio.getString(HELPER_BIO_FIELD)!=null&&bio.getBoolean("helper")){
                tvBio.setText(bio.getString(HELPER_BIO_FIELD));
            }

// USE FOR IMAGE LATER
//                ParseFile image = post.getImage();
//                if(image!= null){
//                    Glide.with(context).load(image.getUrl()).into(ivImage);
//                }

        }
    }
}

