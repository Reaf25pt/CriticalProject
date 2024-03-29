package dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class Notification extends Abstract<entity.Notification>{
    private static final long serialVersionUID = 1L;

    public Notification(){
        super(entity.Notification.class);
    }


    public List<entity.Notification> findNotificationListByUserId(int userId) {

        List<entity.Notification> notifList = new ArrayList<>();
        try {
            notifList = (List<entity.Notification>) em.createNamedQuery("Notification.findNotificationListByUserId").setParameter("userId", userId).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return notifList;
    }

    public entity.Notification findNotificationByUserIdAndProjectMember(int userId, int projId) {
        entity.Notification ent = null;
        try {
            ent = (entity.Notification) em.createNamedQuery("Notification.findNotificationByUserIdAndProjectMember").setParameter("userId", userId).setParameter("projId", projId)
                    .getSingleResult();
        } catch (NoResultException e) {
            // e.printStackTrace();
            return null;
        }
        return ent;
    }



}
