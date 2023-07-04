package entity;


import ENUM.Office;
import ENUM.StatusProject;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Project")
@NamedQuery(name = "Project.findProjectById", query = "SELECT p FROM Project p WHERE p.id = :id")

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "title", nullable = false, unique = false, updatable = true)
    private String title;

    @Column(name = "office", nullable = true, unique = false, updatable = true)
    private Office office;

    @Column(name = "details", nullable = false, unique = false, updatable = true)
    private String details;

    @Column(name = "resources", nullable = true, unique = false, updatable = true)
    private String resources;

    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private StatusProject status;

    @Column(name = "membersNumber", nullable = false, unique = false, updatable = true)
    private int membersNumber = 4;

    @Column(name = "creationDate", nullable = false, unique = false, updatable = false)
    private Date creationDate;

    @Column(name = "finishDate", nullable = true, unique = false, updatable = false)
    private Date finishDate;


    @OneToMany(mappedBy = "project")
    private List<ProjectChatMessage> chatMsgList = new ArrayList<>();

    @ManyToMany(mappedBy = "listProject_Skills")
    private List<Skill> listSkills = new ArrayList<>();

    @ManyToMany(mappedBy = "listProject_Keywords")
    private List<Keyword> listKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "projectToParticipate")
    private List<ProjectMember> listPotentialMembers = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Task> listTasks = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ContestApplication> listContest = new ArrayList<>();
    @OneToMany(mappedBy = "project")
    private List<ProjectHistory> listRecords = new ArrayList<>();

/*
    @ManyToOne
    private Contest contest;
*/



    public Project() {
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

    public List<ProjectChatMessage> getChatMsgList() {
        return chatMsgList;
    }

    public void setChatMsgList(List<ProjectChatMessage> chatMsgList) {
        this.chatMsgList = chatMsgList;
    }

    public List<Skill> getListSkills() {
        return listSkills;
    }

    public void setListSkills(List<Skill> listSkills) {
        this.listSkills = listSkills;
    }

    public List<Keyword> getListKeywords() {
        return listKeywords;
    }

    public void setListKeywords(List<Keyword> listKeywords) {
        this.listKeywords = listKeywords;
    }

    public List<ProjectMember> getListPotentialMembers() {
        return listPotentialMembers;
    }

    public void setListPotentialMembers(List<ProjectMember> listPotentialMembers) {
        this.listPotentialMembers = listPotentialMembers;
    }

    public List<Task> getListTasks() {
        return listTasks;
    }

    public void setListTasks(List<Task> listTasks) {
        this.listTasks = listTasks;
    }



    public List<ContestApplication> getListContest() {
        return listContest;
    }

    public void setListContest(List<ContestApplication> listContest) {
        this.listContest = listContest;
    }

    public List<ProjectHistory> getListRecords() {
        return listRecords;
    }

    public void setListRecords(List<ProjectHistory> listRecords) {
        this.listRecords = listRecords;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    /*
    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
