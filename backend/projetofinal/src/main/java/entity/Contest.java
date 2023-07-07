package entity;

import ENUM.StatusContest;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Contest")
@NamedQuery(name = "Contest.findListOfWinnerProjects", query = "SELECT c.winner FROM Contest c WHERE c.winner IS NOT NULL")
@NamedQuery(name = "Contest.findActiveContests", query = "SELECT c FROM Contest c WHERE c.status NOT IN (:concluded, :planning)  ")
@NamedQuery(name = "Contest.countPlanningContest", query = "SELECT COUNT(c) FROM Contest c WHERE c.status IN (:planning)")
@NamedQuery(name = "Contest.findContestListContainingStr", query = "SELECT c FROM Contest c WHERE LOWER(c.title) LIKE LOWER(:str) ")

public class Contest implements Serializable {

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
    @Column(name = "startOpenCall", nullable = false, unique = false, updatable = true)
    private Date startOpenCall;

    @Column(name = "finishOpenCall", nullable = false, unique = false, updatable = true)
    private Date finishOpenCall;

    @Column(name = "details", nullable = true, unique = false, updatable = true)
    private String details;

    @Column(name = "rules", nullable = true, unique = false, updatable = true)
    private String rules;
    //TODO rules ser nullable ou não ?!

    @Column(name = "maxNumberProjects", nullable = false, unique = false, updatable = true)
    private int maxNumberProjects;

    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private StatusContest status;

    /*
    @Column(name = "winner", nullable = true, unique = false, updatable = true)
    private int winnerProjectId;*/


    @OneToOne
    private Project winner;

    /*
    @OneToMany(mappedBy = "contest")
    private List<Project> listProjects = new ArrayList<>();
*/
    @OneToMany(mappedBy = "contest")
    private List<ContestApplication> listProjectApplicants = new ArrayList<>();

    public Contest() {
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

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public int getMaxNumberProjects() {
        return maxNumberProjects;
    }

    public void setMaxNumberProjects(int maxNumberProjects) {
        this.maxNumberProjects = maxNumberProjects;
    }

    public StatusContest getStatus() {
        return status;
    }

    public void setStatus(StatusContest status) {
        this.status = status;
    }

    public Project getWinner() {
        return winner;
    }

    public void setWinner(Project winner) {
        this.winner = winner;
    }

    public List<ContestApplication> getListProjectApplicants() {
        return listProjectApplicants;
    }

    public void setListProjectApplicants(List<ContestApplication> listProjectApplicants) {
        this.listProjectApplicants = listProjectApplicants;
    }

    public Date getStartOpenCall() {
        return startOpenCall;
    }

    public void setStartOpenCall(Date startOpenCall) {
        this.startOpenCall = startOpenCall;
    }

    public Date getFinishOpenCall() {
        return finishOpenCall;
    }

    public void setFinishOpenCall(Date finishOpenCall) {
        this.finishOpenCall = finishOpenCall;
    }
}
