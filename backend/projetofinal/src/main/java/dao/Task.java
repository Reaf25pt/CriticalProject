package dao;

import ENUM.StatusProject;
import ENUM.StatusTask;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class Task extends Abstract<entity.Task>{
    private static final long serialVersionUID = 1L;

    public Task(){
        super(entity.Task.class);
    }


    public List<entity.Task> findTasksFromProjectByProjId(int id) {
        List<entity.Task> taskList = new ArrayList<>();
        try {
            taskList = (List<entity.Task>) em.createNamedQuery("Task.findTasksFromProjectByProjId").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return taskList;
    }

    public List<entity.Task> findListOfTasksFromProjectByProjIdWhoseTaskOwnerIsGivenUserId(int id, int userId) {
        System.out.println("task dao metodo ");
        List<entity.Task> taskList = new ArrayList<>();
        try {
            taskList = (List<entity.Task>) em.createNamedQuery("Task.findListOfTasksFromProjectByProjIdWhoseTaskOwnerIsGivenUserId").setParameter("id", id).setParameter("userId", userId).setParameter("finished", StatusTask.FINISHED).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return taskList;
    }


    public Long countNotFinishedTasksFromProjectByProjId(int id) {
        Long count;
        try {
            count = (Long) em.createNamedQuery("Task.countNotFinishedTasksFromProjectByProjId").setParameter("id", id).setParameter("finished", StatusTask.FINISHED)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return count;
    }

    public entity.Task findFinalTaskByProjectId(int id) {
       entity.Task ent = null;
        try {
            ent = (entity.Task) em.createNamedQuery("Task.findFinalTaskByProjectId").setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }



}
