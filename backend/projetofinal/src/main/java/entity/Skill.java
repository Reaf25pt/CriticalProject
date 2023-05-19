package entity;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Skill")
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="skillId", nullable = false, unique = true, updatable = false )
    private int skillId;

    //Como podem ser partilhados por vários users, o título não poderá ser alterado
    @Column(name = "title", nullable = false, unique = true, updatable = false)
    private String skillTitle;

    @Column(name="skillType", nullable = false, unique = false, updatable = true)
    private SkillType skillType;

    @ManyToMany
    @JoinTable(
            name = "Skills",
            joinColumns = @JoinColumn(name = "skill_Id"),
            inverseJoinColumns = @JoinColumn(name = "user_Id"))
    private List<User> listUsers_Skills = new ArrayList<>();


    public Skill(){}

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillTitle() {
        return skillTitle;
    }

    public void setSkillTitle(String skillTitle) {
        this.skillTitle = skillTitle;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public List<User> getListUsers_Skills() {
        return listUsers_Skills;
    }

    public void setListUsers_Skills(List<User> listUsers_Skills) {
        this.listUsers_Skills = listUsers_Skills;
    }
}



