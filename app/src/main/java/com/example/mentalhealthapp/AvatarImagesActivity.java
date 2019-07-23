package com.example.mentalhealthapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AvatarImagesActivity extends AppCompatActivity {

    RecyclerView rvAvatarImages;
    List<Integer> avatarPicsList;
    AvatarImagesAdapter avAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_images);
        rvAvatarImages = findViewById(R.id.rvAvatarImages_AvatarImages);
        rvAvatarImages.setHasFixedSize(true);
        final GridLayoutManager layout = new GridLayoutManager(AvatarImagesActivity.this,3);
        rvAvatarImages.setLayoutManager(layout);

        avatarPicsList = getAvatarImages();
        avAdapter = new AvatarImagesAdapter(AvatarImagesActivity.this, avatarPicsList);
        rvAvatarImages.setAdapter(avAdapter);

    }

    private List<Integer> getAvatarImages() {
        List<Integer> avatarPics = new ArrayList<>();
        avatarPics.add(R.drawable.icons8_avocado_100);
        avatarPics.add(R.drawable.icons8_bear_100);
        avatarPics.add(R.drawable.icons8_beaver_100);
        avatarPics.add(R.drawable.icons8_cactus_100);
        avatarPics.add(R.drawable.icons8_carrot_100);
        avatarPics.add(R.drawable.icons8_cartoon_boy_100);
        avatarPics.add(R.drawable.icons8_cherry_100);
        avatarPics.add(R.drawable.icons8_corgi_100);
        avatarPics.add(R.drawable.icons8_corn_100);
        avatarPics.add(R.drawable.icons8_cute_hamster_100);
        avatarPics.add(R.drawable.icons8_dolphin_100);
        avatarPics.add(R.drawable.icons8_elephant_100);
        avatarPics.add(R.drawable.icons8_female_user_100);
        avatarPics.add(R.drawable.icons8_flamingo_100);
        return avatarPics;
    }
}
