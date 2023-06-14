package dao;

import entity.Hobby;
import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class Keyword extends Abstract<entity.Keyword>{
    private static final long serialVersionUID = 1L;

    public Keyword(){
        super(entity.Keyword.class);
    }

    public entity.Keyword findKeywordByTitle(String title) {
        entity.Keyword ent = null;
        try {
            ent = (entity.Keyword) em.createNamedQuery("Keyword.findKeywordByTitle").setParameter("title", title.toLowerCase())
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }

    public List<entity.Keyword> findKeywordListContainingStr(String str) {
        List<entity.Keyword> keywordsList = new ArrayList<>();
        try {
            keywordsList = (List<entity.Keyword>) em.createNamedQuery("Keyword.findKeywordListContainingStr").setParameter("str", "%"+ str.toLowerCase()+"%").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return keywordsList;
    }



}
