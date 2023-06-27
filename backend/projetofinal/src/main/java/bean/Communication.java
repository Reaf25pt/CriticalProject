package bean;

import entity.*;
import entity.Project;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import org.jboss.logging.Logger;
import websocket.Notifier;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
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

    public void notifyNewPossibleProjectMember(ProjectMember projMember, Project project, User user, boolean isInvited) {
        // gera notificação para avisar que membro foi convidado: isInvited = true/    se auto-convidou isInvited = false


        if (isInvited) {

            // pessoa foi convidada por gestor. Preciso notificar apenas a pessoa convidada
            Notification notif = new Notification();
            notif.setCreationTime(Date.from(Instant.now()));
            notif.setSeen(false);
            notif.setNeedsInput(true);
            notif.setProjectMember(projMember);
            notif.setMessage("Está convidado (a) a participar no projecto: " + project.getTitle() + " .");
            notif.setMessageEng("You have been invited to participate in the project: " + project.getTitle() + " .");
            notif.setNotificationOwner(user);
            notifDao.persist(notif);
            notifyRealTime(notif, user);
        } else {
            // pessoa "auto-convidou". Preciso notificar todos os gestores do projecto

            List<User> managersList = projMemberDao.findListOfManagersByProjectId(project.getId());
            // n precisa de validar null pq um projecto tem sempre um gestor
            for (User u : managersList) {
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
                notif.setProjectMember(projMember);
                notif.setMessage(user.getFirstName() + " " + user.getLastName() + " tem interesse em participar no projecto: " + project.getTitle() + " . Pode responder ao pedido na página do projecto");
                notif.setMessageEng(user.getFirstName() + " " + user.getLastName() + " is interested in participating in project: " + project.getTitle() + " . You can answer to request in the project page");

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

        if (listTokens != null) {
            dto.Notification notifDto = convertNotifEntToDto(notif);

            for (String t : listTokens) {
// TODO falta testar socket
                Notifier.sendNotification(notifDto, t);
            }

        }
    }

    private dto.Notification convertNotifEntToDto(Notification notif) {
        dto.Notification dto = new dto.Notification();

        dto.setId(notif.getNotificationId());
        dto.setCreationTime(notif.getCreationTime());
        dto.setMessage(notif.getMessage());
        dto.setMessageEng(notif.getMessageEng());
        dto.setSeen(notif.isSeen());
        dto.setNeedsInput(notif.isNeedsInput());
        dto.setRelationId(notif.getProjectMember().getId());

        return dto;
    }


    public List<dto.Notification> getOwnNotificationList(String token) {

        List<dto.Notification> listDto = new ArrayList<>();

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {

            List<Notification> list = notifDao.findNotificationListByUserId(user.getUserId());
            if (list != null) {
                for (Notification n : list) {
                    listDto.add(convertNotifEntToDto(n));
                }
            }
        }
        return listDto;
    }


    public dto.Notification markNotifAsRead(String token, int id) {
        // marca como lida uma notificação que não precise de resposta e pertença ao token
        dto.Notification notifDto = null;

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            Notification notif = notifDao.find(id);
            if (notif != null) {
                // TODO raciocinio correcto ou ir logo buscar se owner for o token
                if (!notif.isNeedsInput() && notif.getNotificationOwner().getUserId() == user.getUserId()) {
                    notif.setSeen(true);
                    notifDao.merge(notif);
                    notifDto = convertNotifEntToDto(notif);
                }
            }
        }
        return notifDto;
    }

    public boolean deleteNotif(String token, int id) {
        // apaga notificação que pertença ao token, se n precisar de resposta
        boolean res = false;

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            Notification notif = notifDao.find(id);
            if (notif != null) {
                if (!notif.isNeedsInput() && notif.getNotificationOwner().getUserId() == user.getUserId()) {

                    notifDao.remove(notif);
                    res = true;
                }
            }
        }

        return res;
    }

    public dto.Notification answerInvitation(String token, int notifId, int answer) {
        // user responde a convite enviado por notificação. answer == 0 -> false REFUSE INVITE / answer == 1 -> true ACCEPT INVITE
        // método só pode ser usado por token, pq é o dono da notificação
        dto.Notification notifDto = null;

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            Notification notif = notifDao.find(notifId);
            if (notif != null) {
                if (notif.getNotificationOwner().getUserId() == user.getUserId()) {
                    // tem de marcar notif como lida e já n precisa de input
                    // tem de ir buscar projMember a que convite diz respeito para alterar os dados em conformidade com a resposta

                    ProjectMember projMember = notif.getProjectMember();
                    projMember.setAnswered(true);


                    notif.setSeen(true);
                    notif.setNeedsInput(false);
                    if (answer == 0) {
                        // recusar convite para participar no projecto

                        projMember.setAccepted(false);

                    } else if (answer == 1) {
                        // aceitar convite para participar no projecto
                        projMember.setAccepted(true);
                    }
                    notifDao.merge(notif);
                    projMemberDao.merge(projMember);

                    notifyRelevantPartsOfInvitationResponse(projMember, answer);

                    if(answer==1){
                        // convite aceite. Tem de verificar se vagas do projecto foi atingido. Se sim, é preciso recusar os restantes convites pendentes para o projecto em causa
                   // TODO verificar limite vagas do projecto e recusar os outros ou verificar à entrada e não deixar botões disponvieis no frontend se n der pra adicionar mais membros?
                    }

                    notifDto = convertNotifEntToDto(notif);

                }
            }
        }

        return notifDto;
    }

    private void notifyRelevantPartsOfInvitationResponse(ProjectMember projMember, int answer) {
        // avisa pessoas relevantes se convite foi ou não aceite pelo USER convidado: avisa todos os membros do projecto se convite foi aceite ou apenas os gestores se convite foi recusado
        // answer == 0 -> false REFUSE INVITE / answer == 1 -> true ACCEPT INVITE

        if (answer == 0) {
            // recusou convite para participar no projecto. Avisar apenas os gestores do projecto

            List<User> managersList = projMemberDao.findListOfManagersByProjectId(projMember.getProjectToParticipate().getId());
            if (managersList != null) {
                for (User u : managersList) {
                    Notification notif = new Notification();
                    notif.setCreationTime(Date.from(Instant.now()));
                    notif.setSeen(false);
                    notif.setNeedsInput(false);
                    notif.setProjectMember(projMember);
                    notif.setMessage(projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() + " recusou o convite para participar no projecto " + projMember.getProjectToParticipate().getTitle());
                    notif.setMessageEng(projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() + " has refused  to participate in the project " + projMember.getProjectToParticipate().getTitle());
                    notif.setNotificationOwner(u);
                    notifDao.persist(notif);
                    notifyRealTime(notif, u);
                }
            }
        } else if (answer == 1) {
            // aceitou convite para participar no projecto. Avisar todos os membros
            List<User> projectMembersList = projMemberDao.findListOfUsersByProjectId(projMember.getProjectToParticipate().getId());
            if (projectMembersList != null) {
                for (User u : projectMembersList) {
                    Notification notif = new Notification();
                    notif.setCreationTime(Date.from(Instant.now()));
                    notif.setSeen(false);
                    notif.setNeedsInput(false);
                    notif.setProjectMember(projMember);
                    notif.setMessage(projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() + " aceitou o convite para participar no projecto " + projMember.getProjectToParticipate().getTitle());
                    notif.setMessageEng(projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() + " has accepted  to participate in the project " + projMember.getProjectToParticipate().getTitle());
                    notif.setNotificationOwner(u);
                    notifDao.persist(notif);
                    notifyRealTime(notif, u);
                }
            }
        }
               /* notif.setMessage("O pedido de " + projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() +" para participar no projecto " + projMember.getProjectToParticipate().getTitle() + " foi recusado");
                notif.setMessageEng("Self-invite of "+ projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() +" to participate in the project " + projMember.getProjectToParticipate().getTitle() + " has been refused");


            } else if (answer == 1){
                // auto-convite aceite
                notif.setMessage("O pedido de " + projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() +" para participar no projecto " + projMember.getProjectToParticipate().getTitle() + " foi aceite");
                notif.setMessageEng("Self-invite of "+ projMember.getUserInvited().getFirstName() + " " + projMember.getUserInvited().getLastName() +" to participate in the project " + projMember.getProjectToParticipate().getTitle() + " has been accepted");
*/


    }
}