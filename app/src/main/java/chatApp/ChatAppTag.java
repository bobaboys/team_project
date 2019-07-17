package chatApp;

public enum ChatAppTag {
    CONNECT_TO_SERVER("CONNECT_TO_SERVER"),
    CREATE_CHAT("CREATE_CHAT"),
    SEND_MESSAGE_TEXT("SEND_MESSAGE_TEXT"),
    DISCONNECT_FROM_SERVER("DISCONNECT_FROM_SERVER"),
    CHANGE_PROFILE_INFORMATION("CHANGE_PROFILE_INFORMATION"),
    GET_LIST_OF_USERS_BY_ID("GET_LIST_OF_USERS_BY_ID"),
    GET_CHAT("GET_CHAT");


    private String tag;

    ChatAppTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
