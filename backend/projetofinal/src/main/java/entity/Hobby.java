package entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Hobby")
@NamedQuery(name = "Hobby.findHobbyByTitle", query = "SELECT h FROM Hobby h WHERE LOWER(h.hobbyTitle)  = LOWER(:title) ")
@NamedQuery(name = "Hobby.findRelationBetweenUserAndHobby", query = "SELECT COUNT(h) FROM Hobby h JOIN h.listUsers_Hobbies u WHERE h.hobbyId = :hobbyId AND u.userId = :userId")
@NamedQuery(name = "Hobby.findListOfHobbiesByUserId", query = "SELECT h FROM Hobby h  JOIN h.listUsers_Hobbies u WHERE u.userId = :userId")
@NamedQuery(name = "Hobby.findHobbyOfUserById", query = "SELECT h FROM Hobby h  JOIN h.listUsers_Hobbies u WHERE u.userId = :userId AND h.hobbyId = :hobbyId")

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

    public List<User> getListUsers_Hobbies() {
        return listUsers_Hobbies;
    }

    public void setListUsers_Hobbies(List<User> listUsers_Hobbies) {
        this.listUsers_Hobbies = listUsers_Hobbies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hobby hobby = (Hobby) o;
        return hobbyId == hobby.hobbyId && Objects.equals(hobbyTitle, hobby.hobbyTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hobbyId, hobbyTitle);
    }
}
