package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Project extends Abstract<entity.Project>{
    private static final long serialVersionUID = 1L;

    public Project(){
        super(entity.Project.class);
    }
}

