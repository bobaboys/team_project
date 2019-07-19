package chatApp;

import com.sendbird.android.User;

public interface ConnectionHandle {
    void onSuccess(String TAG, User user); //Connected to server.
    void onFailure(String TAG,Exception e);//send text message
}
