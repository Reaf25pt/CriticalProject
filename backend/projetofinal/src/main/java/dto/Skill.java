package dto;

import ENUM.SkillType;

public class Skill {

    private int id;
    private String title;
   // private SkillType skillType;

    private int skillType;

    public Skill() {
    }

    public Skill(int id, String title, SkillType skillType) {
        this.id = id;
        this.title = title;
     //   this.skillType = skillType;
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

    public int getSkillType() {
        return skillType;
    }

    public void setSkillType(int skillType) {
        this.skillType = skillType;
    }
}
