package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Hobby")
public class Hobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int hobbyId;

    //Como podem ser partilhados por vários users, o título não poderá ser alterado
    @Column(name = "title", nullable = false, unique = true, updatable = false)
    private String hobbyTitle;

    @ManyToMany
    @JoinTable(
            name = "Hobbies",
            joinColumns = @JoinColumn(name = "hobby_Id"),
            inverseJoinColumns = @JoinColumn(name = "user_Id"))
    private List<User> listUsers_Hobbies = new ArrayList<>();

    public Hobby() {
    }

    public int getHobbyId() {
        return hobbyId;
    }

    public void setHobbyId(int hobbyId) {
        this.hobbyId = hobbyId;
    }

    public String getHobbyTitle() {
        return hobbyTitle;
    }

    public void setHobbyTitle(String hobbyTitle) {
        this.hobbyTitle = hobbyTitle;
    }

    public List<User> getListUsers() {
        return listUsers_Hobbies;
    }

    public void setListUsers(List<User> listUsers) {
        this.listUsers_Hobbies = listUsers;
    }
}
