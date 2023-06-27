package dao;

import entity.Project;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProjectHistory extends Abstract<entity.ProjectHistory>{
    private static final long serialVersionUID = 1L;

    public ProjectHistory(){
        super(entity.ProjectHistory.class);
    }


    public List<entity.ProjectHistory> findListOfRecordsByProjId(int id) {
        List<entity.ProjectHistory> list = new ArrayList<>();
        try {
            list = (List<entity.ProjectHistory>) em.createNamedQuery("ProjectHistory.findListOfRecordsByProjId").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }





}
