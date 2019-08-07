package com.example.mentalhealthapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mentalhealthapp.adapters.AvatarImagesAdapter;
import com.example.mentalhealthapp.R;

import java.util.ArrayList;
import java.util.List;

public class AvatarImagesActivity extends AppCompatActivity {

    private RecyclerView rvAvatarImages;
    private List<Integer> avatarPicsList;
    private AvatarImagesAdapter avAdapter;

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
        //add to constants, use hashmap
        List<Integer> avatarPics = new ArrayList<>();
        avatarPics.add(R.drawable.icons8_female_user_100);
        avatarPics.add(R.drawable.icons8_user_female_skintype1_2_100);
        avatarPics.add(R.drawable.icons8_user_female_skintype3_100);
        avatarPics.add(R.drawable.icons8_user_female_skintype4_100);
        avatarPics.add(R.drawable.icons8_user_female_skintype5_100);
        avatarPics.add(R.drawable.icons8_user_female_skintype7_100);
        avatarPics.add(R.drawable.icons8_user_male_100);
        avatarPics.add(R.drawable.icons8_cartoon_boy_100);
        avatarPics.add(R.drawable.icons8_user_male_skintype1_2_100);
        avatarPics.add(R.drawable.icons8_user_male_skintype3_100);
        avatarPics.add(R.drawable.icons8_user_male_skintype4_100);
        avatarPics.add(R.drawable.icons8_user_male_skintype5_100);
        avatarPics.add(R.drawable.icons8_user_male_skintype6_100);
        avatarPics.add(R.drawable.icons8_user_male_skintype7_100);
        avatarPics.add(R.drawable.icons8_avocado_100);
        avatarPics.add(R.drawable.icons8_bear_100);
        avatarPics.add(R.drawable.icons8_beaver_100);
        avatarPics.add(R.drawable.icons8_cactus_100);
        avatarPics.add(R.drawable.icons8_carrot_100);
        avatarPics.add(R.drawable.icons8_cherry_100);
        avatarPics.add(R.drawable.icons8_corgi_100);
        avatarPics.add(R.drawable.icons8_corn_100);
        avatarPics.add(R.drawable.icons8_cute_hamster_100);
        avatarPics.add(R.drawable.icons8_dolphin_100);
        avatarPics.add(R.drawable.icons8_elephant_100);
        avatarPics.add(R.drawable.icons8_flamingo_100);
        avatarPics.add(R.drawable.icons8_german_shepherd_100);
        avatarPics.add(R.drawable.icons8_giraffe_100);
        avatarPics.add(R.drawable.icons8_hornet_100);
        avatarPics.add(R.drawable.icons8_kangaroo_100);
        avatarPics.add(R.drawable.icons8_ladybird_100);
        avatarPics.add(R.drawable.icons8_machaon_butterfly_100);
        avatarPics.add(R.drawable.icons8_maple_leaf_100);
        avatarPics.add(R.drawable.icons8_morty_smith_100);
        avatarPics.add(R.drawable.icons8_orange_100);
        avatarPics.add(R.drawable.icons8_panda_100);
        avatarPics.add(R.drawable.icons8_peacock_100);
        avatarPics.add(R.drawable.icons8_pig_with_lipstick_100);
        avatarPics.add(R.drawable.icons8_princess_100);
        avatarPics.add(R.drawable.icons8_rhinoceros_100);
        avatarPics.add(R.drawable.icons8_rick_sanchez_100);
        avatarPics.add(R.drawable.icons8_rose_100);
        avatarPics.add(R.drawable.icons8_seahorse_100);
        avatarPics.add(R.drawable.icons8_sloth_100);
        avatarPics.add(R.drawable.icons8_snail_100);
        avatarPics.add(R.drawable.icons8_spring_100);
        avatarPics.add(R.drawable.icons8_starfish_100);
        avatarPics.add(R.drawable.icons8_turtle_100);
        avatarPics.add(R.drawable.icons8_unicorn_100);
        avatarPics.add(R.drawable.icons8_watermelon_100);
        avatarPics.add(R.drawable.icons8_wolf_100);
        avatarPics.add(R.drawable.icons8_zebra_100);
        return avatarPics;
    }
}
