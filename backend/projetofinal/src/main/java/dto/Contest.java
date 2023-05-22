package dto;

import entity.StatusContest;

import java.util.Date;

public class Contest {
    private int id;
    private String title;
    private Date startDate;
    private Date finishDate;
    private String details;
    private String maxNumberProjects;
    private StatusContest status;
    private int winnerProjectId;

    public Contest(){

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

    public String getMaxNumberProjects() {
        return maxNumberProjects;
    }

    public void setMaxNumberProjects(String maxNumberProjects) {
        this.maxNumberProjects = maxNumberProjects;
    }

    public StatusContest getStatus() {
        return status;
    }

    public void setStatus(StatusContest status) {
        this.status = status;
    }

    public int getWinnerProjectId() {
        return winnerProjectId;
    }

    public void setWinnerProjectId(int winnerProjectId) {
        this.winnerProjectId = winnerProjectId;
    }
}
