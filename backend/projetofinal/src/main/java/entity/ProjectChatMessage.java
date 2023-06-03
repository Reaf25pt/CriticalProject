package entity;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
public class ProjectChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "creationTime", nullable = false, unique = false, updatable = false)
    private Date creationTime;

    @Column(name = "message", nullable = false, unique = false, updatable = false)
    private String message;

    /*
    @Column(name = "userSenderId", nullable = false, unique = false, updatable = false)
    private int userSenderId;
*/
    // user que envia a mensagem
    @ManyToOne
    private User messageSender;
   /*
    @Column (name="seen", nullable=false, unique=false, updatable=true)
    private boolean seen = false;
   */
    // projecto a que dizem respeito as mensagens / chat
   @ManyToOne
    private Project project;



    public ProjectChatMessage(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(User messageSender) {
        this.messageSender = messageSender;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
