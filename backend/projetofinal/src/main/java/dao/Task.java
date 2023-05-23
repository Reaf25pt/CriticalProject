package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Task extends Abstract<entity.Task>{
    private static final long serialVersionUID = 1L;

    public Task(){
        super(entity.Task.class);
    }
}
