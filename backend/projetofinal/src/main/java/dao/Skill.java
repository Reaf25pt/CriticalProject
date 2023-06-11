package dao;

import ENUM.SkillType;
import entity.Hobby;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

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

    public Long findRelationBetweenUserAndSkill(int skillId, int userId) {
        Long count;
        try {
            count = (Long) em.createNamedQuery("Skill.findRelationBetweenUserAndSkill").setParameter("skillId", skillId).setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return count;
    }


    public List<entity.Skill> findListOfSkillsByUserId(int id) {
        List<entity.Skill> skillsList = new ArrayList<>();
        try {
            skillsList = (List<entity.Skill>) em.createNamedQuery("Skill.findListOfSkillsByUserId").setParameter("userId", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return skillsList;
    }

    public entity.Skill findSkillOfUserById(int userId, int skillId) {
        entity.Skill ent = null;
        try {
            ent = (entity.Skill) em.createNamedQuery("Skill.findSkillOfUserById").setParameter("userId", userId).setParameter("skillId", skillId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }

}

