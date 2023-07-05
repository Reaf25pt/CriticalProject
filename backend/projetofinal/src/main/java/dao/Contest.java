package dao;

import ENUM.StatusContest;
import ENUM.StatusProject;
import entity.Project;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

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

    public List<entity.Contest> findActiveContests() {

        List<entity.Contest> list = new ArrayList<>();
        try {
            list = (List<entity.Contest>) em.createNamedQuery("Contest.findActiveContests").setParameter("concluded", StatusContest.CONCLUDED).setParameter("planning", StatusContest.PLANNING)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public Long countPlanningContest() {
        Long count;
        try {
            count = (Long) em.createNamedQuery("Contest.countPlanningContest").setParameter("planning", StatusContest.PLANNING)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return count;
    }


}
