package entity;


import ENUM.SkillType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="Skill")
@NamedQuery(name = "Skill.findSkillByTitle", query = "SELECT s FROM Skill s WHERE LOWER(s.title)  = LOWER(:title) ")
@NamedQuery(name = "Skill.findRelationBetweenUserAndSkill", query = "SELECT COUNT(s) FROM Skill s JOIN s.listUsers_Skills u WHERE s.skillId = :skillId AND u.userId = :userId")
@NamedQuery(name = "Skill.findListOfSkillsByUserId", query = "SELECT s FROM Skill s  JOIN s.listUsers_Skills u WHERE u.userId = :userId")
@NamedQuery(name = "Skill.findSkillOfUserById", query = "SELECT s FROM Skill s  JOIN s.listUsers_Skills u WHERE u.userId = :userId AND s.skillId = :skillId")
@NamedQuery(name = "Skill.findSkillListContainingStr", query = "SELECT s FROM Skill s WHERE LOWER(s.title) LIKE LOWER(:str) ")
@NamedQuery(name = "Skill.findRelationBetweenProjAndSkill", query = "SELECT COUNT(s) FROM Skill s JOIN s.listProject_Skills p WHERE s.skillId = :skillId AND p.id = :projId")
@NamedQuery(name = "Skill.findListOfSkillsByProjId", query = "SELECT s FROM Skill s  JOIN s.listProject_Skills p WHERE p.id = :id")
@NamedQuery(name = "Skill.filterProjectsWhoHaveSkillMatchingStr", query = "SELECT s.listProject_Skills FROM Skill s WHERE LOWER(s.title) LIKE LOWER(:str)")

public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="skillId", nullable = false, unique = true, updatable = false )
    private int skillId;

    //Como podem ser partilhados por vários users, o título não poderá ser alterado
    @Column(name = "title", nullable = false, unique = true, updatable = false)
    private String title;

    @Column(name="skillType", nullable = false, unique = false, updatable = true)
    private SkillType type;

    @ManyToMany
    @JoinTable(
            name = "Skills_Users",
            joinColumns = @JoinColumn(name = "skill_Id"),
            inverseJoinColumns = @JoinColumn(name = "user_Id"))
    private List<User> listUsers_Skills = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "Skills_Projects",
            joinColumns = @JoinColumn(name = "skill_Id"),
            inverseJoinColumns = @JoinColumn(name = "project_Id"))
    private List<Project> listProject_Skills = new ArrayList<>();


    public Skill(){}

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SkillType getType() {
        return type;
    }

    public void setType(SkillType type) {
        this.type = type;
    }

    public List<User> getListUsers_Skills() {
        return listUsers_Skills;
    }

    public void setListUsers_Skills(List<User> listUsers_Skills) {
        this.listUsers_Skills = listUsers_Skills;
    }

    public List<Project> getListProject_Skills() {
        return listProject_Skills;
    }

    public void setListProject_Skills(List<Project> listProject_Skills) {
        this.listProject_Skills = listProject_Skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return skillId == skill.skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }
}



