package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mentalhealthapp.Fragments.CreateEntryJournalFragment;
import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.models.Journal;

import java.util.List;

import Utils.Utils;

public class JournalAdapter  extends RecyclerView.Adapter<JournalAdapter.ViewHolder>  {

    private List<Journal> entries;
    private Context context;
    private Fragment fragment;


    public JournalAdapter(Context context, List<Journal> entries, Fragment f) {
        this.fragment =f;
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_journal_entry, viewGroup, false);
        return new JournalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Journal entry = entries.get(i);
        viewHolder.bind(entry);
    }

    @Override
    public int getItemCount() {
        if (entries == null) return 0;
        return entries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView date, content;
        private ConstraintLayout itemEntry;
        private Journal entry;

        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.tv_entry_date);
            content = view.findViewById(R.id.tv_entry_content);
            itemEntry = view.findViewById(R.id.cl_entry);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            editThisEntry();
        }


        private void editThisEntry(){
            //TODO intent to other fragment, check null cases.
            if(entry==null)return;
            Fragment fragment = new CreateEntryJournalFragment();
            Bundle bundle = new Bundle();
            bundle.putString("date", entry.getDate());
            bundle.putBoolean("alreadyExists", true);
            fragment.setArguments(bundle);
            Utils.switchToAnotherFragment(fragment,
                    JournalAdapter.this.fragment.getActivity().getSupportFragmentManager(),
                    R.id.flContainer_main);
        }

        public void bind(final Journal entry) {
            this.entry=entry;
            date.setText(entry.getDate());
            content.setText(entry.getJournalEntry());
        }
    }
}
