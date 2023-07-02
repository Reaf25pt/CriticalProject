package dto;

import java.util.Date;

public class PersonalMessage {

    private int id;

    private Date creationTime;
    private String message;
    private boolean seen;
    private int userSenderId;

    private int userReceiverId;

    public PersonalMessage(int id, Date creationTime, String message, boolean seen, int userSenderId) {
        this.id = id;
        this.creationTime = creationTime;
        this.message = message;
        this.seen = seen;
        this.userSenderId = userSenderId;
    }

    public PersonalMessage() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public int getUserSenderId() {
        return userSenderId;
    }

    public void setUserSenderId(int userSenderId) {
        this.userSenderId = userSenderId;
    }

    public int getUserReceiverId() {
        return userReceiverId;
    }

    public void setUserReceiverId(int userReceiverId) {
        this.userReceiverId = userReceiverId;
    }
}
