package chatApp;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.User;

import java.util.List;

public interface GeneralHandle {

    void onSuccess(String TAG);
    void onFailure(String TAG,Exception e);
}
