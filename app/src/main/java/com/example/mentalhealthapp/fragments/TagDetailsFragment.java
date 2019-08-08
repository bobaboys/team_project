package com.example.mentalhealthapp.Fragments;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mentalhealthapp.R;
import com.example.mentalhealthapp.activities.MainActivity;
import com.example.mentalhealthapp.models.Tag;

import org.parceler.Parcels;


public class TagDetailsFragment extends Fragment {
    Tag tag;
    TextView title, description, link;
    ImageView back;

    public View.OnClickListener onBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).setCurrentFragment( new RecieverSearchPageFragment());
        }
    };


    public TagDetailsFragment() {// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        Bundle bundle = getArguments();
        back = view.findViewById(R.id.iv_back_main_btn);
        tag = (Tag) bundle.getParcelable("tag");
        title = view.findViewById(R.id.tv_tag_title);
        description = view.findViewById(R.id.tv_tag_details_content);
        link = view.findViewById(R.id.tv_tag_link);

        back.setVisibility(ConstraintLayout.VISIBLE);
        back.setOnClickListener(onBack);
        title.setText(tag.getString("Tag"));
        description.setText(tag.getString("Description"));
        link.setText(tag.getString("Link"));


    }
}
