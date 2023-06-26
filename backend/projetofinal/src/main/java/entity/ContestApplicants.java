package entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "ContestApplicants")
public class ContestApplicants implements Serializable {

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

    public ContestApplicants() {
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
