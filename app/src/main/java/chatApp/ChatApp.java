package chatApp;

import android.app.Application;
import android.content.Context;

import com.example.mentalhealthapp.R;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.ArrayList;
import java.util.List;

public class ChatApp extends Application {



    private String APP_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        startChatApp(getApplicationContext());
    }

    public  void startChatApp(Context context){
        APP_ID = context.getString(R.string.APP_ID);
        SendBird.init(APP_ID, context);


    }

    public static void connectToServer(String userId){
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
            }
        });
    }



    public static void createChat(String userCreator, String userHelper, String name ){
        List<String> userIds = new ArrayList<>();
        userIds.add(userCreator);
        userIds.add(userHelper);

        List<String> operatorIds = new ArrayList<>();
        userIds.add(userHelper);

        String uniqueChannel = userCreator +"_"+ userHelper;//TODO CHANNEL WELL CREATED?


        GroupChannelParams params = new GroupChannelParams()
                .setPublic(false) // private conversation between helper and reciever.
                .setEphemeral(false) // keeps messages on server
                .setDistinct(false)
                .addUserIds(userIds)
                .setOperatorUserIds(operatorIds)    // or .setOperators(List<User> operators)
                .setChannelUrl(uniqueChannel); // In case of a group channel, you can create a new channel by specifying its unique channel URL in a 'GroupChannelParams' object.

        GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {    // Error.
                    return;
                }
            }
        });
    }

    public static void getChat(String channelUrl){
        OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {

                if (e != null) {    // Error.
                    return;
                }
                // inside of the chat . . .
                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {    // Error.
                            return;
                        }
                    }
                });
            }
        });
    }


    //getters and setters
    public String getAPP_ID() {
        return APP_ID;
    }

}
