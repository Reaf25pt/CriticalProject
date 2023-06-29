package dto;

import ENUM.StatusProject;

import java.util.Date;

public class ProjectChat {

    private int chatMessageId;

    private Date creationTime;

    private String message;

    private int userSenderId;

    private String userSenderFirstName;
    private String userSenderLastName;
    private String userSenderPhoto;
   // private StatusProject status;

    private int projectId;

    public ProjectChat() {
    }

    public ProjectChat(int chatMessageId, Date creationTime, String message, int userSenderId, String userSenderFirstName, String userSenderLastName, String userSenderPhoto, StatusProject status, int projectId) {
        this.chatMessageId = chatMessageId;
        this.creationTime = creationTime;
        this.message = message;
        this.userSenderId = userSenderId;
        this.userSenderFirstName = userSenderFirstName;
        this.userSenderLastName = userSenderLastName;
        this.userSenderPhoto = userSenderPhoto;
      //  this.status = status;
        this.projectId = projectId;
    }


    public int getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(int chatMessageId) {
        this.chatMessageId = chatMessageId;
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

    public int getUserSenderId() {
        return userSenderId;
    }

    public void setUserSenderId(int userSenderId) {
        this.userSenderId = userSenderId;
    }

    public String getUserSenderFirstName() {
        return userSenderFirstName;
    }

    public void setUserSenderFirstName(String userSenderFirstName) {
        this.userSenderFirstName = userSenderFirstName;
    }

    public String getUserSenderLastName() {
        return userSenderLastName;
    }

    public void setUserSenderLastName(String userSenderLastName) {
        this.userSenderLastName = userSenderLastName;
    }

    public String getUserSenderPhoto() {
        return userSenderPhoto;
    }

    public void setUserSenderPhoto(String userSenderPhoto) {
        this.userSenderPhoto = userSenderPhoto;
    }


    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
