package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Contest")
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

    @Column(name = "details", nullable = true, unique = false, updatable = true)
    private String details;

    @Column(name = "maxNumberProjects", nullable = false, unique = false, updatable = true)
    private String maxNumberProjects;

    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private StatusContest status;

    @Column(name = "winner", nullable = true, unique = false, updatable = true)
    private int winnerProjectId;

    @OneToMany(mappedBy = "contest")
    private List<Project> listProjects = new ArrayList<>();

    public Contest (){}

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

    public String getMaxNumberProjects() {
        return maxNumberProjects;
    }

    public void setMaxNumberProjects(String maxNumberProjects) {
        this.maxNumberProjects = maxNumberProjects;
    }

    public StatusContest getStatus() {
        return status;
    }

    public void setStatus(StatusContest status) {
        this.status = status;
    }

    public int getWinnerProjectId() {
        return winnerProjectId;
    }

    public void setWinnerProjectId(int winnerProjectId) {
        this.winnerProjectId = winnerProjectId;
    }

    public List<Project> getListProjects() {
        return listProjects;
    }

    public void setListProjects(List<Project> listProjects) {
        this.listProjects = listProjects;
    }


}
