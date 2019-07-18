package chatApp;

public interface GetStringHandle {
    void onSuccess(String TAG, String channelUrl); // Getting channel url.
    void onFailure(String TAG,Exception e);
}
