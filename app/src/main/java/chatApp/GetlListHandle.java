package chatApp;


import java.util.List;

public interface GetlListHandle {
    void onSuccess(String TAG, List<?> list);
    void onFailure(String TAG,Exception e);
}
