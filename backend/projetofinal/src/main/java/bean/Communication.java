package bean;

import dto.ProjectChat;
import dto.UserInfo;
import entity.*;
import entity.Contest;
import entity.Project;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.jws.soap.SOAPBinding;
import org.jboss.logging.Logger;
import websocket.Notifier;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
    @Inject
    bean.User userBean;
    @EJB
    dao.Project projDao;
    @EJB
    dao.Contest contestDao;

    /**
     * Notifies user that has been invited to participate in project
     *
     * @param projMember represents ProjectMember relationship between user and project
     * @param project    represents project
     * @param user       represents user invited to participate in project
     * @param isInvited  is true if user is invited by project manager; is false if it's a self-invitation
     */
    public void notifyNewPossibleProjectMember(ProjectMember projMember, Project project, User user, boolean isInvited) {

        if (isInvited) {
            // pessoa foi convidada por gestor. Preciso notificar apenas a pessoa convidada
            Notification notif = new Notification();
            notif.setCreationTime(Date.from(Instant.now()));
            notif.setSeen(false);
            notif.setNeedsInput(true);
            notif.setProjectMember(projMember);
            notif.setMessage("Está convidado (a) a participar no projecto: " + project.getTitle() + ".");
            notif.setMessageEng("You have been invited to participate in the project: " + project.getTitle() + ".");
            notif.setNotificationOwner(user);
            notifDao.persist(notif);
            notifyRealTime(notif, user);
        } else {
            // pessoa "auto-convidou". Preciso notificar todos os gestores do projecto

            List<User> managersList = projMemberDao.findListOfManagersByProjectId(project.getId());
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
    }

    /**
     * Sends notification in real-time, through websocket, to sessions that might be available. Sessions are identified by token
     *
     * @param notif contains notification information
     * @param user  identifies user that should be notified in real-time
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

    /**
     * Converts Notification entity to Notification DTO
     *
     * @param notif represents notification entity
     * @return Notification DTO
     */
    private dto.Notification convertNotifEntToDto(Notification notif) {
        dto.Notification dto = new dto.Notification();

        dto.setId(notif.getNotificationId());
        dto.setCreationTime(notif.getCreationTime());
        dto.setMessage(notif.getMessage());
        dto.setMessageEng(notif.getMessageEng());
        dto.setSeen(notif.isSeen());
        dto.setNeedsInput(notif.isNeedsInput());
        if (notif.getProjectMember() != null) {
            dto.setRelationId(notif.getProjectMember().getId());
        }
        return dto;
    }

    /**
     * Gets list of token notifications
     * Reverses list to display most recent ones at the top
     *
     * @param token identifies session that makes the request
     * @return list of Notification DTO
     */
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

    /**
     * Marks given notification as seen if it doesn't need input and belongs to token
     *
     * @param token identifies session that makes the request
     * @param id    identifies notification
     * @return Notification DTO
     */
    public dto.Notification markNotifAsRead(String token, int id) {
        dto.Notification notifDto = null;

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            Notification notif = notifDao.find(id);
            if (notif != null) {
                if (!notif.isNeedsInput() && notif.getNotificationOwner().getUserId() == user.getUserId()) {
                    notif.setSeen(true);
                    notifDao.merge(notif);
                    notifDto = convertNotifEntToDto(notif);
                }
            }
        }
        return notifDto;
    }

    /**
     * Deletes notification from database if it doesn't need input and belongs to token
     *
     * @param token identifies session that makes the request
     * @param id    identifies notification
     * @return true if notification is deleted from database
     */
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

    /**
     * Token answers to invitation made by project manager to participate in project, via notification if notification belongs to token
     * Updates ProjectMember that defines relationship between token and project
     * Automatically marks notification as seen
     *
     * @param token   identifies session that makes the request
     * @param notifId identifies notification
     * @param answer  value = 0 to refuse invitation; value = 1 to accept invitation
     * @return Notification DTO
     */
    public dto.Notification answerInvitation(String token, int notifId, int answer) {

        dto.Notification notifDto = null;

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            Notification notif = notifDao.find(notifId);
            if (notif != null) {
                if (notif.getNotificationOwner().getUserId() == user.getUserId()) {

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
                    recordMemberInvitationResponse(user, projMember.getProjectToParticipate(), answer);


                    if (answer == 1) {
                        // convite aceite
                        userBean.refusePendingInvitations(user.getUserId()); // recusa outros convites para participar em outros projectos
                        LOGGER.info("User ID " + projMember.getUserInvited().getUserId() + " accepted to participate in project ID " + projMember.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());

                    } else if (answer == 0) {
                        LOGGER.info("User ID " + projMember.getUserInvited().getUserId() + " refused to participate in project ID " + projMember.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());
                    }

                    notifDto = convertNotifEntToDto(notif);
                }
            }
        }

        return notifDto;
    }

    /**
     * Notifies project members if user accepted invitation made by project manager to participate in project
     * Notifies project managers if user rejected invitation made by project manager to participate in project
     *
     * @param projMember represents ProjectMember that defines relationship between user and project
     * @param answer     value = 0 to reject invitation; value = 1 to accept invitation
     */
    private void notifyRelevantPartsOfInvitationResponse(ProjectMember projMember, int answer) {

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

    /**
     * Notifies project members of answer to contest application
     *
     * @param project represents project
     * @param answer  value = 0 if application is rejected; value = 1 if application is accepted
     */
    public void notifyProjectMembersOfApplicationResponse(Project project, int answer) {

        List<User> membersList = projMemberDao.findListOfUsersByProjectId(project.getId());

        if (membersList != null) {
            for (User u : membersList) {
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
                // notif.setProjectMember(projMember);
                if (answer == 0) {
                    // projecto rejeitado a concurso
                    notif.setMessage("A candidatura a concurso do projecto " + project.getTitle() + " foi recusada");
                    notif.setMessageEng("Application for contest of project " + project.getTitle() + " has been refused");

                } else if (answer == 1) {
                    // projecto aceite a concurso
                    notif.setMessage("A candidatura a concurso do projecto " + project.getTitle() + " foi aceite. A execução do projecto pode avançar assim que o concurso abrir a fase de execução");
                    notif.setMessageEng("Application for contest of project " + project.getTitle() + " has been accepted. Project development can start once contest execution phase opens ");

                }
                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);
            }
        }
    }


    /**
     * Notifies user excluded from project by project manager so that it understands why no longer can access full project information
     *
     * @param project represents project
     * @param user    represents user excluded
     */
    public void notifyUserHasBeenExcludedFromProject(Project project, User user) {

        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        notif.setMessage("Foi excluído do projecto " + project.getTitle());
        notif.setMessageEng("You have been excluded from project " + project.getTitle());
        notif.setNotificationOwner(user);
        notifDao.persist(notif);
        notifyRealTime(notif, user);

    }


    /**
     * Notifies user that it is responsible for given task
     *
     * @param user      identifies user responsible for task
     * @param taskTitle is the name of the task
     */
    public void notifyNewOwnerOfTask(User user, String taskTitle) {

        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        // notif.setProjectMember(projMember);

        notif.setMessage("Tem uma nova tarefa à sua responsabilidade: " + taskTitle);
        notif.setMessageEng("You have been designated responsible for task: " + taskTitle);

        notif.setNotificationOwner(user);
        notifDao.persist(notif);
        notifyRealTime(notif, user);
    }

    /**
     * Notifies user that task of which it was responsible has been deleted
     *
     * @param user      represents user
     * @param taskTitle identifies task
     */
    public void notifyTaskWasRemoved(User user, String taskTitle) {
        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        // notif.setProjectMember(projMember);

        notif.setMessage("A tarefa " + taskTitle + ", à sua responsabilidade, foi apagada");
        notif.setMessageEng("Task " + taskTitle + ", of which you were responsible, has been deleted");

        notif.setNotificationOwner(user);
        notifDao.persist(notif);
        notifyRealTime(notif, user);
    }

    /**
     * Notifies project member that given task which was its responsability to complete is no longer (person is notified to make sure it knows)
     *
     * @param user      represents member to be notified, and previous owner of task
     * @param taskTitle identifies task
     */
    public void notifyTaskIsNoLongerMemberResponsability(User user, String taskTitle) {
        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        // notif.setProjectMember(projMember);

        notif.setMessage("Atenção, tarefa " + taskTitle + " deixou de ser da sua responsabilidade");
        notif.setMessageEng("Attention, task " + taskTitle + " is no longer your responsability");

        notif.setNotificationOwner(user);
        notifDao.persist(notif);
        notifyRealTime(notif, user);
    }

    /**
     * Notify project members that task has been concluded
     *
     * @param taskEnt represents task entity
     */
    public void notifyAllMembersTaskIsFinished(Task taskEnt) {

        List<User> members = projMemberDao.findListOfUsersByProjectId(taskEnt.getProject().getId());
        if (members != null) {
            for (User u : members) {

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
            }
        }
    }

    /**
     * Notifies all contest managers of important events: new contest created, contest edited, new project application received for given contest
     * Notifies automatically if date to open or close contest application period or if date to ONGOING start or finish are due tomorrow
     *
     * @param value        value = 0 if new contest has been created; value = 1 if contest has been edited; value = 2 if contest received project application
     * @param contestTitle identifies contest
     */
    public void notifyAllContestManagers(int value, String contestTitle) {
        List<User> contestManagersList = userDao.findListContestManagers();

        if (contestManagersList != null) {
            for (User u : contestManagersList) {
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
                // notif.setProjectMember(projMember);
                if (value == 0) {
                    // novo concurso criado
                    notif.setMessage("Um novo concurso foi criado: " + contestTitle);
                    notif.setMessageEng("A new contest has been created: " + contestTitle);
                } else if (value == 1) {
                    // info de concurso foi editada
                    notif.setMessage("Informação do concurso " + contestTitle + " foi editada");
                    notif.setMessageEng("Details of contest " + contestTitle + " have been edited");
                } else if (value == 2) {
                    // projecto concorreu a concurso
                    notif.setMessage("Nova candidatura recebida para o concurso " + contestTitle);
                    notif.setMessageEng("There is a new application for contest " + contestTitle);
                } else if (value == 3) {
                    // open call should open tomorrow
                    notif.setMessage("Open call do concurso " + contestTitle + " deverá começar amanhã");
                    notif.setMessageEng("Open call for contest " + contestTitle + " should open tomorrow");
                } else if (value == 4) {
                    // open call should close tomorrow
                    notif.setMessage("Open call do concurso " + contestTitle + " deverá fechar amanhã");
                    notif.setMessageEng("Open call for contest " + contestTitle + " should close tomorrow");
                } else if (value == 5) {
                    // ONGOING deverá abrir amanhã
                    notif.setMessage("Fase ONGOING do concurso " + contestTitle + " deverá começar amanhã");
                    notif.setMessageEng("ONGOING phase of contest " + contestTitle + " should open tomorrow");
                } else if (value == 6) {
                    // ONGOING deverá fechar amanhã
                    notif.setMessage("Fase ONGOING do concurso " + contestTitle + " deverá fechar amanhã");
                    notif.setMessageEng("ONGOING phase of contest " + contestTitle + " should close tomorrow");
                }
                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);
            }
        }
    }

    /**
     * Notifies all users with valid account that a given contest has opened to applications
     *
     * @param contest represents contest
     */
    public void notifyAllUsers(Contest contest) {
        List<User> allUsers = userDao.findAllUsersWithValidatedAccount();

        if (allUsers != null) {
            for (User u : allUsers) {
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

    /**
     * Notifies all active members of accepted projects in given contest that contest is ONGOING
     * It means that projects can start being executed
     *
     * @param contest represents given contest
     */
    public void notifyProjectMembersExecutionHasStarted(Contest contest) {

        List<Project> acceptedProjects = applicationDao.findAcceptedProjectsForGivenContestId(contest.getId());

        if (acceptedProjects != null) {
            for (Project p : acceptedProjects) {
                // encontrar lista de membros activos -accepted and not removed
                List<User> members = projMemberDao.findListOfUsersByProjectId(p.getId());

                if (members != null) {
                    for (User u : members) {
                        Notification notif = new Notification();
                        notif.setCreationTime(Date.from(Instant.now()));
                        notif.setSeen(false);
                        notif.setNeedsInput(false);
                        // notif.setProjectMember(projMember);

                        notif.setMessage("Estado do concurso " + contest.getTitle() + " é ONGOING. Já pode iniciar a execução do seu projecto");
                        notif.setMessageEng("Status of contest " + contest.getTitle() + " is ONGOING. You can now start execution of your project");

                        notif.setNotificationOwner(u);
                        notifDao.persist(notif);
                        notifyRealTime(notif, u);
                    }
                }
            }
        }
    }

    /**
     * Notifies all active members of accepted projects in given contest that contest has FINISHED
     * Informs which project has been declared winner
     *
     * @param contest represents contest
     */
    public void notifyContestHasFinished(Contest contest) {
        List<Project> acceptedProjects = applicationDao.findAcceptedProjectsForGivenContestId(contest.getId());

        if (acceptedProjects != null) {
            for (Project p : acceptedProjects) {
                List<User> members = projMemberDao.findListOfUsersByProjectId(p.getId());

                if (members != null) {
                    for (User u : members) {
                        Notification notif = new Notification();
                        notif.setCreationTime(Date.from(Instant.now()));
                        notif.setSeen(false);
                        notif.setNeedsInput(false);
                        // notif.setProjectMember(projMember);

                        notif.setMessage("O concurso " + contest.getTitle() + " está concluído. O projecto vencedor é " + contest.getWinner().getTitle());
                        notif.setMessageEng("Contest " + contest.getTitle() + " has finished. Contest winner is " + contest.getWinner().getTitle());

                        notif.setNotificationOwner(u);
                        notifDao.persist(notif);
                        notifyRealTime(notif, u);
                    }
                }
            }
        }
    }

    /**
     * Notifies all users of which project has been declared winner of given contest
     *
     * @param contest represents contest
     */
    public void notifyContestHasWinner(Contest contest) {
        List<User> allUsers = userDao.findAllUsersWithValidatedAccount();

        if (allUsers != null) {
            for (User u : allUsers) {
                Notification notif = new Notification();
                notif.setCreationTime(Date.from(Instant.now()));
                notif.setSeen(false);
                notif.setNeedsInput(false);
                // notif.setProjectMember(projMember);

                notif.setMessage("O projecto vencedor do concurso " + contest.getTitle() + " é " + contest.getWinner().getTitle());
                notif.setMessageEng("Project " + contest.getWinner().getTitle() + " has been declares winner of contest " + contest.getTitle());

                notif.setNotificationOwner(u);
                notifDao.persist(notif);
                notifyRealTime(notif, u);
            }
        }
    }

    /**
     * Records in project history given project has been created
     *
     * @param newProjEnt represents new project
     * @param user       representes who created project
     */
    public void recordProjectCreation(Project newProjEnt, User user) {

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setMessage("Projecto criado por " + user.getFirstName() + " " + user.getLastName() + ". Estado: Planning");
        record.setAuthor(user);
        record.setProject(newProjEnt);
        recordDao.persist(record);
    }

    /**
     * Records in project history given project has been edited
     *
     * @param project represents project
     * @param user    represents user who edited
     */
    public void recordProjectEdition(Project project, User user) {

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setMessage("Informação geral do projecto editada por " + user.getFirstName() + " " + user.getLastName());
        record.setAuthor(user);
        record.setProject(project);
        recordDao.persist(record);
    }

    /**
     * Records in project history given project status has been modified
     *
     * @param project represents project
     * @param user    represents user responsible for status editing if it modified manually by someone
     * @param status  value defines new project status
     */
    public void recordProjectStatusChange(Project project, User user, int status) {

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        if (user != null) {
            // alterações automáticas não têm user atribuído
            record.setAuthor(user);
        }
        record.setProject(project);

        switch (status) {
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

    /**
     * Records in project history that one of its tasks has had status modified
     *
     * @param user   represents user that modified task status
     * @param task   represents task
     * @param status value defines new task status
     */
    public void recordTaskStatusEdit(User user, Task task, int status) {

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);
        record.setProject(task.getProject());

        switch (status) {
            case 1:
                record.setMessage("Estado do tarefa " + task.getTitle() + ": In progress");
                break;
            case 2:
                record.setMessage("Estado do tarefa " + task.getTitle() + ": Finished");
                break;
        }
        recordDao.persist(record);
    }

    /**
     * Records in project history whether user invited to participate in project by project manager accepts (1) or rejects (0) invitation
     *
     * @param user    represents user invited
     * @param project represents project
     * @param answer  value identifies whether invitation was accepted (1) or rejected (0)
     */
    public void recordMemberInvitationResponse(User user, Project project, int answer) {

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);
        record.setProject(project);

        switch (answer) {
            case 0:
                //recusou convite para participar no projecto
                record.setMessage(user.getFirstName() + " " + user.getLastName() + " recusou o convite para participar no projecto");
                break;
            case 1:
                //aceitou convite para participar no projecto
                record.setMessage(user.getFirstName() + " " + user.getLastName() + " aceitou o convite para participar no projecto");
                break;
        }
        recordDao.persist(record);
    }

    /**
     * Records in project history when a member leaves project, either excluded or left by choice
     *
     * @param user    represents user that left project
     * @param project represents project
     */
    public void recordMemberRemovalFromProject(User user, Project project) {

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);
        record.setProject(project);
        record.setMessage(user.getFirstName() + " " + user.getLastName() + " saiu do projecto");

        recordDao.persist(record);
    }

    /**
     * Records in project history when member role in given project is changed
     *
     * @param token  identifies session that makes the request
     * @param userId identifies member whose role is modified
     * @param projId identifies project
     * @param role   value = 0 when role is modified to 'normal'; value = 1 when role is modified to project manager
     */
    public void recordProjectMemberRoleChange(String token, int userId, int projId, int role) {
        Project project = projDao.findProjectById(projId);
        if (project != null) {
            User loggedUser = tokenDao.findUserEntByToken(token);
            User member = userDao.findUserById(userId);
            if (loggedUser != null && member != null) {
                ProjectHistory record = new ProjectHistory();
                record.setCreationTime(Date.from(Instant.now()));
                record.setAuthor(loggedUser);
                record.setProject(project);
                if (role == 0) {
                    record.setMessage(member.getFirstName() + " " + member.getLastName() + " já não é gestor do projecto");
                } else if (role == 1) {
                    record.setMessage(member.getFirstName() + " " + member.getLastName() + " é gestor do projecto");
                }
                recordDao.persist(record);
            }
        }
    }

    /**
     * Records in project history whether user that self-invites itself to participate in project is accepted (1) or rejected (0) by project manager
     *
     * @param manager represents project manager
     * @param user    represents user who wants to participate in project
     * @param project represents project
     * @param answer  value identifies whether invitation was accepted (1) or rejected (0)
     */
    public void recordManagerResponseToSelfInvitation(User manager, User user, Project project, int answer) {
        // guarda registo no histórico do projecto da resposta que gestor deu a um pedido para participar no projecto
        // manager do projecto é author da resposta ao pedido. user é a pessoa que pediu para participar

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(manager);
        record.setProject(project);

        switch (answer) {
            case 0:
                //recusou pedido para participar no projecto
                record.setMessage("Pedido de " + user.getFirstName() + " " + user.getLastName() + " para participar no projecto foi recusado");
                break;
            case 1:
                //aceitou pedido para participar no projecto
                record.setMessage("Pedido de " + user.getFirstName() + " " + user.getLastName() + " para participar no projecto foi aceite");
                break;
        }
        recordDao.persist(record);
    }

    /**
     * Notifies user that self-invitation to participate in project was accepted (1) or rejected (0)
     * @param pm represents ProjectMember that defines relationship between user and project
     * @param answer value = 0 if self-invitation is rejected; value=1 if self-invitation is accepted
     */
    public void notifyPotentialMemberOfSelfInvitationResponse(ProjectMember pm, int answer) {

        Notification notif = new Notification();
        notif.setCreationTime(Date.from(Instant.now()));
        notif.setSeen(false);
        notif.setNeedsInput(false);
        notif.setProjectMember(pm);

        if (answer == 0) {
            // pedido recusado
            notif.setMessage("O seu pedido para participar no projecto " + pm.getProjectToParticipate().getTitle() + " foi recusado");
            notif.setMessageEng("Your self-invite of to participate in project " + pm.getProjectToParticipate().getTitle() + " has been rejected");


        } else if (answer == 1) {
            // pedido aceite
            notif.setMessage("O seu pedido para participar no projecto " + pm.getProjectToParticipate().getTitle() + " foi aceite");
            notif.setMessageEng("Your self-invite of to participate in project " + pm.getProjectToParticipate().getTitle() + " has been accepted");
        }

        notif.setNotificationOwner(pm.getUserInvited());
        notifDao.persist(notif);
        notifyRealTime(notif, pm.getUserInvited());
    }

    /**
     * Records in project history whether project application to contest was accepted (1) or rejected (0)
     *
     * @param user    represents contest manager responsible for decision. Will not be persisted in database so that it is not identified in project history
     * @param project represents project
     * @param answer  value identifies whether application was accepted (1) or rejected (0)
     */
    public void recordProjectApplicationResult(User user, Project project, int answer) {
        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        if (user != null) {
            record.setAuthor(user);
        }
        record.setProject(project);

        if (answer == 0) {
            //rejeitado
            record.setMessage("A candidatura para participar no concurso foi rejeitada");

        } else if (answer == 1) {
            //aceite
            record.setMessage("A candidatura para participar no concurso foi aceite");
        }
        recordDao.persist(record);
    }

    /**
     * Records in project history that given project was declared winner of contest
     *
     * @param user    represents contest manager responsible for decision. Will not be persisted in database so that it is not identified in project history
     * @param project represents project
     * @param contest identifies contest
     */
    public void recordProjectDeclaredWinner(User user, Project project, Contest contest) {

        ProjectHistory record = new ProjectHistory();
        record.setCreationTime(Date.from(Instant.now()));
        record.setAuthor(user);
        record.setProject(project);

        record.setMessage("O projecto foi declarado vencedor no concurso " + contest.getTitle());
        recordDao.persist(record);
    }

    /**
     * Gets list of contacts (users) of given token
     * Contact is another user with whom token has sent or received personal messages
     * If idToChat has a value that is not 0, means that token wants to start conversation with corresponding user. User is added to contacts list
     *
     * @param token    identifies session that makes the request
     * @param idToChat identifies user with whom token wants to start a conversation
     * @return list of UserInfo DTO
     */
    public List<UserInfo> getContactsList(String token, int idToChat) {

        List<UserInfo> contactsList = new ArrayList<>();
        User user = tokenDao.findUserEntByToken(token);
        Set<User> mergeSet = new HashSet<>();

        if (user != null) {
            List<User> sendersList = personalChatDao.findListOfSenderContactsOfGivenUser(user.getUserId());
            List<User> receiversList = personalChatDao.findListOfReceiverContactsOfGivenUser(user.getUserId());

            if (sendersList != null) {
                mergeSet.addAll(sendersList);
            }

            if (receiversList != null) {
                mergeSet.addAll(receiversList);
            }

            List<User> mergeList = new ArrayList<>(mergeSet);
            List<entity.User> tempList = mergeList.stream().filter(userE -> userE.getUserId() != idToChat).collect(Collectors.toList());
            // retira o id do user para garantir que só aparecerá 1x na lista, dado que no frontend a pessoa pode procurar user na página inicial para enviar mensagem

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

            if (idToChat != 0) {
                // tem de ir buscar contacto com id
                User newContact = userDao.findUserById(idToChat);

                if (newContact != null) {

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

    /**
     * Gets all personal messages sent or received by token
     *
     * @param token identifies session that makes the request
     * @return list of PersonalMessage DTO
     */
    public List<dto.PersonalMessage> getAllPersonalMessages(String token) {

        List<dto.PersonalMessage> listDto = new ArrayList<>();

        User user = tokenDao.findUserEntByToken(token);

        if (user != null) {
            List<PersonalMessage> allTokenMessages = personalChatDao.findListOfMessagesOfGivenUser(user.getUserId());

            if (allTokenMessages != null) {
                for (PersonalMessage m : allTokenMessages) {
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

    /**
     * Notifies in real-time project chat with new messages
     *
     * @param message represents message sent
     * @param project represents project
     */
    public void notifyProjectChatRealTime(ProjectChat message, Project project) {
        // envia mensagem em tempo-real por chat do projecto a todos os tokens cujo user seja membro activo do projecto

        List<User> membersList = projMemberDao.findListOfUsersByProjectId(project.getId());

        if (membersList != null) {
            for (User u : membersList) {
                List<String> listTokens = tokenDao.findTokenListByUserId(u.getUserId());
                if (listTokens != null) {
                    for (String t : listTokens) {
                        websocket.ProjectChat.sendNotification(message, t);
                    }

                }
            }
        }
    }

    /**
     * Marks personal messages sent by contactId to token as seen
     *
     * @param token     identifies session that makes the request
     * @param contactId identifies contact
     * @return true if personal messages are marked as seen
     */
    public boolean markMessagesRead(String token, int contactId) {

        boolean res = false;

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            List<PersonalMessage> messagesEnt = personalChatDao.findListOfReceivedMessagesOfGivenUserSentByContactId(user.getUserId(), contactId);

            if (messagesEnt != null) {
                for (PersonalMessage m : messagesEnt) {
                    m.setSeen(true);
                    personalChatDao.merge(m);
                }
                res = true;
            }
        }
        return res;
    }

    /**
     * Gets list of messages exchanged between token and contact
     *
     * @param token     identifies session that makes the request
     * @param contactId identified contact (another app user)
     * @return list of PersonalMessage DTO
     */
    public List<dto.PersonalMessage> getMessagesForSpecificContact(String token, int contactId) {
        List<dto.PersonalMessage> list = new ArrayList<>();

        User user = tokenDao.findUserEntByToken(token);

        if (user != null) {
            List<PersonalMessage> messagesEnt = personalChatDao.findListOfExchangedMessagesBetweenTwoContacts(user.getUserId(), contactId);
            if (messagesEnt != null) {
                for (PersonalMessage m : messagesEnt) {
                    list.add(convertPersonalMessageEntToDto(m));
                }
            }
        }
        return list;
    }

    /**
     * Converts PersonalMessage entity to PersonalMessage DTO
     *
     * @param m represents PersonalMessage entity
     * @return PersonalMessage DTO
     */
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

    /**
     * Persists in database a new personal message sent by token to messageReceiver
     *
     * @param message represents message information to de added, including messageReceiver
     * @param token   identifies session that makes the request
     * @return PersonalMessage DTO
     */
    public dto.PersonalMessage sendMessageToContact(dto.PersonalMessage message, String token) {
        dto.PersonalMessage dto = new dto.PersonalMessage();
        User user = tokenDao.findUserEntByToken(token);

        if (user != null) {
            User receiver = userDao.find(message.getUserReceiverId());
            if (receiver != null) {
                PersonalMessage messageEnt = new PersonalMessage();
                messageEnt.setSeen(false);
                messageEnt.setMessage(message.getMessage());
                messageEnt.setCreationTime(Date.from(Instant.now()));
                messageEnt.setMessageSender(user);
                messageEnt.setMessageReceiver(receiver);

                personalChatDao.persist(messageEnt);

                dto = convertPersonalMessageEntToDto(messageEnt);

                List<String> listTokens = tokenDao.findTokenListByUserId(receiver.getUserId());
                if (listTokens != null) {
                    for (String t : listTokens) {
                        websocket.PersonalChat.sendNotification(dto, t);
                    }
                }
            }
        }
        return dto;
    }

    /**
     * Notifies automatically all contest managers that important action is needed tomorrow
     * If date to open or close contest application period or if date to ONGOING start or finish are due tomorrow
     */
    public void notifyContestManagersImportantActionNeeded() {
        Date today = Date.from(Instant.now());
        Long oneDay = (long) (24 * 60 * 60 * 1000);
        Date todayPlusOne = new Date(today.getTime() + oneDay);

        List<Contest> all = contestDao.findAll();

        if (all != null) {
            for (Contest c : all) {
                if (c.getStartOpenCall().after(today) && c.getStartOpenCall().before(todayPlusOne)) {
                    // contest open call opens tomorrow
                    notifyAllContestManagers(3, c.getTitle());
                }
                if (c.getFinishOpenCall().after(today) && c.getFinishOpenCall().before((todayPlusOne))) {
                    notifyAllContestManagers(4, c.getTitle());
                }
                if (c.getStartDate().after(today) && c.getStartDate().before((todayPlusOne))) {
                    notifyAllContestManagers(5, c.getTitle());
                }
                if (c.getFinishDate().after(today) && c.getFinishDate().before((todayPlusOne))) {
                    notifyAllContestManagers(6, c.getTitle());
                }
            }
        }
    }

    /**
     * Notifies automatically all project members of accepted projects that contest finish date is due in 7 days
     */
    public void notifyProjectMembersContestApproachesEnding() {
        Date today = Date.from(Instant.now());
        Long oneDay = (long) (24 * 60 * 60 * 1000);
        Date todayPlus7Days = new Date(today.getTime() + 7 * oneDay);

        List<Contest> all = contestDao.findAll();

        if (all != null) {
            for (Contest c : all) {
                if (c.getFinishDate().after(today) && c.getFinishDate().before((todayPlus7Days))) {
                    notifyProjectMembers(c);
                }
        }
    }}
    /**
     * Notifies automatically all project members of accepted projects that contest finish date is due in 7 days
     * @Param c representes contest
     */
    private void notifyProjectMembers(Contest c) {

        List<Project> projects = applicationDao.findAcceptedProjectsForGivenContestId(c.getId());
        if (projects!=null){
            for (Project p : projects){
                List <User> projectMembers = projMemberDao.findListOfUsersByProjectId(p.getId());
                if (projectMembers!=null){
                    for (User u:projectMembers){
                        Notification notif = new Notification();
                        notif.setCreationTime(Date.from(Instant.now()));
                        notif.setSeen(false);
                        notif.setNeedsInput(false);
                        // notif.setProjectMember(projMember);

                        notif.setMessage("O concurso " + c.getTitle() + " terminará dentro de 7 dias");
                        notif.setMessageEng("Contest " + c.getTitle() + " will finish in 7 days");

                        notif.setNotificationOwner(u);
                        notifDao.persist(notif);
                        notifyRealTime(notif, u);
                    }
                }
            }
        }
    }
}