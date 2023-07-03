package dao;

import entity.Project;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class Contest extends Abstract<entity.Contest>{
    private static final long serialVersionUID = 1L;

    public Contest(){
        super(entity.Contest.class);
    }

    public List<entity.Project> findListOfWinnerProjects() {
        List<entity.Project> projectList = new ArrayList<>();
        try {
            projectList = (List<Project>) em.createNamedQuery("Contest.findListOfWinnerProjects").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return projectList;
    }


}
