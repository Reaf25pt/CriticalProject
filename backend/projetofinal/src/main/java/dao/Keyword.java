package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Keyword extends Abstract<entity.Keyword>{
    private static final long serialVersionUID = 1L;

    public Keyword(){
        super(entity.Keyword.class);
    }
}
