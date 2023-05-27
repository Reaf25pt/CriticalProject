package dto;

import ENUM.Office;
import ENUM.StatusProject;

import java.util.Date;

public class Project {

    private int id;
    private String title;
    private Office office;
    private String details;
    private String resources;
    private StatusProject status;
    private int membersNumber;
    private Date creationDate;

    public Project(int id, String title, Office office, String details, String resources, StatusProject status, int membersNumber, Date creationDate) {
        this.id = id;
        this.title = title;
        this.office = office;
        this.details = details;
        this.resources = resources;
        this.status = status;
        this.membersNumber = membersNumber;
        this.creationDate = creationDate;
    }

    public Project(){}

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

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public StatusProject getStatus() {
        return status;
    }

    public void setStatus(StatusProject status) {
        this.status = status;
    }

    public int getMembersNumber() {
        return membersNumber;
    }

    public void setMembersNumber(int membersNumber) {
        this.membersNumber = membersNumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
