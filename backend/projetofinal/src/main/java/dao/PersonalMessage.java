package dao;

import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class PersonalMessage extends Abstract<entity.PersonalMessage>{
    private static final long serialVersionUID = 1L;

    public PersonalMessage(){
        super(entity.PersonalMessage.class);
    }

    public List<entity.User> findListOfContactsOfGivenUser(int id) {
        List<entity.User> list = new ArrayList<>();
        try {
            list = (List<User>) em.createNamedQuery("PersonalMessage.findListOfContactsOfGivenUser").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public List<entity.PersonalMessage> findListOfMessagesOfGivenUser(int id) {
        List<entity.PersonalMessage> list = new ArrayList<>();
        try {
            list = (List<entity.PersonalMessage>) em.createNamedQuery("PersonalMessage.findListOfMessagesOfGivenUser").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }




}
