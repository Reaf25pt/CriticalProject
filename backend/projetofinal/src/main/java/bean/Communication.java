package bean;

import entity.Notification;
import entity.Project;
import entity.Token;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import org.jboss.logging.Logger;
import websocket.Notifier;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RequestScoped
public class Communication implements Serializable {
    // inclui tudo o que diga respeito a comunicação: mensagens, notificações e sockets
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Communication.class);

    @EJB
    dao.Notification notifDao;
    @EJB
    dao.ProjectMember projMemberDao;
    @EJB
    dao.Token tokenDao;

    public void notifyNewPossibleProjectMember(int relationId, Project project, User user, boolean isInvited) {
        // gera notificação para avisar que membro foi convidado: isInvited = true/se auto-convidou isInvited = false

        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(true);
        notif.setRelationId(relationId);
        if (isInvited) {
            // pessoa foi convidada por gestor. Preciso notificar apenas a pessoa convidada
            notif.setMessage("Está convidado (a) a participar no projecto: " + project.getTitle() + " .");
            notif.setMessageEng("You have been invited to participate in the project: " + project.getTitle() + " .");
            notif.setNotificationOwner(user);
            notifDao.persist(notif);
            notifyRealTime(notif, user);
        } else {
            // pessoa "auto-convidou". Preciso notificar todos os gestores do projecto
            notif.setMessage(user.getFirstName() + " " + user.getLastName() + " tem interesse em participar no projecto: " + project.getTitle() + " .");
            notif.setMessageEng(user.getFirstName() + " " + user.getLastName() + " is interested in participating in project: " + project.getTitle() + " .");

            List<User> managersList = projMemberDao.findListOfManagersByProjectId(project.getId());
// n precisa de validar null pq um projecto tem sempre um gestor
            for (User u : managersList) {
                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);

            }

        }
        /* TODO  java.sql.Date date=new java.sql.Date(System.currentTimeMillis()); */
        // System.out.println("Current Date: "+date);
    }

    private void notifyRealTime(Notification notif, User user) {
        // envia notificação em tempo-real por socket a todos os tokens cujo user seja igual ao parâmetro user

        List<String> listTokens = tokenDao.findTokenListByUserId(user.getUserId());

        if(listTokens!= null){
        dto.Notification notifDto = convertNotifEntToDto(notif);

        for (String t : listTokens) {
// TODO falta testar socket
            Notifier.sendNotification(notifDto, t);
        }

    }}

    private dto.Notification convertNotifEntToDto(Notification notif) {
        dto.Notification dto = new dto.Notification();

        dto.setId(notif.getNotificationId());
        dto.setCreationTime(notif.getCreationTime());
        dto.setMessage(notif.getMessage());
        dto.setMessageEng(notif.getMessageEng());
        dto.setSeen(notif.isSeen());
        dto.setNeedsInput(notif.isNeedsInput());
        dto.setRelationId(notif.getRelationId());

        return dto;
    }


}
