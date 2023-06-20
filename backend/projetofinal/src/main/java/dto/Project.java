package dto;

import ENUM.Office;
import ENUM.StatusProject;

import java.util.Date;
import java.util.List;

public class Project {

    private int id;
    private String title;
    private int office;
    private String officeInfo;
    private String details;
    private String resources;
    private String status;

    private int statusInt;
    private int membersNumber;

    private int availableSpots;
    private Date creationDate;

    private List<Keyword> keywords;

    private List<Skill> skills;

    private boolean manager;

    private boolean member;

    public Project(int id, String title, Office office, String details, String resources, StatusProject status, int membersNumber, Date creationDate) {
        this.id = id;
        this.title = title;
       // this.office = office;
        this.details = details;
        this.resources = resources;
       // this.status = status;
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

    public int getOffice() {
        return office;
    }

    public void setOffice(int office) {
        this.office = office;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    public String getOfficeInfo() {
        return officeInfo;
    }

    public void setOfficeInfo(String officeInfo) {
        this.officeInfo = officeInfo;
    }

    public int getStatusInt() {
        return statusInt;
    }

    public void setStatusInt(int statusInt) {
        this.statusInt = statusInt;
    }

    public int getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }
}
