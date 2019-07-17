package chatApp;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.User;

import java.util.List;

public interface MasterHandle {

    void onSuccess(String TAG);
    void onSuccess(String TAG, User user); //Connected to server.
    void onSuccess(String TAG, GroupChannel groupChannel); // Created new chat
    void onSuccess(String TAG, String channelUrl); // Getting channel url.
    void onSuccess(String TAG, List<User> list); // Getting list of users

    void onFailure(String TAG,Exception e);//send text message.


    //TODO


}
