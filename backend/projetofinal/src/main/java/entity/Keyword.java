package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Keyword")
@NamedQuery(name = "Keyword.findKeywordByTitle", query = "SELECT k FROM Keyword k WHERE LOWER(k.title)  = LOWER(:title) ")
@NamedQuery(name = "Keyword.findKeywordListContainingStr", query = "SELECT k FROM Keyword k WHERE LOWER(k.title) LIKE LOWER(:str) ")

public class Keyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    //Como podem ser partilhados por vários users, o título não poderá ser alterado
    @Column(name = "title", nullable = false, unique = true, updatable = false)
    private String title;

    @ManyToMany
    @JoinTable(
            name = "Keywords",
            joinColumns = @JoinColumn(name = "keyword_Id"),
            inverseJoinColumns = @JoinColumn(name = "project_Id"))
    private List<Project> listProject_Keywords = new ArrayList<>();

    public Keyword(){}

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

    public List<Project> getListProject_Keywords() {
        return listProject_Keywords;
    }

    public void setListProject_Keywords(List<Project> listProject_Keywords) {
        this.listProject_Keywords = listProject_Keywords;
    }
}
