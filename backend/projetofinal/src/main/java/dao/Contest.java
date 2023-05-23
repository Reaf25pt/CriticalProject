package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Contest extends Abstract<entity.Contest>{
    private static final long serialVersionUID = 1L;

    public Contest(){
        super(entity.Contest.class);
    }
}
