package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.example.mentalhealthapp.Fragments.HelperEditProfileFragment;
import com.example.mentalhealthapp.Fragments.ReceiverEditProfileFragment;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.HelperSignUpTagsActivity;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.Fragments.TagDetailsFragment;
import com.example.mentalhealthapp.models.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Utils.Utils;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> implements Filterable {
    private  ArrayList<Tag> selectedTags;
    private  final String TAG_TABLE_FIELD = "Tag";
    private Context context;
    private  List<Tag> tags;
    private  List<Tag> tagsFull;


    private List<String> lastSelectedTags;

    public TagsAdapter(Context context, List<Tag> tags) {
        this.context = context;
        this.tags = tags;
        selectedTags = new ArrayList<>();
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
    public int getItemCount() { return tags.size(); }

    public void setTagsListFull(){
        tagsFull = new ArrayList<>(tags); //TODO: CHANGE THIS
    }

    @Override
    public Filter getFilter() { return tagFilter; }


    private Filter tagFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Tag> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0)
                filteredList.addAll(tagsFull);
            else
                filterTags( constraint, filteredList);
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tags.clear();
            if((List)results.values!=null){
                tags.addAll((List)results.values);
            }
            notifyDataSetChanged();
        }

    };

    private void filterTags(CharSequence constraint, List<Tag> filteredList){
        String filterPattern = constraint.toString().toLowerCase().trim();
        for(Tag item: tagsFull){
            if(item.getString(TAG_TABLE_FIELD).toLowerCase().contains(filterPattern))
                filteredList.add(item);
        }
    }


    public void setLastSelectedTags(List<String> lastSelectedTags) {
        this.lastSelectedTags = lastSelectedTags;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<Tag> getSelectedTags() {
        return selectedTags;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox tagName;
        private ImageView description;
        private View view;


        View.OnClickListener openDescriptionFragment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tag selectedTag = getSelectedTag();

                Fragment resultFragment = new TagDetailsFragment();
                //passing to result of list of helpers
                Bundle bundle = new Bundle();
                bundle.putParcelable("tag", selectedTag);
                resultFragment.setArguments(bundle);
                try{
                    ((MainActivity)view.getContext()).setCurrentFragment(resultFragment);
                }catch (ClassCastException e){
                    ((HelperSignUpTagsActivity)view.getContext()).replaceFragment(resultFragment);
                }

            }
        };


        CompoundButton.OnCheckedChangeListener setStateCheckBoxItem = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Tag selectedTag = getSelectedTag();
                if(isChecked){
                    selectedTags.add(selectedTag);
                }else{
                    selectedTags.remove(selectedTag);
                }
            }
        };


        public ViewHolder(final View view) {
            super(view);
            this.view=view;
            tagName = itemView.findViewById(R.id.cb_tag_select);
            description = itemView.findViewById(R.id.ic_tag_description);

            tagName.setOnCheckedChangeListener(setStateCheckBoxItem);
            description.setOnClickListener(openDescriptionFragment);
        }


        public void bind(final Tag tag){
            if(lastSelectedTags==null){
                tagName.setChecked(selectedTags.contains(getSelectedTag()));
            }else{
                tagName.setChecked(selectedTags.contains(getSelectedTag())
                        || lastSelectedTags.contains(tag.getText()));
            }
            tagName.setText(tag.getString(TAG_TABLE_FIELD));
        }


        public Tag getSelectedTag(){
            int i = getAdapterPosition();
            return tags.get(i);
        }
    }
}

