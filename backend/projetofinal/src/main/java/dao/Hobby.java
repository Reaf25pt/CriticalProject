package dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

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
}
