package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="ProjectHistory")
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

    // record can be associated with a given task
    @Column(name = "taskId", nullable = true, unique = false, updatable = true)
    private int taskId;

    @ManyToOne
    private User author;

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

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
