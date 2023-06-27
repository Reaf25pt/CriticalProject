package dao;

import entity.Hobby;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class User extends Abstract<entity.User> {
    private static final long serialVersionUID = 1L;

    public User() {
        super(entity.User.class);
    }


    public entity.User findUserByEmail(String email) {
        entity.User ent = null;
        try {
            ent = (entity.User) em.createNamedQuery("User.findUserByEmail").setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }


    public entity.User findUserByTokenForActivationOrRecoverPass(String token) {
        entity.User ent = null;
        try {
            ent = (entity.User) em.createNamedQuery("User.findUserByTokenForActivationOrRecoverPass").setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }

    public entity.User findUserById(int userId) {
        entity.User ent = null;
        try {
            ent = (entity.User) em.createNamedQuery("User.findUserById").setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }

    public List<entity.User> findUserContainingStr(String str) {
        List<entity.User> usersList = new ArrayList<>();
        try {
            usersList = (List<entity.User>) em.createNamedQuery("User.findUserContainingStr").setParameter("str", "%"+ str.toLowerCase()+"%").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return usersList;
    }

    public List<entity.User> findListContestManagers() {
        List<entity.User> usersList = new ArrayList<>();
        try {
            usersList = (List<entity.User>) em.createNamedQuery("User.findListContestManagers").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return usersList;
    }

    public List<entity.User> findAllUsersWithValidatedAccount() {
        List<entity.User> usersList = new ArrayList<>();
        try {
            usersList = (List<entity.User>) em.createNamedQuery("User.findAllUsersWithValidatedAccount").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return usersList;
    }



}
