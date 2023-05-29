package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Token extends Abstract<entity.Token>{
    private static final long serialVersionUID = 1L;

    public Token(){
        super(entity.Token.class);
    }


    public entity.Token findTokenEntByToken(String token) {
        // to access user info
        entity.Token tEnt = null;
        try {

            tEnt = (entity.Token) em.createNamedQuery("Token.findTokenEntByToken").setParameter("token", token)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        }
        return tEnt;

    }

    public entity.User findUserEntByToken(String token) {
        // to access user info
        entity.User uEnt = null;
        try {

            uEnt = (entity.User) em.createNamedQuery("Token.findUserEntByToken").setParameter("token", token)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        }
        return uEnt;

    }

}
