package entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "ProjectMembers")
public class ProjectMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;
    @ManyToOne
    private Project projectToParticipate;

    // user who is invited or self-invites to participate in project
    @ManyToOne
    private User userInvited;

    // identifies user who invites someone to participate in project. Can be null if someone self-invites to participate
    @Column(name = "userWhoInvitesid", nullable = true, unique = false, updatable = false)
    private int whoInvitesid;

    @Column(name = "manager", nullable = false, unique = false, updatable = true)
    private boolean manager;

    // se convite foi respondido. False by default
    @Column(name = "answered", nullable = false, unique = false, updatable = true)
    private boolean answered = false;

    // se convite foi aceite. False by default
    @Column(name = "accepted", nullable = false, unique = false, updatable = true)
    private boolean accepted = false;

    // False by default. Changes to true if a member is removed from a project
    @Column(name = "removed", nullable = false, unique = false, updatable = true)
    private boolean removed = false;

public ProjectMember(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProjectToParticipate() {
        return projectToParticipate;
    }

    public void setProjectToParticipate(Project projectToParticipate) {
        this.projectToParticipate = projectToParticipate;
    }

    public User getUserInvited() {
        return userInvited;
    }

    public void setUserInvited(User userInvited) {
        this.userInvited = userInvited;
    }

    public int getWhoInvitesid() {
        return whoInvitesid;
    }

    public void setWhoInvitesid(int whoInvitesid) {
        this.whoInvitesid = whoInvitesid;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
