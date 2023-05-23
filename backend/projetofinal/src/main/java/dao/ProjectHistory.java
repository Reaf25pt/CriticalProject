package dao;

import jakarta.ejb.Stateless;

@Stateless
public class ProjectHistory extends Abstract<entity.ProjectHistory>{
    private static final long serialVersionUID = 1L;

    public ProjectHistory(){
        super(entity.ProjectHistory.class);
    }
}
