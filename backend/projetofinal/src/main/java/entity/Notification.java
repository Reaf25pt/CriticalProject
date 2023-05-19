package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
 @Table(name="Notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="notificationId", nullable = false, unique = true, updatable = false)
    private int notificationId;

    @Column (name="creationTime", nullable=false, unique=false, updatable=false)
    private Date creationTime;

    @Column (name="message", nullable=false, unique=false, updatable=false)
    private String message;

    @Column (name="seen", nullable=false, unique=false, updatable=true)
    private boolean seen = false;

    @Column (name="needsInput", nullable=false, unique=false, updatable=true)
    private boolean needsInput = false;

    //To identify invitation to participate in project, that needs answer
    @Column (name="relationId", nullable=true, unique=false, updatable=false)
    private int relationId;

    // user que recebe a notificação do sistema / convite
    @ManyToOne
    private User notificationOwner;

    public Notification(){
    }


    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
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

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isNeedsInput() {
        return needsInput;
    }

    public void setNeedsInput(boolean needsInput) {
        this.needsInput = needsInput;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public User getNotificationOwner() {
        return notificationOwner;
    }

    public void setNotificationOwner(User notificationOwner) {
        this.notificationOwner = notificationOwner;
    }
}
