package com.example.centralchat.messages;

public class MessagesList {
    private final String userName;
    private final String getOtherIndexNum;
    private final String lastMessage;
    private final String profilePic;
    private final String chatKey;
    private final int unseenMessages;

    public MessagesList(String userName, String getOtherIndexNum, String lastMessage, String profilePic, int unseenMessages, String chatKey) {
        this.userName = userName;
        this.getOtherIndexNum = getOtherIndexNum;
        this.lastMessage = lastMessage;
        this.profilePic = profilePic;
        this.unseenMessages = unseenMessages;
        this.chatKey = chatKey;
    }

    public String getChatKey() {
        return chatKey;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getUserName() {
        return userName;
    }

    public String getGetOtherIndexNum() {
        return getOtherIndexNum;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
