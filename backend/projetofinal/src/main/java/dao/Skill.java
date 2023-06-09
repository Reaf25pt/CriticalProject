package dao;

import ENUM.SkillType;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

@Stateless
public class Skill extends Abstract<entity.Skill>{
    private static final long serialVersionUID = 1L;

    public Skill(){
        super(entity.Skill.class);
    }

    public entity.Skill findSkillByTitle(String title) {
        entity.Skill ent = null;
        try {
            ent = (entity.Skill) em.createNamedQuery("Skill.findSkillByTitle").setParameter("title", title.toLowerCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }



}

