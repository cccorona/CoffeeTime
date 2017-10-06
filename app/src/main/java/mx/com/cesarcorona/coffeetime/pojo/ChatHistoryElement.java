package mx.com.cesarcorona.coffeetime.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 06/10/17.
 */

public class ChatHistoryElement implements Serializable {


    private String userChatWith;
    private String userPhoto;
    private long userChatDate;
    private String userChatWithDisplayName;


    public ChatHistoryElement(String userChatWith, String userPhoto, long userChatDate, String userChatWithDisplayName) {
        this.userChatWith = userChatWith;
        this.userPhoto = userPhoto;
        this.userChatDate = userChatDate;
        this.userChatWithDisplayName = userChatWithDisplayName;
    }

    public String getUserChatWith() {
        return userChatWith;
    }

    public void setUserChatWith(String userChatWith) {
        this.userChatWith = userChatWith;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public long getUserChatDate() {
        return userChatDate;
    }

    public void setUserChatDate(long userChatDate) {
        this.userChatDate = userChatDate;
    }

    public String getUserChatWithDisplayName() {
        return userChatWithDisplayName;
    }

    public void setUserChatWithDisplayName(String userChatWithDisplayName) {
        this.userChatWithDisplayName = userChatWithDisplayName;
    }
}
