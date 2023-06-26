package dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ContestApplication extends Abstract<entity.ContestApplication>{
    private static final long serialVersionUID = 1L;

    public ContestApplication(){
        super(entity.ContestApplication.class);
    }

    public List<entity.ContestApplication> findApplicationsForGivenContestId(int contestId) {
        List<entity.ContestApplication> list = new ArrayList<>();
        try {
            list = (List<entity.ContestApplication>) em.createNamedQuery("ContestApplication.findApplicationsForGivenContestId").setParameter("contestId", contestId).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public entity.ContestApplication findApplicationForGivenContestIdAndProjectId(int contestId, int projectId) {
        entity.ContestApplication ent = null;
        try {
            ent = (entity.ContestApplication) em.createNamedQuery("ContestApplication.findApplicationForGivenContestIdAndProjectId").setParameter("contestId", contestId).setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }

    public List<entity.ContestApplication> findAcceptedApplicationsForGivenContestId(int contestId) {
        List<entity.ContestApplication> list = new ArrayList<>();
        try {
            list = (List<entity.ContestApplication>) em.createNamedQuery("ContestApplication.findAcceptedApplicationsForGivenContestId").setParameter("contestId", contestId).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public List<entity.ContestApplication> findApplicationsNotAnsweredForGivenContestId(int contestId) {
        List<entity.ContestApplication> list = new ArrayList<>();
        try {
            list = (List<entity.ContestApplication>) em.createNamedQuery("ContestApplication.findApplicationsNotAnsweredForGivenContestId").setParameter("contestId", contestId).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }





}
