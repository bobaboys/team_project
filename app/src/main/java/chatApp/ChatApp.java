package chatApp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.mentalhealthapp.R;
import com.parse.ParseUser;
import com.sendbird.android.ApplicationUserListQuery;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.GroupChannelParams;
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

    **THIS IS A SINGLETON CLASS.
     */

    public static final String TAG = ChatApp.class.getSimpleName();
    private static ChatApp single_instance = null;


    private User sendBirdUser;
    private static String APP_ID;
    private ChatApp(){}
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static ChatApp getInstance(){
        if(single_instance == null){
            single_instance = new ChatApp();
        }
        return  single_instance;
    }

    public static void startChatApp(Context context){
        APP_ID = context.getString(R.string.APP_CHAT_ID);
        SendBird.init(APP_ID, context);
        //This api is called using only our secret App ID.
    }

    //will create user if you don't have one
    public static void connectToServer(String userId, final ConnectionHandle handle){
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {    // Error.
                    Log.e(TAG, "Could not connect to server");
                    e.printStackTrace();
                    //This callback handle is implemented when is called this method
                    if (handle != null) {
                        handle.onFailure(ChatAppTag.CONNECT_TO_SERVER.getTag(),e);
                    }

                    return;
                }
                //If everything goes well, this method is gonna be called as callback
                //on the anonymous class inside the method when is executed.
                if (handle != null) {
                    handle.onSuccess(ChatAppTag.CONNECT_TO_SERVER.getTag(), user);
                }
            }
        });
    }


    //Chats
    public static void createChat(ParseUser userCreator, ParseUser userHelper, boolean isEmergency, final CreateChatHandle handle){
        //Method called when want to create a new chat.
        List<String> userIds = new ArrayList<>();
        userIds.add(userCreator.getObjectId());
        userIds.add(userHelper.getObjectId());

        List<String> operatorIds = new ArrayList<>();
        operatorIds.add(userHelper.getObjectId());

        GroupChannelParams params = new GroupChannelParams() // we create a new channel / chat with certain characteristics
                .setPublic(false) // private conversation between helper and receiver.
                .setEphemeral(isEmergency) // If ephimeral NO keeps messages on server
                .setDistinct(true) // unique channel between 2 users. TODO
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

    public static void getChat(final String channelUrl, final CreateChatHandle handle){
        //Method called when want to select an specific channel
        GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {

                if (e != null) {    // Error.
                    handle.onFailure(ChatAppTag.GET_CHAT.getTag(), e);
                    return;
                }
                // inside of the chat . . .
                handle.onSuccess(ChatAppTag.GET_CHAT.getTag(), groupChannel);
            }
        });
    }



    // block and unblock users
    public void blockUser(User user, GeneralHandle handle){
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
    public void unblockUser(User user, GeneralHandle handle){
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

    public void setSendBirdUser(User sendBirdUser) {
        this.sendBirdUser = sendBirdUser;
    }
}
