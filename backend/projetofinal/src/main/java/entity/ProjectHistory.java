package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="ProjectHistory")
@NamedQuery(name = "ProjectHistory.findListOfRecordsByProjId", query = "SELECT r FROM ProjectHistory r WHERE r.project.id = :id ")

public class ProjectHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "message", nullable = false, unique = false, updatable = true)
    private String message;

    @Column (name="creationTime", nullable=false, unique=false, updatable=false)
    private Date creationTime;

    /*
    @Column(name = "taskId", nullable = true, unique = false, updatable = true)
    private int taskId;
*/
    // record can be associated with a given task
    @ManyToOne
    private Task task;
    @ManyToOne
    private User author;
    @ManyToOne
    private Project project;

public ProjectHistory(){

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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
