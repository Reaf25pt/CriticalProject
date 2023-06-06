package dao;

import entity.Project;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProjectMember extends Abstract<entity.ProjectMember>{
    private static final long serialVersionUID = 1L;

    public ProjectMember(){
        super(entity.ProjectMember.class);
    }

    // encontra todos os projectos em que userId participa / participou AND not removed!
    public List<entity.Project> findListOfProjectsByUserId(int id) {
        List<entity.Project> projectList = new ArrayList<Project>();
        try {
            projectList = (List<Project>) em.createNamedQuery("ProjectMember.findListOfProjectsByUserId").setParameter("userId", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return projectList;
    }

}
