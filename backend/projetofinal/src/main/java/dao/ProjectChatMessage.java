package dao;

import jakarta.ejb.Stateless;

@Stateless
public class ProjectChatMessage extends Abstract<entity.ProjectChatMessage>{
    private static final long serialVersionUID = 1L;

    public ProjectChatMessage(){
        super(entity.ProjectChatMessage.class);
    }
}

