package dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

@Stateless
public class Keyword extends Abstract<entity.Keyword>{
    private static final long serialVersionUID = 1L;

    public Keyword(){
        super(entity.Keyword.class);
    }

    public entity.Keyword findKeywordByTitle(String title) {
        entity.Keyword ent = null;
        try {
            ent = (entity.Keyword) em.createNamedQuery("Keyword.findKeywordByTitle").setParameter("title", title)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }
}
