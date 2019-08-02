package com.example.mentalhealthapp.models;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public final class Constants {

    //PARSE KEYS:
    //Classes:
    public static final String HELPER_TAGS_PARSE_OBJECT = "HelperTags";
    public static final String USER_PARSE_CLASS = "User";
    public static final String SEARCH_OPTIONS_CLASS = "SearchOptions";

    //Fields:
    public static final String USER_FIELD = "user";
    public static final String USERNAME_FIELD = "username";
    public static final String NAME_FIELD = "name";
    public static final String HELPER_BIO_FIELD = "helperBio";
    public static final String HELPER_FIELD = "helper";
    public static final String AVATAR_FIELD = "avatar";
    public static final String TAG_FIELD = "Tag";
    public static final String AUDIO_RECORD_FAIL_TAG= "AUDIO_RECORDING";
    public static final String PROGRESS_BAR_DOWNLOAD="PROGRESS_BAR_DOWNLOAD";
    public static final String AUDIO_PLAY_FAIL_TAG="AUDIO_PLAY_FAIL_TAG";
    public static final String CHAT_HELPER_DELETED = "helperDeleted";
    public static final String CHAT_RECEIVER_DELETED = "receiverDeleted";
    public static final String OBJECT_ID = "objectId";

    //REQUEST CODES:
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final int CHOOSE_AVATAR_REQUEST = 333;
    public static final int WRITE_REQUEST_CODE = 300;

    //OTHER:
    public static final String TAG = "tag: ";
    public static final String CLICKED_AVATAR_KEY = "clicked_avatar";
    private ParseUser onCallHelper;

}
