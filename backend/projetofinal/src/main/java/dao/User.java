package dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

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

}
