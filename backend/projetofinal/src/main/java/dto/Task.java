package dto;

import entity.StatusTask;

import java.util.Date;

public class Task {

    private int id;
    private String title;
    private Date startDate;
    private Date finishDate;

    private String details;
    private StatusTask status;
    private int taskOwnerId;

    private String taskOwnerFirstName;
    private String taskOwnerLastName;
    private String taskOwnerPhoto;

    private String additionalExecutors;

    public Task(int id, String title, Date startDate, Date finishDate, String details, StatusTask status, int taskOwnerId, String taskOwnerFirstName, String taskOwnerLastName, String taskOwnerPhoto, String additionalExecutors) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.details = details;
        this.status = status;
        this.taskOwnerId = taskOwnerId;
        this.taskOwnerFirstName = taskOwnerFirstName;
        this.taskOwnerLastName = taskOwnerLastName;
        this.taskOwnerPhoto = taskOwnerPhoto;
        this.additionalExecutors = additionalExecutors;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public int getTaskOwnerId() {
        return taskOwnerId;
    }

    public void setTaskOwnerId(int taskOwnerId) {
        this.taskOwnerId = taskOwnerId;
    }

    public String getTaskOwnerFirstName() {
        return taskOwnerFirstName;
    }

    public void setTaskOwnerFirstName(String taskOwnerFirstName) {
        this.taskOwnerFirstName = taskOwnerFirstName;
    }

    public String getTaskOwnerLastName() {
        return taskOwnerLastName;
    }

    public void setTaskOwnerLastName(String taskOwnerLastName) {
        this.taskOwnerLastName = taskOwnerLastName;
    }

    public String getTaskOwnerPhoto() {
        return taskOwnerPhoto;
    }

    public void setTaskOwnerPhoto(String taskOwnerPhoto) {
        this.taskOwnerPhoto = taskOwnerPhoto;
    }

    public String getAdditionalExecutors() {
        return additionalExecutors;
    }

    public void setAdditionalExecutors(String additionalExecutors) {
        this.additionalExecutors = additionalExecutors;
    }
}
