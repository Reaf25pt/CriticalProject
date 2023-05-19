package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="Task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "title", nullable = false, unique = false, updatable = true)
    private String title;

    @Column(name = "startDate", nullable = false, unique = false, updatable = true)
    private Date startDate;

    @Column(name = "finishDate", nullable = false, unique = false, updatable = true)
    private Date finishDate;

    @Column(name = "details", nullable = false, unique = false, updatable = true)
    private String details;

    // person responsible for the task
    @Column(name = "owner", nullable = false, unique = false, updatable = true)
    private int taskOwnerId;

    @Column(name = "additionalExecutors", nullable = true, unique = false, updatable = true)
    private String additionalExecutors;

    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private StatusTask status;

    // identifies task who needs to be completed before current one starts
    @Column(name = "precedence", nullable = true, unique = false, updatable = true)
    private int precedence;

    @ManyToOne
    private Project project;

public Task(){}

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

    public int getTaskOwnerId() {
        return taskOwnerId;
    }

    public void setTaskOwnerId(int taskOwnerId) {
        this.taskOwnerId = taskOwnerId;
    }

    public String getAdditionalExecutors() {
        return additionalExecutors;
    }

    public void setAdditionalExecutors(String additionalExecutors) {
        this.additionalExecutors = additionalExecutors;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public int getPrecedence() {
        return precedence;
    }

    public void setPrecedence(int precedence) {
        this.precedence = precedence;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
