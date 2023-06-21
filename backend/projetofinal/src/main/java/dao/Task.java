package dao;

import ENUM.StatusProject;
import ENUM.StatusTask;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;

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







}
