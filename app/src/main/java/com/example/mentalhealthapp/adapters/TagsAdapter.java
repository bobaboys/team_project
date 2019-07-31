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

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.Fragments.TagDetailsFragment;
import com.example.mentalhealthapp.models.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> implements Filterable {
    public ArrayList<Tag> selectedTags;
    public final String TAG_TABLE_FIELD = "Tag";
    private Context context;
    protected List<Tag> tags;
    protected List<Tag> tagsFull;

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

    public void setTagsListFull(){
        tagsFull = new ArrayList<>(tags); //TODO: CHANGE THIS
    }

    @Override
    public Filter getFilter() {
        return tagFilter;
    }

    private Filter tagFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Tag> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(tagsFull);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Tag item: tagsFull){
                    if(item.getString(TAG_TABLE_FIELD).toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tags.clear();
            tags.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox tagName;
        CardView cardTag;
        ImageView description;

        public ViewHolder(final View view) {
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

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment resultFragment = new TagDetailsFragment();
                    //passing to result of list of helpers
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("tag",selectedTag);
                    resultFragment.setArguments(bundle);

                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.flContainer_main, resultFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
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

