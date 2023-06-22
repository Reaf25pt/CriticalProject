package dto;

import ENUM.StatusTask;

import java.util.Date;
import java.util.List;

public class Task {

    private int id;
    private String title;
    private Date startDate;
    private Date finishDate;

    private String details;
     // private StatusTask status;

    private int statusInfo;
    private String status;
    private int taskOwnerId;

    private String taskOwnerFirstName;
    private String taskOwnerLastName;
    private String taskOwnerPhoto;

    private String additionalExecutors;

    private List<Task> preRequiredTasks;



    public Task() {
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

    public int getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(int statusInfo) {
        this.statusInfo = statusInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public List<Task> getPreRequiredTasks() {
        return preRequiredTasks;
    }

    public void setPreRequiredTasks(List<Task> preRequiredTasks) {
        this.preRequiredTasks = preRequiredTasks;
    }
}
