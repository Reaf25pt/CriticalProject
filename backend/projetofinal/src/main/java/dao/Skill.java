package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Skill extends Abstract<entity.Skill>{
    private static final long serialVersionUID = 1L;

    public Skill(){
        super(entity.Skill.class);
    }
}

