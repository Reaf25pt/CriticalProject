package dao;

import jakarta.ejb.Stateless;

@Stateless
public class ProjectMember extends Abstract<entity.ProjectMember>{
    private static final long serialVersionUID = 1L;

    public ProjectMember(){
        super(entity.ProjectMember.class);
    }
}
