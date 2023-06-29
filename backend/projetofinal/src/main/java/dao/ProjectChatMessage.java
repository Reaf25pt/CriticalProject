package dao;

import entity.Project;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProjectChatMessage extends Abstract<entity.ProjectChatMessage>{
    private static final long serialVersionUID = 1L;

    public ProjectChatMessage(){
        super(entity.ProjectChatMessage.class);
    }

    public List<entity.ProjectChatMessage> findListOfMessagesByProjId(int id) {
        List<entity.ProjectChatMessage> list = new ArrayList<>();
        try {
            list = (List<entity.ProjectChatMessage>) em.createNamedQuery("ProjectChatMessage.findListOfMessagesByProjId").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }



}

