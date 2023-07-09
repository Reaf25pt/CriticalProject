package bean;

import dto.ProjectChat;
import dto.UserInfo;
import entity.*;
import entity.Contest;
import entity.Project;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.jws.soap.SOAPBinding;
import org.jboss.logging.Logger;
import websocket.Notifier;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
    @EJB
    dao.User userDao;
    @EJB
    dao.ContestApplication applicationDao;
    @EJB
    dao.ProjectHistory recordDao;
    @EJB
    dao.PersonalMessage personalChatDao;

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

    /**
     * Sends notification in real-time, through websocket, to sessions that might be available. Sessions are identified by token
     * @param notif contains notification information
     * @param user identifies user that should be notified in real-time
     */
    private void notifyRealTime(Notification notif, User user) {
        List<String> listTokens = tokenDao.findTokenListByUserId(user.getUserId());

        if (listTokens != null) {
            dto.Notification notifDto = convertNotifEntToDto(notif);

            for (String t : listTokens) {

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
        if(notif.getProjectMember()!=null) {
            dto.setRelationId(notif.getProjectMember().getId());
        }
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
                Collections.reverse(listDto);
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
        System.out.println("answer metodo"+answer);

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            Notification notif = notifDao.find(notifId);
            if (notif != null) {
                if (notif.getNotificationOwner().getUserId() == user.getUserId()) {
                    // tem de marcar notif como lida e já n precisa de input
                    // tem de ir buscar projMember a que convite diz respeito para alterar os dados em conformidade com a resposta

                    ProjectMember projMember = notif.getProjectMember();
                    projMember.setAnswered(true);
                    System.out.println("answer metodo para colocar set"+answer);

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
                    recordMemberInvitationResponse(user, projMember.getProjectToParticipate(), answer);


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

    }

    public void notifyProjectMembersOfApplicationResponse(Project project, int answer) {
        // Notifica membros do projecto se candidatura a concurso foi aceite (status = 1) ou rejeitada (status =0)

        List<User> membersList = projMemberDao.findListOfUsersByProjectId(project.getId());

        if (membersList != null) {
            for(User u : membersList){
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
               // notif.setProjectMember(projMember);
            if (answer == 0) {
                // projecto rejeitado a concurso
                notif.setMessage( "A candidatura a concurso do projecto " + project.getTitle() + " foi recusada");
                notif.setMessageEng("Application for contest of project "+ project.getTitle() + " has been refused");


            } else if (answer == 1) {
                // projecto aceite a concurso
                notif.setMessage( "A candidatura a concurso do projecto " + project.getTitle() + " foi aceite. A execução do projecto pode avançar assim que o concurso abrir a fase de execução");
                notif.setMessageEng("Application for contest of project "+ project.getTitle() + " has been accepted. Project development can start once contest execution phase opens ");

            }
                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);

            }
        }

    }

    public void notifyProjectMembersOfMemberLeavingProject(Project project, User user) {
        // Notifica membros do projecto que membro user saiu do projecto
        // TODO talvez seja redundante com record. Pode-se remover notificação

        List<User> membersList = projMemberDao.findListOfUsersByProjectId(project.getId());

        if (membersList != null) {
            for(User u : membersList){
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
                // notif.setProjectMember(projMember);

                    notif.setMessage(user.getFirstName() + " " + user.getLastName() + " saiu do projecto " + project.getTitle());
                    notif.setMessageEng(user.getFirstName() +" " + user.getLastName() + " has left project "+ project.getTitle());

                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);

            }
        }

    }

    /**
     * Notifies user that it is responsible for given task
     * @param user identifies user responsible for task
     * @param taskTitle is the name of the task
     */
    public void notifyNewOwnerOfTask(User user, String taskTitle) {

        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        // notif.setProjectMember(projMember);

        notif.setMessage("Tem uma nova tarefa à sua responsabilidade: " + taskTitle );
        notif.setMessageEng("You have been designated responsible for task: " + taskTitle );

        notif.setNotificationOwner(user);
        notifDao.persist(notif);
        notifyRealTime(notif, user);
    }

    public void notifyTaskWasRemoved(User user, String taskTitle) {
        // notifica que tarefa à responsabilidade do owner foi removida

        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        // notif.setProjectMember(projMember);

        notif.setMessage("A tarefa " + taskTitle + ", à sua responsabilidade, foi apagada" );
        notif.setMessageEng("Task " + taskTitle + ", of which you were responsible, has been deleted" );

        notif.setNotificationOwner(user);
        notifDao.persist(notif);
        notifyRealTime(notif, user);
    }

    public void notifyAllMembersTaskIsFinished(Task taskEnt) {
        // notifica todos os membros que tarefa foi concluída

        List<User> members = projMemberDao.findListOfUsersByProjectId(taskEnt.getProject().getId());
        if(members!=null) {
            for (User u : members){

            Notification notif = new Notification();
            notif.setCreationTime(Date.from(Instant.now()));
            notif.setSeen(false);
            notif.setNeedsInput(false);
            // notif.setProjectMember(projMember);

            notif.setMessage("A tarefa " + taskEnt.getTitle() + " está concluída");
            notif.setMessageEng("Task " + taskEnt.getTitle() + " has been completed");

            notif.setNotificationOwner(u);
            notifDao.persist(notif);
            notifyRealTime(notif, u);
        }}
    }

    public void notifyAllContestManagers(int value, String contestTitle) {
        // notifica todos os gestores de concurso de acontecimento relevante, de acordo com value

        List<User> contestManagersList = userDao.findListContestManagers();

        if(contestManagersList!=null){
            for (User u : contestManagersList){
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
                // notif.setProjectMember(projMember);
if (value==0){
    // novo concurso criado
    notif.setMessage("Um novo concurso foi criado: "+ contestTitle);
    notif.setMessageEng("A new contest has been created: " + contestTitle);
} else if (value==1){
    // info de concurso foi editada
    notif.setMessage("Informação do concurso " + contestTitle + " foi editada");
    notif.setMessageEng("Details of contest " + contestTitle + "have been edited");
} else if(value==2){
    // projecto concorreu a concurso
    notif.setMessage("Nova candidatura recebida para o concurso " + contestTitle);
    notif.setMessageEng("There is a new application for contest " + contestTitle );
}


                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);
            }
        }

    }

    public void notifyAllUsers(Contest contest) {
        //notifica todos os utilizadores que concurso iniciou a fase de candidaturas
        List<User> allUsers= userDao.findAllUsersWithValidatedAccount();

        if(allUsers!=null){
            for (User u : allUsers){
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
                // notif.setProjectMember(projMember);

                notif.setMessage("O concurso " + contest.getTitle() + " abriu a fase de candidaturas");
                notif.setMessageEng("Contest " + contest.getTitle() + " has opened to project applications");

                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);
            }
        }


    }

    public void notifyProjectMembersExecutionHasStarted(Contest contest) {
        // notifica todos os membros de projectos aceites num concurso que a fase ongoing começou. A execução dos projectos pode avançar
// TODO mesmo que projecto esteja cancelado ?!

        List<Project> acceptedProjects = applicationDao.findAcceptedProjectsForGivenContestId(contest.getId());

        if(acceptedProjects!=null){
            for(Project p : acceptedProjects){
                // encontrar lista de membros activos e not removed para serem notificados
                List<User> members = projMemberDao.findListOfUsersByProjectId(p.getId());

                if(members!=null){
                    for (User u : members){
                        Notification notif = new Notification();
                        notif.setCreationTime(Date.from(Instant.now()));
                        notif.setSeen(false);
                        notif.setNeedsInput(false);
                        // notif.setProjectMember(projMember);

                        notif.setMessage("O concurso " + contest.getTitle() + " abriu a fase de execução dos projectos. Já pode iniciar a execução do seu projecto");
                        notif.setMessageEng("Contest " + contest.getTitle() + " has started execution phase. You can now start execution of your project");

                        notif.setNotificationOwner(u);
                        notifDao.persist(notif);
                        notifyRealTime(notif, u);
                    }
                }


            }
        }


    }

    public void notifyContestHasFinished(Contest contest) {
        // notifica membros de projectos envolvidos num concurso que concurso acabou e avisa qual o vencedor
        List<Project> acceptedProjects = applicationDao.findAcceptedProjectsForGivenContestId(contest.getId());

        if(acceptedProjects!=null){
            for(Project p : acceptedProjects){
                // encontrar lista de membros activos e not removed para serem notificados
                List<User> members = projMemberDao.findListOfUsersByProjectId(p.getId());

                if(members!=null){
                    for (User u : members) {
                        Notification notif = new Notification();
                        notif.setCreationTime(Date.from(Instant.now()));
                        notif.setSeen(false);
                        notif.setNeedsInput(false);
                        // notif.setProjectMember(projMember);

                        notif.setMessage("O concurso " + contest.getTitle() + " está concluído. O projecto vencedor é " + contest.getWinner().getTitle());
                        notif.setMessageEng("Contest " + contest.getTitle() + " has finished. Contest winner is "+  contest.getWinner().getTitle());

                        notif.setNotificationOwner(u);
                        notifDao.persist(notif);
                        notifyRealTime(notif, u);



                    }}}
                    }
    }


    public void notifyContestHasWinner(Contest contest) {
        // notifica membros de projectos envolvidos num concurso que concurso já tem vencedor
        List<Project> acceptedProjects = applicationDao.findAcceptedProjectsForGivenContestId(contest.getId());

        if(acceptedProjects!=null){
            for(Project p : acceptedProjects){
                // encontrar lista de membros activos e not removed para serem notificados
                List<User> members = projMemberDao.findListOfUsersByProjectId(p.getId());

                if(members!=null){
                    for (User u : members) {
                        Notification notif = new Notification();
                        notif.setCreationTime(Date.from(Instant.now()));
                        notif.setSeen(false);
                        notif.setNeedsInput(false);
                        // notif.setProjectMember(projMember);

                        notif.setMessage("O projecto vencedor do concurso "  + contest.getTitle() + " é " + contest.getWinner().getTitle());
                        notif.setMessageEng("Project "  + contest.getWinner().getTitle()+ " has been declares winner of contest " + contest.getTitle() );

                        notif.setNotificationOwner(u);
                        notifDao.persist(notif);
                        notifyRealTime(notif, u);



                    }}}
        }
    }


    public void recordProjectCreation(Project newProjEnt, User user) {
        // guarda registo no histórico do projecto da sua criação

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setMessage("Projecto criado: " + newProjEnt.getTitle()+". Estado: Planning");
        record.setAuthor(user);
record.setProject(newProjEnt);
        recordDao.persist(record);
    }

    public void recordProjectEdition(Project project, User user) {
        // guarda registo no histórico do projecto da sua edição

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setMessage("Informação geral editada");
        record.setAuthor(user);
        record.setProject(project);
        recordDao.persist(record);
    }

    public void recordProjectStatusChange(Project project, User user, int status) {
        // guarda registo no histórico do projecto da mudança de status

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        if(user!=null){
        record.setAuthor(user);}
        record.setProject(project);

        switch (status){
            case 0:
                record.setMessage("Estado do projecto: Planning");
                break;
            case 1:
                record.setMessage("Estado do projecto: Ready");
                break;
            case 2:
                record.setMessage("Estado do projecto: Proposed to contest");
                break;
            case 3:
                record.setMessage("Estado do projecto: Approved to contest");
                break;
            case 4:
                record.setMessage("Estado do projecto: In progress");
                break;
            case 5:
                record.setMessage("Estado do projecto: Cancelled");
                break;
            case 6:
                record.setMessage("Estado do projecto: Finished");
                break;
            case 7:
                record.setMessage("O projecto foi re-activado. Estado: Planning");
                break;
        }
        recordDao.persist(record);
    }

    public void recordTaskStatusEdit(User user, Task task, int status) {
        // guarda registo no histórico do projecto da mudança de status de tarefa

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);
record.setProject(task.getProject());

        switch (status) {
            case 1:
                record.setMessage("Estado do tarefa "+task.getTitle()+": In progress");
                break;
            case 2:
                record.setMessage("Estado do tarefa "+task.getTitle()+": Finished");
                break;
        }
        recordDao.persist(record);
    }

    public void recordMemberInvitationResponse(User user, Project project, int answer) {
        // guarda registo no histórico do projecto da resposta a um convite para participar no projecto

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);
        record.setProject(project);

        switch (answer) {
            case 0:
                //recusou convite para participar no projecto
                record.setMessage(user.getFirstName() +" " + user.getLastName() + " recusou o convite para participar no projecto");
                break;
            case 1:
                //aceitou convite para participar no projecto
                record.setMessage(user.getFirstName() +" " + user.getLastName() + " aceitou o convite para participar no projecto");
                break;
        }
        recordDao.persist(record);
    }

    public void recordMemberRemovalFromProject(User user, Project project) {
        // guarda registo no histórico do projecto da saída de um membro do projecto

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);  // TODO diferenciar quem remove ?
        record.setProject(project);
        record.setMessage(user.getFirstName() +" " + user.getLastName() + " saiu do projecto");

        recordDao.persist(record);
    }

    public void recordManagerResponseToSelfInvitation(User manager, User user,Project project, int answer) {
        // guarda registo no histórico do projecto da resposta que gestor deu a um pedido para participar no projecto
        // manager do projecto é author da resposta ao pedido. user é a pessoa que pediu para participar

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(manager);
        record.setProject(project);

        switch (answer) {
            case 0:
                //recusou pedido para participar no projecto
                record.setMessage("Pedido de " +user.getFirstName() +" " + user.getLastName() + " para participar no projecto foi recusado");
                break;
            case 1:
                //aceitou pedido para participar no projecto
                record.setMessage("Pedido de " +user.getFirstName() +" " + user.getLastName() + " para participar no projecto foi aceite");
                break;
        }
        recordDao.persist(record);
    }


    public void notifyPotentialMemberOfSelfInvitationResponse(ProjectMember pm, int answer) {
        // notifica user que pedido para participar em projecto foi aceite (1) ou recusado (0)

        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        notif.setProjectMember(pm);

        if(answer==0){
            // pedido recusado
            notif.setMessage("O pedido de " + pm.getUserInvited().getFirstName() + " " + pm.getUserInvited().getLastName() +" para participar no projecto " + pm.getProjectToParticipate().getTitle() + " foi recusado");
            notif.setMessageEng("Self-invite of "+ pm.getUserInvited().getFirstName() + " " + pm.getUserInvited().getLastName() +" to participate in the project " + pm.getProjectToParticipate().getTitle() + " has been refused");


        } else if (answer==1){
            // pedido aceite
            notif.setMessage("O pedido de " + pm.getUserInvited().getFirstName() + " " + pm.getUserInvited().getLastName() +" para participar no projecto " + pm.getProjectToParticipate().getTitle() + " foi aceite");
            notif.setMessageEng("Self-invite of "+ pm.getUserInvited().getFirstName() + " " + pm.getUserInvited().getLastName() +" to participate in the project " + pm.getProjectToParticipate().getTitle() + " has been accepted");

        }

        notif.setNotificationOwner(pm.getUserInvited());
        notifDao.persist(notif);
        notifyRealTime(notif, pm.getUserInvited());
    }

    public void recordProjectApplicationResult(User user, Project project, int answer) {
        // guarda registo no histórico do projecto de ser aceite (1) / rejeitado (0) a concurso
        //TODO faz sentido identificar o gestor de concurso, autor da decisão?
        System.out.println("record application response");
        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        if(user!=null){
        record.setAuthor(user);}
        record.setProject(project);

        if(answer == 0){
            //rejeitado
            record.setMessage("A candidatura para participar no concurso foi rejeitada");

        }else if(answer==1){
            //aceite
            record.setMessage("A candidatura para participar no concurso foi aceite");

        }

        System.out.println("FIM record application response");

        recordDao.persist(record);
    }

    public void recordProjectDeclaredWinner(User user, Project project, Contest contest) {
        // guarda registo no histórico do projecto ser declarado vencedor do concurso
        //TODO faz sentido identificar o gestor de concurso, autor da decisão?
        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);
        record.setProject(project);

            record.setMessage("O projecto foi declarado vencedor no concurso " + contest.getTitle());

        recordDao.persist(record);
    }


    public List<UserInfo> getContactsList(String token, int idToChat) {
        // obter contactos (lista de utilizadores) com quem o token tem mensagens pessoais trocadas
        // ir buscar todos os message sender de mensagens cujo message receiver seja o token, e vice-versa
// passa valor de query param (geralmente String ?). Se for 0 não faz nada extra. Se for != 0 tem de adicionar a pessoa
        List<UserInfo> contactsList = new ArrayList<>();
        User user = tokenDao.findUserEntByToken(token);
Set<User> mergeSet = new HashSet<>();

        if (user != null) {
            List<User> sendersList = personalChatDao.findListOfSenderContactsOfGivenUser(user.getUserId());
            List<User> receiversList = personalChatDao.findListOfReceiverContactsOfGivenUser(user.getUserId());

            if(sendersList!=null){
                mergeSet.addAll(sendersList);
            }

            if(receiversList!=null){
                mergeSet.addAll(receiversList);
            }

            List <User> mergeList = new ArrayList<>(mergeSet);
            List<entity.User> tempList = mergeList.stream().filter(userE -> userE.getUserId() != idToChat).collect(Collectors.toList());
// retira o id do user para garantir que só aparecerá 1x na lista

            if (tempList != null) {
                for (User u : tempList) {

                    UserInfo minimalUser = new UserInfo();
                    minimalUser.setId(u.getUserId());
                    minimalUser.setFirstName(u.getFirstName());
                    minimalUser.setLastName(u.getLastName());
                    minimalUser.setNickname(u.getNickname());
                    minimalUser.setPhoto(u.getPhoto());
                    minimalUser.setOpenProfile(u.isOpenProfile());

                    contactsList.add(minimalUser);
                }
            }



            if(idToChat!=0) {
                System.out.println("NAO ZERO");
                // tem de ir buscar contacto com id
                User newContact = userDao.findUserById(idToChat);

                if(newContact!=null){

                    UserInfo minimalUser = new UserInfo();
                    minimalUser.setId(newContact.getUserId());
                    minimalUser.setFirstName(newContact.getFirstName());
                    minimalUser.setLastName(newContact.getLastName());
                    minimalUser.setNickname(newContact.getNickname());
                    minimalUser.setPhoto(newContact.getPhoto());
                    minimalUser.setOpenProfile(newContact.isOpenProfile());

                    contactsList.add(minimalUser);
                }

            }
        }

        return contactsList;
    }

    public List<dto.PersonalMessage> getAllPersonalMessages(String token) {
        // obter lista de todas as mensagens de token

List<dto.PersonalMessage> listDto = new ArrayList<>();

        User user = tokenDao.findUserEntByToken(token);

        if(user!=null) {
List<PersonalMessage> allTokenMessages = personalChatDao.findListOfMessagesOfGivenUser(user.getUserId());

if(allTokenMessages!=null){
    for (PersonalMessage m:allTokenMessages){
        dto.PersonalMessage dto = new dto.PersonalMessage();
        dto.setId(m.getPersonalMessageId());
        dto.setMessage(m.getMessage());
        dto.setSeen(m.isSeen());
        dto.setUserSenderId(m.getMessageSender().getUserId());
        dto.setUserReceiverId(m.getMessageReceiver().getUserId());
        dto.setCreationTime(m.getCreationTime());

        listDto.add(dto);
    }
}
        }

        return listDto;

    }


    public void notifyProjectChatRealTime(ProjectChat message, Project project) {
        // envia mensagem em tempo-real por chat do projecto a todos os tokens cujo user seja membro activo do projecto

        List<User> membersList = projMemberDao.findListOfUsersByProjectId(project.getId());

        if(membersList!=null){
            for( User u:membersList){
                List<String> listTokens = tokenDao.findTokenListByUserId(u.getUserId());
                if (listTokens != null) {
                    for (String t : listTokens) {
// TODO falta testar socket
                        websocket.ProjectChat.sendNotification(message, t);
                    }

            }
        }
    }
}


    public boolean markMessagesRead(String token, int contactId) {
        // marca como lidas todas as mensagens trocadas entre token e contactId, quando selecciona este contacto para mandar msg

        boolean res=false;

        User user = tokenDao.findUserEntByToken(token);

        if(user!=null) {
            List<PersonalMessage> messagesEnt = personalChatDao.findListOfReceivedMessagesOfGivenUserSentByContactId(user.getUserId(), contactId);

            if(messagesEnt!=null){
                for (PersonalMessage m: messagesEnt){
                    m.setSeen(true);
                    personalChatDao.merge(m);
                }
                res=true;
            }
        }
        return res;
    }

    public List<dto.PersonalMessage> getMessagesForSpecificContact(String token, int contactId) {
        List<dto.PersonalMessage> list=new ArrayList<>();

        User user = tokenDao.findUserEntByToken(token);

        if(user!=null) {
            List<PersonalMessage> messagesEnt = personalChatDao.findListOfExchangedMessagesBetweenTwoContacts(user.getUserId(), contactId);
if(messagesEnt!=null){
    for(PersonalMessage m: messagesEnt){
        list.add(convertPersonalMessageEntToDto(m));
    }
}
        }


        return list;
    }

    private dto.PersonalMessage convertPersonalMessageEntToDto(PersonalMessage m) {
        dto.PersonalMessage dto = new dto.PersonalMessage();

        dto.setId(m.getPersonalMessageId());
        dto.setCreationTime(m.getCreationTime());
        dto.setMessage(m.getMessage());
        dto.setSeen(m.isSeen());
        dto.setUserSenderId(m.getMessageSender().getUserId());
        dto.setUserReceiverId(m.getMessageReceiver().getUserId());

        return dto;
    }

    public dto.PersonalMessage sendMessageToContact(dto.PersonalMessage message, String token) {
        // enviar nova mensagem pessoal para contacto. Notificar em real-time por socket o receiver da mensagem, caso tenha alguma sessão activa
        dto.PersonalMessage dto = new dto.PersonalMessage();
        User user = tokenDao.findUserEntByToken(token);

        if(user!=null) {
User receiver=userDao.find(message.getUserReceiverId());
if(receiver!=null){
    PersonalMessage messageEnt = new PersonalMessage();
    messageEnt.setSeen(false);
    messageEnt.setMessage(message.getMessage());
    messageEnt.setCreationTime(Date.from(Instant.now()));
    messageEnt.setMessageSender(user);
    messageEnt.setMessageReceiver(receiver);

    personalChatDao.persist(messageEnt);




    dto=convertPersonalMessageEntToDto(messageEnt);

    List<String> listTokens = tokenDao.findTokenListByUserId(receiver.getUserId());
    if (listTokens != null) {
        for (String t : listTokens) {
// TODO falta testar socket
            websocket.PersonalChat.sendNotification(dto, t);
        }
    }
}

        }
        return dto;
    }
}