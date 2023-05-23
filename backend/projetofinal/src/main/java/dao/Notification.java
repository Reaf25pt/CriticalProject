package dao;

import jakarta.ejb.Stateless;

@Stateless
public class Notification extends Abstract<entity.Notification>{
    private static final long serialVersionUID = 1L;

    public Notification(){
        super(entity.Notification.class);
    }
}
