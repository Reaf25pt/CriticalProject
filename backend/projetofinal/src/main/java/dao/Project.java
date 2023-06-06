package dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

@Stateless
public class Project extends Abstract<entity.Project>{
    private static final long serialVersionUID = 1L;

    public Project(){
        super(entity.Project.class);
    }



    public entity.Project findProjectById(int id) {
        entity.Project ent = null;
        try {
            ent = (entity.Project) em.createNamedQuery("Project.findProjectById").setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }
}

