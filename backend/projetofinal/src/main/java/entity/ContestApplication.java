package entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "ContestApplication")
@NamedQuery(name = "ContestApplication.findApplicationsForGivenContestId", query = "SELECT a FROM ContestApplication a WHERE a.contest.id = :contestId ")
@NamedQuery(name = "ContestApplication.findApplicationForGivenContestIdAndProjectId", query = "SELECT a FROM ContestApplication a WHERE a.contest.id = :contestId AND a.project.id = :projectId ")
@NamedQuery(name = "ContestApplication.findAcceptedApplicationsForGivenContestId", query = "SELECT a FROM ContestApplication a WHERE a.contest.id = :contestId AND a.accepted = true ")
@NamedQuery(name = "ContestApplication.findApplicationsNotAnsweredForGivenContestId", query = "SELECT a FROM ContestApplication a WHERE a.contest.id = :contestId AND a.answered = false ")
@NamedQuery(name = "ContestApplication.findAcceptedProjectsForGivenContestId", query = "SELECT a.project FROM ContestApplication a WHERE a.contest.id = :contestId AND a.accepted = true ")
@NamedQuery(name = "ContestApplication.findAcceptedApplicationForGivenProjectId", query = "SELECT a FROM ContestApplication a WHERE a.project.id = :projectId AND a.accepted=true")

public class ContestApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @ManyToOne
    private Contest contest;

    // projecto que se candidata a concurso
    @ManyToOne
    private Project project;

    // se projecto tem resposta. False by default
    @Column(name = "answered", nullable = false, unique = false, updatable = true)
    private boolean answered = false;

    // se projecto foi aceite. False by default
    @Column(name = "accepted", nullable = false, unique = false, updatable = true)
    private boolean accepted = false;

    public ContestApplication() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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
}
