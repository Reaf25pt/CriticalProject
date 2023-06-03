package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="PersonalMessage")
public class PersonalMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="personalMessageId", nullable = false, unique = true, updatable = false)
    private int personalMessageId;

    @Column (name="creationTime", nullable=false, unique=false, updatable=false)
    private Date creationTime;

    @Column (name="message", nullable=false, unique=false, updatable=false)
    private String message;

    @Column (name="seen", nullable=false, unique=false, updatable=true)
    private boolean seen = false;

    /*
    @Column (name="userSenderId", nullable=false, unique=false, updatable=false)
    private int userSenderId;
*/
    // user que envia a mensagem pessoal
    @ManyToOne
    private User messageSender;

    // user que recebe a mensagem pessoal
    @ManyToOne
    private User messageReceiver;

    public PersonalMessage() {

    }

    public int getPersonalMessageId() {
        return personalMessageId;
    }

    public void setPersonalMessageId(int personalMessageId) {
        this.personalMessageId = personalMessageId;
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



    public User getMessageReceiver() {
        return messageReceiver;
    }

    public void setMessageReceiver(User messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    public User getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(User messageSender) {
        this.messageSender = messageSender;
    }
}
