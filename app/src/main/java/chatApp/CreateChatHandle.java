package chatApp;

import com.sendbird.android.GroupChannel;

public interface CreateChatHandle {
    void onSuccess(String TAG, GroupChannel groupChannel); // Created new chat

    void onFailure(String TAG,Exception e);
}
