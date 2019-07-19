package com.example.mentalhealthapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.mentalhealthapp.model.Tag;
import com.parse.ParseUser;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    public final String TAG_TABLE_FIELD = "Tag";
    private Context context;
    private List<Tag> tags;
    RecyclerView rvTags;

    public TagsAdapter(Context context, List<Tag> tags) {
        this.context = context;
        this.tags = tags;
    }

    public void clear() {
        tags.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
        public void addAll(List<Tag> list) {
        tags.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag_search, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Tag tag = tags.get(i);
        viewHolder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CheckBox tagName;

        public ViewHolder(View view) {
            super(view);
            tagName = itemView.findViewById(R.id.cb_tag_select);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position!=RecyclerView.NO_POSITION){
                //TODO ADD/REMOVE TO LIST OF SELECTED TAGS
            }
        }

        public void bind(final Tag tag){
            if(tag.getString(TAG_TABLE_FIELD)!=null){
                tagName.setText(tag.getString(TAG_TABLE_FIELD));
            }
        }
    }
}

