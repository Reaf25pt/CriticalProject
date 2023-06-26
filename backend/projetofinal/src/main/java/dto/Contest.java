package dto;

import ENUM.StatusContest;

import java.util.Date;

public class Contest {
    private int id;
    private String title;
    private Date startDate;
    private Date finishDate;

    private Date startOpenCall;
    private Date finishOpenCall;
    private String details;

    private String rules;
    private int maxNumberProjects;
    private String status;

    private int statusInt;
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

    public int getMaxNumberProjects() {
        return maxNumberProjects;
    }

    public void setMaxNumberProjects(int maxNumberProjects) {
        this.maxNumberProjects = maxNumberProjects;
    }

    public Date getStartOpenCall() {
        return startOpenCall;
    }

    public void setStartOpenCall(Date startOpenCall) {
        this.startOpenCall = startOpenCall;
    }

    public Date getFinishOpenCall() {
        return finishOpenCall;
    }

    public void setFinishOpenCall(Date finishOpenCall) {
        this.finishOpenCall = finishOpenCall;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusInt() {
        return statusInt;
    }

    public void setStatusInt(int statusInt) {
        this.statusInt = statusInt;
    }

    public int getWinnerProjectId() {
        return winnerProjectId;
    }

    public void setWinnerProjectId(int winnerProjectId) {
        this.winnerProjectId = winnerProjectId;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
