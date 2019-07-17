package chatApp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.mentalhealthapp.R;
import com.sendbird.android.ApplicationUserListQuery;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserListQuery;
import com.sendbird.android.UserMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatApp extends Application {

    /*
     GENERAL NOTES:

     **ALL RESTRICTIONS ( WHERE CLAUSE) ON USERLISTS QUERIES IN THIS CHAT SERVER,
     ARE GONNA BE CONSTRAINED (IF NECESSARY) BY ID
     TYPE OF USER, COMPATIBILITY ( EMOTIONAL ISSUES TAGS) AND OTHER ELEMENTS GONNA BE
     CONSIDERED IN PARSE SERVER QUERIES. NOT HERE.
     PARSE SERVER SHOULD RETRIEVE LIST OF ID USERS TO USE HERE.

     */

    public static final String TAG = ChatApp.class.getSimpleName();

    private String APP_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        startChatApp(getApplicationContext());
    }

    public  void startChatApp(Context context){
        APP_ID = context.getString(R.string.APP_ID);
        SendBird.init(APP_ID, context);
        //This api is called using only our secret App ID.


    }

    public static void connectToServer(String userId, final MasterHandle handle){
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {    // Error.
                    Log.e(TAG, "Could not connect to server");
                    e.printStackTrace();
                    //This callback handle is implemented when is called this method
                    handle.onFailure(ChatAppTag.CONNECT_TO_SERVER.getTag(),e);
                    return;
                }
                //If everything goes well, this method is gonna be called as callback
                //on the anonymous class inside the method when is executed.
                handle.onSuccess(ChatAppTag.CONNECT_TO_SERVER.getTag(), user);

            }
        });
    }



    public static void createChat(String userCreator, String userHelper, String name ,boolean isEmergency, final MasterHandle handle){
        //Method called when want to create a new chat.
        List<String> userIds = new ArrayList<>();
        userIds.add(userCreator);
        userIds.add(userHelper);

        List<String> operatorIds = new ArrayList<>();
        userIds.add(userHelper);

        GroupChannelParams params = new GroupChannelParams() // we create a new channel / chat with certain characteristics
                .setPublic(false) // private conversation between helper and receiver.
                .setEphemeral(false) // keeps messages on server
                .setDistinct(!isEmergency) // unique channel between 2 users.
                                           // If is a emergency chat, we dont want keep
                                           // this conversation on the server. (For later)
                .addUserIds(userIds)
                .setOperatorUserIds(operatorIds);    // or .setOperators(List<User> operators)

        GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    Log.e(TAG, "Could not create the new chat");
                    e.printStackTrace();
                    handle.onFailure(ChatAppTag.CREATE_CHAT.getTag(), e);
                    return;
                }
                //once the chat was created, it can be handled as a response on the activity/fragment.
                handle.onSuccess(ChatAppTag.CREATE_CHAT.getTag(), groupChannel);
            }
        });
    }

    public static void getChat(final String channelUrl, final MasterHandle handle){
        //Method called when want to select an specific channel
        OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {

                if (e != null) {    // Error.
                    handle.onFailure(ChatAppTag.GET_CHAT.getTag(), e);
                    return;
                }
                // inside of the chat . . .
                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {    // Error.
                            handle.onSuccess(ChatAppTag.GET_CHAT.getTag(), channelUrl);
                            return;
                        }
                    }
                });
            }
        });
    }
    public static void sendMessageText(GroupChannel groupChannel, final String message, final MasterHandle handle){
        groupChannel.sendUserMessage(message, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {    // Error.
                    handle.onFailure(ChatAppTag.SEND_MESSAGE_TEXT.getTag(), e);
                    return;
                }
                handle.onSuccess(ChatAppTag.SEND_MESSAGE_TEXT.getTag(), message);
            }
        });
    }

    public static void disconnectFromServer(final MasterHandle handle){
        SendBird.disconnect(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {
                handle.onSuccess(ChatAppTag.DISCONNECT_FROM_SERVER.getTag());
            }
        });
    }

    public static void changeProfileInformation(String nickname, String profileUrl, final MasterHandle handle){
        SendBird.updateCurrentUserInfo(nickname, profileUrl, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {    // Error.
                    handle.onFailure(ChatAppTag.CHANGE_PROFILE_INFORMATION.getTag(),e);
                    return;
                }
                handle.onSuccess(ChatAppTag.CHANGE_PROFILE_INFORMATION.getTag());
            }
        });
    }

    public static void changeProfileInformation(String nickname, File photoFile, final MasterHandle handle){
        SendBird.updateCurrentUserInfoWithProfileImage(nickname, photoFile, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {    // Error.
                    handle.onFailure(ChatAppTag.CHANGE_PROFILE_INFORMATION.getTag(),e);
                    return;
                }
                handle.onSuccess(ChatAppTag.CHANGE_PROFILE_INFORMATION.getTag());
            }
        });
    }
    //getters and setters
    public String getAPP_ID() {
        return APP_ID;
    }

    public void getListOfUsersById(List<String> userIds, final MasterHandle handle){
        //Check notes on head of class.

        ApplicationUserListQuery applicationUserListQueryByIds = SendBird.createApplicationUserListQuery();
        applicationUserListQueryByIds.setUserIdsFilter(userIds);
        applicationUserListQueryByIds.next(new UserListQuery.UserListQueryResultHandler() {
            @Override
            public void onResult(List<User> list, SendBirdException e) {
                if (e != null) {    // Error.
                    handle.onFailure(ChatAppTag.GET_LIST_OF_USERS_BY_ID.getTag(),e);
                    return;
                }
                handle.onSuccess(ChatAppTag.GET_LIST_OF_USERS_BY_ID.getTag(),list);
            }
        });
    }




    public void blockUser(User user, MasterHandle handle){
        //TODO MAKE GETUSER, BLOCK AND UNBLOCK get by id (string)
        SendBird.blockUser(user, new SendBird.UserBlockHandler() {
            @Override
            public void onBlocked(User user, SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
            }
        });


    }
    public void unblockUser(User user, MasterHandle handle){
        // In case of unblocking a user
        //TODO MAKE GETUSER, BLOCK AND UNBLOCK
        SendBird.unblockUser(user, new SendBird.UserUnblockHandler() {
            @Override
            public void onUnblocked(SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
            }
        });
    }
}
