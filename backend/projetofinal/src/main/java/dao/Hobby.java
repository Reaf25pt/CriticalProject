package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Hobby extends Abstract<entity.Hobby>{
    private static final long serialVersionUID = 1L;

    public Hobby(){
        super(entity.Hobby.class);
    }
}
