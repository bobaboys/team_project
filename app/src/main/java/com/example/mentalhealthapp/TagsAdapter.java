package com.example.mentalhealthapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.mentalhealthapp.Fragments.HelperBiosFragment;
import com.example.mentalhealthapp.model.Tag;
import com.example.mentalhealthapp.model.TagsParcel;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    public ArrayList<Tag> selectedTags;
    public final String TAG_TABLE_FIELD = "Tag";
    private Context context;
    private List<Tag> tags;


    public TagsAdapter(Context context, List<Tag> tags) {
        this.context = context;
        this.tags = tags;
        selectedTags = new ArrayList<>();
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

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox tagName;
        CardView cardTag;
        ImageView description;

        public ViewHolder(View view) {
            super(view);
            tagName = itemView.findViewById(R.id.cb_tag_select);
            cardTag = itemView.findViewById(R.id.card_tag);
            description = itemView.findViewById(R.id.ic_tag_description);

            tagName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Tag selectedTag = getSelectedTag();
                    if(isChecked){
                            selectedTags.add(selectedTag);
                    }else{
                            selectedTags.remove(selectedTag);
                    }
                }
            });
            // Listener of Tag details icon. opens a new fragment,
            // with the tag name, description and link with more info
            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tag selectedTag = getSelectedTag();


                    Fragment resultFragment = new TagDetailsFragment();
                    ((MainActivity)context).replaceFragment(resultFragment);

                    //passing to result of list of helpers
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("tag",selectedTag);
                    resultFragment.setArguments(bundle);

                }
            });
        }


        public void bind(final Tag tag){
            if(tag.getString(TAG_TABLE_FIELD)!=null){

                tagName.setText(tag.getString(TAG_TABLE_FIELD));
                //int random = Random.next SET A RANDOM COLOR FROM PALETE
                //cardTag.setCardBackgroundColor(R.color.);
                //TODO

            }
        }

        public Tag getSelectedTag(){
            int i = getAdapterPosition();
            return tags.get(i);
        }

    }
}

