package dto;

import ENUM.SkillType;

public class Skill {

    private int id;
    private String title;
    private SkillType skillType;

    public Skill(int id, String title, SkillType skillType) {
        this.id = id;
        this.title = title;
        this.skillType = skillType;
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

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }
}
