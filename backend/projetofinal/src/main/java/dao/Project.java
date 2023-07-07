package dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

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

    public List<entity.Project> findProjectListContainingStr(String str) {
        List<entity.Project> list = new ArrayList<>();
        try {
            list = (List<entity.Project>) em.createNamedQuery("Project.findProjectListContainingStr").setParameter("str", "%"+ str.toLowerCase()+"%").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }




}

