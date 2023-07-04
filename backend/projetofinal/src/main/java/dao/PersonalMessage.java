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

/*    public List<entity.PersonalMessage> findListOfContactsOfGivenUser(int id) {
        List<entity.PersonalMessage> list = new ArrayList<>();
        try {
            list = (List<entity.PersonalMessage>) em.createNamedQuery("PersonalMessage.findListOfContactsOfGivenUser").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }*/

    public List<entity.User> findListOfReceiverContactsOfGivenUser(int id) {
        List<entity.User> list = new ArrayList<>();
        try {
            list = (List<entity.User>) em.createNamedQuery("PersonalMessage.findListOfReceiverContactsOfGivenUser").setParameter("id", id).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
    public List<entity.User> findListOfSenderContactsOfGivenUser(int id) {
        List<entity.User> list = new ArrayList<>();
        try {
            list = (List<entity.User>) em.createNamedQuery("PersonalMessage.findListOfSenderContactsOfGivenUser").setParameter("id", id).getResultList();
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

    public List<entity.PersonalMessage> findListOfReceivedMessagesOfGivenUserSentByContactId(int userId, int contactId) {
        List<entity.PersonalMessage> list = new ArrayList<>();
        try {
            list = (List<entity.PersonalMessage>) em.createNamedQuery("PersonalMessage.findListOfReceivedMessagesOfGivenUserSentByContactId").setParameter("userId", userId).setParameter("contactId", contactId).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public List<entity.PersonalMessage> findListOfExchangedMessagesBetweenTwoContacts(int userId, int contactId) {
        List<entity.PersonalMessage> list = new ArrayList<>();
        try {
            list = (List<entity.PersonalMessage>) em.createNamedQuery("PersonalMessage.findListOfExchangedMessagesBetweenTwoContacts").setParameter("userId", userId).setParameter("contactId", contactId).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }




}
