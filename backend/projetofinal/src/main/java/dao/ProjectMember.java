package dao;

import ENUM.StatusProject;
import entity.Project;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

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
        List<entity.Project> projectList = new ArrayList<>();
        try {
            projectList = (List<Project>) em.createNamedQuery("ProjectMember.findListOfProjectsByUserId").setParameter("userId", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return projectList;
    }

    public List<entity.User> findListOfManagersByProjectId(int id) {
        List<entity.User> managersList = new ArrayList<entity.User>();
        try {
            managersList = (List<User>) em.createNamedQuery("ProjectMember.findListOfManagersByProjectId").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return managersList;
    }

    public entity.ProjectMember findProjectMemberByProjectIdAndUserId(int projId, int userId) {
        entity.ProjectMember ent = null;
        try {
            ent = (entity.ProjectMember) em.createNamedQuery("ProjectMember.findProjectMemberByProjectIdAndUserId").setParameter("projId", projId).setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }

    public List<entity.User> findListOfUsersByProjectId(int id) {
        List<entity.User> membersList = new ArrayList<>();
        try {
            membersList = (List<User>) em.createNamedQuery("ProjectMember.findListOfUsersByProjectId").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return membersList;
    }

    public List<entity.ProjectMember> findListOfMembersByProjectId(int projId) {

        List<entity.ProjectMember> membersList = new ArrayList<>();
        try {
            membersList = (List<entity.ProjectMember>) em.createNamedQuery("ProjectMember.findListOfMembersByProjectId").setParameter("id", projId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return membersList;
    }

    public List<entity.User> findListOfUsersWithActiveProject() {

        List<entity.User> usersList = new ArrayList<>();
        try {
            usersList = (List<entity.User>) em.createNamedQuery("ProjectMember.findListOfUsersWithActiveProject").setParameter("cancelled", StatusProject.CANCELLED).setParameter("finished", StatusProject.FINISHED)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return usersList;
    }

    public entity.ProjectMember findActiveProjectByUserId( int userId) {
        entity.ProjectMember ent = null;
        try {
            ent = (entity.ProjectMember) em.createNamedQuery("ProjectMember.findActiveProjectByUserId").setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }





}
