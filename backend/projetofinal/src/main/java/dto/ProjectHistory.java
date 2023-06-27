package dto;

import java.util.Date;

public class ProjectHistory {

    private int id;
    private String message;
    private Date creationTime;
    private int taskId;

    private String taskTitle;

    private int authorId;
    private String authorPhoto;
    private String authorFirstName;
    private String authorLastName;


    public ProjectHistory() {
    }

    public ProjectHistory(int id, String message, Date creationTime, int taskId, String taskTitle, int authorId, String authorPhoto, String authorFirstName, String authorLastName) {
        this.id = id;
        this.message = message;
        this.creationTime = creationTime;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.authorId = authorId;
        this.authorPhoto = authorPhoto;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPhoto() {
        return authorPhoto;
    }

    public void setAuthorPhoto(String authorPhoto) {
        this.authorPhoto = authorPhoto;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }
}
