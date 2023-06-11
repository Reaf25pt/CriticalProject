package dao;

import entity.Project;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class Hobby extends Abstract<entity.Hobby>{
    private static final long serialVersionUID = 1L;

    public Hobby(){
        super(entity.Hobby.class);
    }

    public entity.Hobby findHobbyByTitle(String title) {
        entity.Hobby ent = null;
        try {
            ent = (entity.Hobby) em.createNamedQuery("Hobby.findHobbyByTitle").setParameter("title", title.toLowerCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }

    public Long findRelationBetweenUserAndHobby(int hobbyId, int userId) {
        Long count;
        try {
            count = (Long) em.createNamedQuery("Hobby.findRelationBetweenUserAndHobby").setParameter("hobbyId", hobbyId).setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return count;
    }

    public List<entity.Hobby> findListOfHobbiesByUserId(int id) {
        List<entity.Hobby> hobbiesList = new ArrayList<entity.Hobby>();
        try {
            hobbiesList = (List<entity.Hobby>) em.createNamedQuery("Hobby.findListOfHobbiesByUserId").setParameter("userId", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return hobbiesList;
    }

    public entity.Hobby findHobbyOfUserById(int userId, int hobbyId) {
        entity.Hobby ent = null;
        try {
            ent = (entity.Hobby) em.createNamedQuery("Hobby.findHobbyOfUserById").setParameter("userId", userId).setParameter("hobbyId", hobbyId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }




}
