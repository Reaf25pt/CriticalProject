package entity;

import ENUM.StatusTask;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="Task")
@NamedQuery(name = "Task.findTasksFromProjectByProjId", query = "SELECT t FROM Task t WHERE t.project.id = :id")
@NamedQuery(name = "Task.findListOfTasksFromProjectByProjIdWhoseTaskOwnerIsGivenUserId", query = "SELECT t FROM Task t WHERE t.project.id = :id AND t.taskOwner.userId = :userId AND t.status NOT IN :finished")
@NamedQuery(name = "Task.countNotFinishedTasksFromProjectByProjId", query = "SELECT COUNT(t) FROM Task t WHERE t.project.id = :id AND t.status NOT IN :finished")
@NamedQuery(name = "Task.findFinalTaskByProjectId", query = "SELECT t FROM Task t WHERE t.project.id = :id AND t.finalTask = true")


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

    @Column(name = "details", nullable = false, unique = false, updatable = true, columnDefinition = "TEXT")
    private String details;

    @Column(name = "finalTask", nullable = false, unique = false, updatable = true)
    private boolean finalTask;

    // person responsible for the task
    @ManyToOne
    private User taskOwner;
   // @Column(name = "owner", nullable = false, unique = false, updatable = true)
    //private int taskOwnerId;

    @Column(name = "additionalExecutors", nullable = true, unique = false, updatable = true, columnDefinition = "TEXT")
    private String additionalExecutors;

    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private StatusTask status;

    /*
    // identifies task who needs to be completed before current one starts
    @Column(name = "precedence", nullable = true, unique = false, updatable = true)
    private int precedence;
*/
    @ManyToOne
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "PrerequiredTasks",
            joinColumns = @JoinColumn(name = "currentTaskId"),  // owning side, quem persiste dados na DB
            inverseJoinColumns = @JoinColumn(name = "requiredTaskId"))
    private List<Task> listPreRequiredTasks = new ArrayList<>();

    public Task(int id, String title, Date startDate, Date finishDate, String details, int taskOwnerId, String additionalExecutors, StatusTask status, Project project, List<Task> listPreRequiredTasks) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.details = details;
       // this.taskOwnerId = taskOwnerId;
        this.additionalExecutors = additionalExecutors;
        this.status = status;
        this.project = project;
        this.listPreRequiredTasks = listPreRequiredTasks;
    }

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

    public User getTaskOwner() {
        return taskOwner;
    }

    public void setTaskOwner(User taskOwner) {
        this.taskOwner = taskOwner;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Task> getListPreRequiredTasks() {
        return listPreRequiredTasks;
    }

    public void setListPreRequiredTasks(List<Task> listPreRequiredTasks) {
        this.listPreRequiredTasks = listPreRequiredTasks;


    }

    public boolean isFinalTask() {
        return finalTask;
    }

    public void setFinalTask(boolean finalTask) {
        this.finalTask = finalTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
