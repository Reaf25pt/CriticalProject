package dao;

import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> findTokenListByUserId(int userId) {

        List<String> tokenList = new ArrayList<String>();
        try {
            tokenList = (List<String>) em.createNamedQuery("Token.findTokenListByUserId").setParameter("userId", userId).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return tokenList;
    }





}
