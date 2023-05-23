package dao;

import jakarta.ejb.Stateless;

@Stateless
public class PersonalMessage extends Abstract<entity.PersonalMessage>{
    private static final long serialVersionUID = 1L;

    public PersonalMessage(){
        super(entity.PersonalMessage.class);
    }
}
