package com.example.mentalhealthapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;

import java.util.ArrayList;

public class SelectedTagsAdapter extends BaseAdapter {
    private Context context;
    private final ArrayList<String> textViewValues;

    public SelectedTagsAdapter(Context context, ArrayList<String> textViewValues) {
        this.context = context;
        this.textViewValues = textViewValues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.item_tag_gridview, null);

            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.tvGridViewTag);
            textView.setText(textViewValues.get(position));
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }

    @Override
    public int getCount() {
        return textViewValues.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}