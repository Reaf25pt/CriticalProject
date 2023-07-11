package bean;

import ENUM.StatusContest;
import ENUM.StatusProject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Application;
import dto.Task;
import entity.ContestApplication;
import entity.Project;
import entity.User;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class Contest {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Contest.class);
    @EJB
    dao.User userDao;
    @EJB
    dao.Token tokenDao;
    @EJB
    dao.Project projDao;
    @EJB
    dao.Contest contestDao;
    @EJB
    dao.ProjectMember projMemberDao;
    @EJB
    dao.ContestApplication applicationDao;
    @Inject
    Communication communicationBean;
    @EJB
    dao.Task taskDao;
    @Inject
    bean.User userBean;

    public Contest() {
    }

    /**
     * Creates a new contest and persists it in database (table Contest)
     * Dates must be sequential (startOpenCall before finishOpenCall before startDate before FinishDate) and this is verified in frontend
     * Verifies if openCall dates overlap any existing openCall periods of any other contest, to make sure that only 1 OPEN contest exists
     *
     * @param contestInfo contains contest information inserted in frontend
     * @param token       identifies session that makes the request
     * @return true if a new contest is created and persisted in database
     */
    public boolean createNewContest(dto.Contest contestInfo, String token) {
        boolean res = false;

        if (contestInfo != null) {
            entity.User user = tokenDao.findUserEntByToken(token);
            if (user != null) {
                if (!verifyOpenCallDates(contestInfo.getStartOpenCall(), contestInfo.getFinishOpenCall(), 0)) {

                    entity.Contest contest = new entity.Contest();
                    contest.setTitle(contestInfo.getTitle());
                    contest.setStartDate(contestInfo.getStartDate());
                    contest.setFinishDate(contestInfo.getFinishDate());
                    contest.setStartOpenCall(contestInfo.getStartOpenCall());
                    contest.setFinishOpenCall(contestInfo.getFinishOpenCall());
                    contest.setDetails(contestInfo.getDetails());
                    contest.setRules(contestInfo.getRules());
                    contest.setMaxNumberProjects(contestInfo.getMaxNumberProjects());
                    contest.setStatus(StatusContest.PLANNING);

                    contestDao.persist(contest);
                    communicationBean.notifyAllContestManagers(0, contestInfo.getTitle());
                    res = true;
                    LOGGER.info("A new contest: " + contestInfo.getTitle() + " is persisted in database by user " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());


                }
            }
        }
        return res;
    }


    /**
     * Verifies if openCall dates of given contest overlap any existing openCall periods of any other contest, to make sure that only 1 OPEN contest exists
     * Checks if startOpenCall is after any contests' finishOpenCall date
     * Checks if finishOpenCall is before any contests' startOpenCall date
     * Together these verifications prevent openCall period to overlap any other contests' openCall periods
     *
     * @param startOpenCall  refers to starting day of open call of given contest
     * @param finishOpenCall refers to finishing day of open call of given contest
     * @return true if dates overlap any other existing contest's open call (therefore, not allowing creating or editing contest)
     */
    private boolean verifyOpenCallDates(Date startOpenCall, Date finishOpenCall, int contestId) {

        boolean res = false;
        int count = 0;  // contador para número de ocorrências que não permitem validar as datas

        List<entity.Contest> listAllContests = contestDao.findAll();
        List<entity.Contest> tempList;
        if(contestId!=0){
            // to not compare dates with its own entity
            tempList = listAllContests.stream().filter(contest -> contest.getId() != contestId).collect(Collectors.toList());
        } else{
           tempList = listAllContests;
        }


        for (entity.Contest c : tempList) {
            if (startOpenCall.after(c.getFinishOpenCall()) || finishOpenCall.before(c.getStartOpenCall())) {
                count++;
            }
        }

        if (count != tempList.size()) {
            res = true; // datas não permitem que novo concurso seja criado
        }
        return res;
    }

    /**
     * Verifies if token that makes the request has permission of Profile A - contest manager
     *
     * @param token identifies session that makes the request
     * @return true if token is indeed contest manager, therefore being allowed to make the request
     */
    public boolean verifyUserProfile(String token) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            if (user.isContestManager()) {
                res = true;
            }
        }

        return res;
    }

    /**
     * Get list of all contests in database
     * @return list of Contest DTO that contains information of contest
     */
    public List<dto.Contest> getAllContests() {
        List<dto.Contest> list = new ArrayList<>();

        List<entity.Contest> tempList = contestDao.findAll();

        for (entity.Contest c : tempList) {
            list.add(convertContestEntToDto(c));
        }
        return list;
    }
    /**
     * Get list of active contests in database: whose status is OPEN or ONGOING
     * @return list of Contest DTO that contains information of contest
     */
    public List<dto.Contest> getActiveContests() {
        List<dto.Contest> list = new ArrayList<>();

        List<entity.Contest> tempList = contestDao.findActiveContests();

        for (entity.Contest c : tempList) {
            list.add(convertContestEntToDto(c));
        }
        return list;
    }

    /**
     * Converts Contest Entity to Contest DTO so that information can be sent as HTTP response
     *
     * @param c contains information of contest entity
     * @return Contest information. Format is DTO
     */
    private dto.Contest convertContestEntToDto(entity.Contest c) {
        dto.Contest contestDto = new dto.Contest();
        contestDto.setId(c.getId());
        contestDto.setTitle(c.getTitle());
        contestDto.setStartDate(c.getStartDate());
        contestDto.setFinishDate(c.getFinishDate());
        contestDto.setStartOpenCall(c.getStartOpenCall());
        contestDto.setFinishOpenCall(c.getFinishOpenCall());
        contestDto.setDetails(c.getDetails());
        contestDto.setRules(c.getRules());
        contestDto.setMaxNumberProjects(c.getMaxNumberProjects());
        contestDto.setStatus(c.getStatus().getStatus());
        contestDto.setStatusInt(c.getStatus().ordinal());

        if (c.getWinner() != null) {
            contestDto.setWinnerProjectId(c.getWinner().getId());
        }
        return contestDto;
    }

    /**
     * Gets details of given contest
     * @param id identifies given contest
     * @return Contest DTO with contest information
     */
    public dto.Contest getContest( int id) {
        dto.Contest contest = new dto.Contest();
        entity.Contest contestEnt = contestDao.find(id);

        if (contestEnt != null) {
            contest = convertContestEntToDto(contestEnt);
        }
        return contest;
    }

    /**
     * Verifies if given contest status is PLANNING, because it can only be edited if that is the case
     *
     * @param id identifies contest
     * @return true if contest information can be edited
     */
    public boolean verifyPermissionToModifyContest(int id) {
        boolean res = false;

        entity.Contest contest = contestDao.find(id);

        if (contest != null) {
            if (contest.getStatus() == StatusContest.PLANNING) {
                res = true;
            }
        }

        return res;
    }

    /**
     * Checks if contest information can be edited
     * Dates must be sequential (startOpenCall before finishOpenCall before startDate before FinishDate) and this is verified in frontend
     * Verifies if openCall dates overlap any existing openCall periods of any other contest, to make sure that only 1 OPEN contest exists
     *
     * @param token       identifies session that makes the request
     * @param editContest contains information to edit given contest
     * @return true if contest info can be edited
     */
    public boolean canEditContestInfo(String token, dto.Contest editContest) {

        boolean res = false;
        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Contest contestEnt = contestDao.find(editContest.getId());

            if (contestEnt != null) {
                if (!verifyOpenCallDates(editContest.getStartOpenCall(), editContest.getFinishOpenCall(), editContest.getId())) {

                    res = true;
            }
        }}
        return res;
    }

    /**
     * Edits contest information after checking dates
     * Dates must be sequential (startOpenCall before finishOpenCall before startDate before FinishDate) and this is verified in frontend
     * Verifies if openCall dates overlap any existing openCall periods of any other contest, to make sure that only 1 OPEN contest exists
     *
     * @param token       identifies session that makes the request
     * @param editContest contains information to edit given contest
     * @return Contest DTO
     */

    public dto.Contest editContestInfo(String token, dto.Contest editContest) {

        dto.Contest contestDto = new dto.Contest();
        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Contest contestEnt = contestDao.find(editContest.getId());

            if (contestEnt != null) {
                  contestEnt.setTitle(editContest.getTitle());
                    contestEnt.setStartOpenCall(editContest.getStartOpenCall());
                    contestEnt.setFinishOpenCall(editContest.getFinishOpenCall());
                    contestEnt.setStartDate(editContest.getStartDate());
                    contestEnt.setFinishDate(editContest.getFinishDate());
                    contestEnt.setDetails(editContest.getDetails());
                    contestEnt.setRules(editContest.getRules());
                    contestEnt.setMaxNumberProjects(editContest.getMaxNumberProjects());

                    contestDao.merge(contestEnt);
                    communicationBean.notifyAllContestManagers(1, contestEnt.getTitle());
                    contestDto = convertContestEntToDto(contestEnt);
                    LOGGER.info("Contest: " + editContest.getTitle() + " information is edited by user " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

        }}

        return contestDto;
    }

    /**
     * Edits contest status, according to status value sent from frontend
     * It will not allow to go back to a previous status
     * Contest status is edited after human intervention, not automatically, therefore date of interest must be validated
     * Edit to OPEN: contest startOpenCall date must be equal or before current date. All app users are notified
     * Edit to ONGOING: contest startDate date must be equal or before current date. Refuses any pending project applications and notifies members of accepted projects
     * Edit to CONCLUDED: contest finishDate date must be equal or before current date and contest must have a winner project attributed. Notifies contest participants
     *
     * @param token     identifies session that makes the request
     * @param contestId identifies contest whose status is to be modified
     * @param status:   int value that identifies corresponding Contest Status ENUM: 1 for OPEN, 2 for ONGOING, 3 for CONCLUDED
     * @return Contest information DTO
     */
    public dto.Contest editContestStatus(String token, int contestId, int status) {
        boolean res = false;
        dto.Contest contestDto = null;

        User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Contest contest = contestDao.find(contestId);

            if (contest != null) {

                switch (status) {
                    case 1:
                        if (validateDate(contest.getStartOpenCall())) {
                            contest.setStatus(StatusContest.OPEN);
                            contestDao.merge(contest);
                            contestDto = convertContestEntToDto(contest);
                            communicationBean.notifyAllUsers(contest);
                            res = true;
                            LOGGER.info("Status of contest: " + contest.getTitle() + " is edited by user " + user.getUserId() + " to OPEN. IP Address of request is " + userBean.getIPAddress());
                        }
                        break;
                    case 2:
                        if (validateDate(contest.getStartDate())) {
                            contest.setStatus(StatusContest.ONGOING);
                            contestDao.merge(contest);
                            contestDto = convertContestEntToDto(contest);
                            communicationBean.notifyProjectMembersExecutionHasStarted(contest);
                            refuseUnansweredApplications(contest);
                            res = true;
                            LOGGER.info("Status of contest: " + contest.getTitle() + " is edited by user " + user.getUserId() + " to ONGOING. IP Address of request is " + userBean.getIPAddress());
                        }
                        break;
                    case 3:
                        if (validateDate(contest.getFinishDate()) && checkWinner(contest)) {
                            contest.setStatus(StatusContest.CONCLUDED);
                            contestDao.merge(contest);
                            contestDto = convertContestEntToDto(contest);
                            communicationBean.notifyContestHasFinished(contest);
                            res = true;
                            LOGGER.info("Status of contest: " + contest.getTitle() + " is edited by user " + user.getUserId() + " to CONCLUDED. IP Address of request is " + userBean.getIPAddress());
                        }
                        break;
                }
            }
        }
        return contestDto;
    }

    /**
     * Verifies if contest has a project declared as winner
     * Mandatory before changing contest status to CONCLUDED
     *
     * @param contest represents contest
     * @return true if a project winner has been declared
     */
    private boolean checkWinner(entity.Contest contest) {
        boolean res = false;

        if (contest.getWinner() != null) {
            res = true;
        }

        return res;
    }

    /**
     * Verifies if given contest date is equal or before current date
     * Necessary to allow contest status editing
     *
     * @param date represents contest date being validated
     * @return true if date is valid and status cant therefore be modified
     */
    private boolean validateDate(Date date) {
        boolean res = false;

        Date today = Date.from(Instant.now());

        if (date.equals(today) || date.before(today)) {
            res = true;
        }
        return res;
    }

    /**
     * Verifies if contest status is OPEN, since it can only receive project applications or answer to project applications if that is the case
     *
     * @param contestId identifies contest
     * @return true if contest status is OPEN
     */
    public boolean verifyPermissionToApply(int contestId) {
        boolean res = false;

        entity.Contest contest = contestDao.find(contestId);

        if (contest != null) {
            if (contest.getStatus() == StatusContest.OPEN) {
                res = true;
            }
        }
        return res;
    }

    /**
     * Finds active project of user that makes the request and defines ContestApplication for given project and given contest
     * If it is first application of given project to given contest, a new ContestApplication is persisted in database
     * If there is already a ContestApplication for same project and same contest extra validations must occur
     * If project is accepted or waiting for response, nothing is changed - could happen if member of project didn't realize its active project has been accepted to participate in contest or proposed to contest already
     * If project has been rejected before, ContestApplication attributes are updated so that it displays new nature of relationship between project and contest
     *
     * @param contestId identifies contest
     * @param token     identifies session that makes the request and therefore active project to apply to contest
     * @return true if project application is concluded successfully
     */
    public boolean applyToContest(int contestId, String token) {
        boolean res = false;

        entity.Contest contest = contestDao.find(contestId);

        if (contest != null) {
            entity.User user = tokenDao.findUserEntByToken(token);

            if (user != null) {
                entity.Project project = projMemberDao.findActiveProjectByUserId(user.getUserId());

                if (project != null) {

                    ContestApplication applicationEnt = applicationDao.findApplicationForGivenContestIdAndProjectId(contest.getId(), project.getId());

                    if (applicationEnt != null) {
                        if (applicationEnt.isAccepted() || !applicationEnt.isAnswered()) {
                            // candidatura está aceite ou à espera de resposta
                            res = true; // não precisa de fazer nada
                        } else {
                            // candidatura foi previamente recusada

                            applicationEnt.setAnswered(false);
                            applicationEnt.setAccepted(false);
                            applicationDao.merge(applicationEnt);
                            project.setStatus(StatusProject.PROPOSED);
                            projDao.merge(project);
                            communicationBean.notifyAllContestManagers(2, contest.getTitle());
                            communicationBean.recordProjectStatusChange(project, null, 2);
                            res = true;
                            LOGGER.info("Project ID " + project.getId()+": " + project.getTitle() + " has applied to contest " + contestId+ ". IP Address of request is " + userBean.getIPAddress());

                        }
                    } else {
                        // não há relação, é a 1ª candidatura

                        ContestApplication application = new ContestApplication();
                        application.setContest(contest);
                        application.setProject(project);
                        application.setAnswered(false);
                        application.setAccepted(false);
                        project.setStatus(StatusProject.PROPOSED);
                        projDao.merge(project);
                        applicationDao.persist(application);
                        communicationBean.notifyAllContestManagers(2, contest.getTitle());
                        communicationBean.recordProjectStatusChange(project, null, 2);
                        res = true;
                        LOGGER.info("Project ID " + project.getId()+": " + project.getTitle() + " has applied to contest " + contestId+ ". IP Address of request is " + userBean.getIPAddress());

                    }
                }
            }
        }

        return res;
    }

    /**
     * Get list of all projects associated with given contest: accepted, rejected and waiting for an answer
     * @return list of Application DTO that contains minimum required information of project and its relationship with contest
     */
    public List<Application> getAllApplications(int contestId) {
        List<Application> list = new ArrayList<>();

        List<ContestApplication> applications = applicationDao.findApplicationsForGivenContestId(contestId);

        if (applications != null) {
            for (ContestApplication a : applications) {
                list.add(convertApplicationToDto(a));
            }
        }

        return list;
    }

    /**
     * Converts Application Entity to Application DTO so that information can be sent as HTTP response
     *
     * @param a contains information of application entity
     * @return Application information. Format is DTO
     */
    private Application convertApplicationToDto(ContestApplication a) {

        Application application = new Application();

        application.setId(a.getId());
        application.setProjectId(a.getProject().getId());
        application.setProjectStatus(a.getProject().getStatus().getStatus());
        application.setProjectTitle(a.getProject().getTitle());
        application.setProjectStatusInt(a.getProject().getStatus().ordinal());
        application.setAnswered(a.isAnswered());
        application.setAccepted(a.isAccepted());

        return application;
    }

    /**
     * Answers to project application to participate in contest
     * If project is rejected, its status will be READY
     * Before a project application is accepted, it must be checked if project status is not cancelled
     * If it's cancelled, it will not be accepted because it would be a dead end: project cancelled participating in a contest cannot be revived
     * If a project applications is accepted, it must verify if contest limit of participants has been reached and, if so, waiting applications must be refused
     *
     * @param token         identifies session that makes the request
     * @param applicationId identifies ContestApplication
     * @param answer:       int value that defines if project is rejected(0) or accepted (1)
     * @return
     */
    public boolean replyToApplication(String token, int applicationId, int answer) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {

            ContestApplication applicationEnt = applicationDao.find(applicationId);

            if (applicationEnt != null) {
                if (answer == 1) {
                    if (!verifyProjectIsReady(applicationEnt.getProject())) {
                        applicationEnt.setAnswered(true);
                        applicationEnt.setAccepted(true);
                        applicationDao.merge(applicationEnt);
                        applicationEnt.getProject().setStatus(StatusProject.APPROVED);
                        projDao.merge(applicationEnt.getProject());

                        communicationBean.recordProjectApplicationResult(null, applicationEnt.getProject(), answer);
                        //TODO testar notificacoes
                        verifyLimitApplicationsToContestHasBeanReached(applicationEnt.getContest());
                        res = true;
                        LOGGER.info("Project ID " + applicationEnt.getProject().getId()+": " + applicationEnt.getProject().getTitle() + " application to contest " + applicationEnt.getContest().getId()+ " has been accepted. IP Address of request is " + userBean.getIPAddress());

                    }
                } else if (answer == 0) {
                    applicationEnt.setAnswered(true);
                    applicationEnt.setAccepted(false);
                    applicationDao.merge(applicationEnt);
                    applicationEnt.getProject().setStatus(StatusProject.READY);
                    projDao.merge(applicationEnt.getProject());
                    res = true;
                    communicationBean.recordProjectApplicationResult(null, applicationEnt.getProject(), answer);
                    LOGGER.info("Project ID " + applicationEnt.getProject().getId()+": " + applicationEnt.getProject().getTitle() + " application to contest " + applicationEnt.getContest().getId()+ " has been rejected. IP Address of request is " + userBean.getIPAddress());

                }
                communicationBean.notifyProjectMembersOfApplicationResponse(applicationEnt.getProject(), answer);
            }
        }

        return res;
    }

    /**
     * Checks if accepted projects in given contest has reached limit of participating projects
     * If so, waiting applications must be refused
     * @param contest represents given contest
     */
    private void verifyLimitApplicationsToContestHasBeanReached(entity.Contest contest) {

        boolean res = checkApplicationsLimit(contest.getId());

        if (res) {
            refuseUnansweredApplications(contest);

        }

    }

    /**
     * Refuses any pending project applications to given contest
     * Useful when contest status is edited to ONGOING or when limit of participating projects of given contest has been reached
     *
     * @param contest represents contest
     */
    private void refuseUnansweredApplications(entity.Contest contest) {
        List<ContestApplication> applicationsWaitingForResponse = applicationDao.findApplicationsNotAnsweredForGivenContestId(contest.getId());

        if (applicationsWaitingForResponse != null) {
            for (ContestApplication a : applicationsWaitingForResponse) {
                a.setAnswered(true);
                a.setAccepted(false);
                applicationDao.merge(a);
                // TODO testar
                communicationBean.recordProjectApplicationResult(null, a.getProject(), 0);
                LOGGER.info("Application of project: " + a.getProject().getTitle() + " to contest:  " + contest.getTitle() + " has been rejected. IP Address of request is " + userBean.getIPAddress());

            }
        }
    }

    /**
     * Checks if given contest reached limit of participating projects in contest
     *
     * @param contestId identifies contest
     * @return true if limit has been reached - no more projects can be accepted to participate in contest
     */
    public boolean checkApplicationsLimit(int contestId) {
        boolean res = false;

        entity.Contest contest = contestDao.find(contestId);
        if (contest != null) {
            List<ContestApplication> list = applicationDao.findAcceptedApplicationsForGivenContestId(contestId);

            if (list != null) {
                if (list.size() == contest.getMaxNumberProjects()) {
                    res = true;
                    // limite foi atingido, não poderá aceitar mais projectos a concurso
                }
            }
        }
        return res;
    }

    /**
     * Verifies if start date of given task is equal or after start date of contest and finish Date of given task is equal or before finish Date of contest
     * @param editTask represents task with information to edit
     * @param taskEnt represents task entity of given task, to access project id
     * @return true if dates of given task are within ONGOING period of contest
     */
    public boolean newDatesAreWithinContestPeriod(entity.Task taskEnt, Task editTask) {

        boolean res = false;
        ContestApplication acceptedApplication = applicationDao.findAcceptedApplicationForGivenProjectId(taskEnt.getProject().getId());
        // representa a candidatura aceite do projecto da tarefa

        if (acceptedApplication != null) {
            entity.Contest contest = acceptedApplication.getContest();

            if ((editTask.getStartDate().equals(contest.getStartDate()) || editTask.getStartDate().after(contest.getStartDate())) && (editTask.getFinishDate().equals(contest.getFinishDate()) || editTask.getFinishDate().before(contest.getFinishDate()))) {
                res = true;
            }
        }
        return res;
    }

    /**
     * Verifies if it is appropriate to choose project winner for given contest: contest status must be ONGOING
     * Current date (Date.now()) must be after each final task date of each project participating in contest
     * @param contestId identifies given contest
     * @return true if a contest winner can be declared
     */
    public boolean verifyPermissionToChooseWinner(int contestId) {

        boolean res = false;
        entity.Contest contest = contestDao.find(contestId);
        if (contest != null) {
            if (contest.getStatus() == StatusContest.ONGOING) {
                if (checkProjectsAcceptedFinalTaskDates(contestId)) {
                    res = true; // pode escolher vencedor
                }
            }
        }
        return res;
    }

    /**
     * Verifies if final task date of projects participating in contest are before current date (Date.now())
     * This way, all projects have a chance to complete execution plan
     * It is not verified if final task status or project status is finished because that could prevent contest from being concluded
     * @param contestId identifies given contest
     * @return true if all final tasks dates have passed - a winner can be declared
     */
    private boolean checkProjectsAcceptedFinalTaskDates(int contestId) {
        boolean res = false;

        List<Project> projectsAccepted = applicationDao.findAcceptedProjectsForGivenContestId(contestId);
        if (projectsAccepted != null) {
            Date today = Date.from(Instant.now());
            int count = 0; // conta ocorrências em que data não é válida. Basta que uma data n seja válida para n permitir que se escolha vencedor

            for (Project p : projectsAccepted) {
                entity.Task finalTask = taskDao.findFinalTaskByProjectId(p.getId());

                if (finalTask != null) {
                    if (finalTask.getFinishDate().equals(today) || finalTask.getFinishDate().after(today)) {
                        count++;
                    }
                }
            }
            if (count == 0) {
                res = true; // pode escolher vencedor
            }
        }
        return res;
    }

    /**
     * Verifies if project to be declared contest winner has a FINISHED status
     * @param projId identifies project
     * @return true if project status is FINISHED
     */
    public boolean verifyProjectIsFinished(int projId) {
        boolean res = false;
        Project project = projDao.findProjectById(projId);

        if (project != null) {
            if (project.getStatus() == StatusProject.FINISHED) {
                res = true;
            }
        }
        return res;
    }

    /**
     * A project is declared winner of given contest
     * @param contestId identifies contest
     * @param projId identifies project
     * @param token identifies session that makes the request
     * @return true if project is declared contest winner successfully
     */
    public boolean chooseContestWinner(int contestId, int projId, String token) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Contest contest = contestDao.find(contestId);
            if (contest != null) {
                Project project = projDao.findProjectById(projId);

                if (project != null) {
                    contest.setWinner(project);
                    contestDao.merge(contest);
                    res = true;

                    communicationBean.notifyContestHasWinner(contest);
                    communicationBean.recordProjectDeclaredWinner(user, project, contest);
                    LOGGER.info("Project ID " + project.getId()+": " + project.getTitle() + " has been declared winner of contest " + contestId+ ". IP Address of request is " + userBean.getIPAddress());

                }
            }
        }
        return res;
    }

    /**
     * Gets statistics of given contest: duration of contest; number and % of projects that applied, by office; average of members per project;
     * Number and % of accepted projects, by office; Number and % of finished projects, by office; average time for projects' execution
     * @param contestId identifies given contest
     * @return a string containing all statistics details for given contest
     * @throws JsonProcessingException
     */
    public String statsContenst(int contestId) throws JsonProcessingException {


        HashMap<String, ArrayList<String>> stats = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");


        entity.Contest contest = contestDao.find(contestId);
        String title = contest.getTitle();
        String startDate = dateFormat.format(contest.getStartDate());
        String endDate = dateFormat.format(contest.getFinishDate());

        ArrayList<String> info = new ArrayList<>();
        info.add(title);
        info.add(startDate);
        info.add(endDate);

        stats.put("info", info);

        ArrayList<String> averages = new ArrayList<>();
        averages.add(averageElementsProject(contestId));
        averages.add(averageExecutionProject(contestId));

        stats.put("averages", averages);

        ArrayList<String> localStatsAll = projectsGivenAllLocalStats(contestId);
        ArrayList<String> localStatsAccepted = projectsAcceptedLocalStats(contestId);
        ArrayList<String> localStatsFinished = projectsFinishedLocalStats(contestId);


        String[] local = {"lisboa", "coimbra", "porto", "tomar", "viseu", "vilareal"};
        String[] localAccepted = {"lisboaaccepted", "coimbraaccepted", "portoaccepted", "tomaraccepted", "viseuaccepted", "vilarealaccepted"};
        String[] localFinished = {"lisboafinished", "coimbrafinished", "portofinished", "tomarfinished", "viseufinished", "vilarealfinished"};


        for (int i = 0; i < localStatsAll.size() - 1; i += 2) {
            String key = local[(i / 2)];
            ArrayList<String> pairList = new ArrayList<>();
            pairList.add(localStatsAll.get(i).toString());
            pairList.add(localStatsAll.get(i + 1).toString());
            stats.put(key, pairList);
        }


        for (int j = 0; j < localStatsAccepted.size() - 1; j += 2) {
            String keyAccepted = localAccepted[(j / 2)];
            ArrayList<String> accepted = new ArrayList<>();
            accepted.add(localStatsAccepted.get(j).toString());
            accepted.add(localStatsAccepted.get(j + 1).toString());
            stats.put(keyAccepted, accepted);
        }

        for (int j = 0; j < localStatsFinished.size() - 1; j += 2) {
            String keyFinished = localFinished[(j / 2)];
            ArrayList<String> finished = new ArrayList<>();
            finished.add(localStatsFinished.get(j).toString());
            finished.add(localStatsFinished.get(j + 1).toString());
            stats.put(keyFinished, finished);
        }


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(stats);


        return jsonData;

    }

    /**
     * Gets statistics of given contest: number and % of projects that applied, by office
     * @param contestId identifies given contest
     * @return list of string containing relevant information
     */
    public ArrayList<String> projectsGivenAllLocalStats(int contestId) {
        double lisboaCount = 0;
        double coimbraCount = 0;
        double portoCount = 0;
        double tomarCount = 0;
        double viseuCount = 0;
        double vilarealCount = 0;

        ArrayList<String> localAll = new ArrayList<>();

        List<ContestApplication> projectsGiven = applicationDao.findApplicationsForGivenContestId(contestId);
        double sizeProjects = applicationDao.findApplicationsForGivenContestId(contestId).size();

        for (ContestApplication contestApplication : projectsGiven) {
            int office = contestApplication.getProject().getOffice().ordinal();
            if (office == 0) {
                lisboaCount++;
            } else if (office == 1) {
                coimbraCount++;
            } else if (office == 2) {
                portoCount++;
            } else if (office == 3) {
                tomarCount++;
            } else if (office == 4) {
                viseuCount++;
            } else if (office == 5) {
                vilarealCount++;
            }
        }

        String lisboaPercentage = String.format("%.2f", (lisboaCount / sizeProjects) * 100);
        String coimbraPercentage = String.format("%.2f", (coimbraCount / sizeProjects) * 100);
        String portoPercentage = String.format("%.2f", (portoCount / sizeProjects) * 100);
        String tomarPercentage = String.format("%.2f", (tomarCount / sizeProjects) * 100);
        String viseuPercentage = String.format("%.2f", (viseuCount / sizeProjects) * 100);
        String vilarealPercentage = String.format("%.2f", (vilarealCount / sizeProjects) * 100);


        localAll.add(String.valueOf(lisboaCount));
        localAll.add(lisboaPercentage);

        localAll.add(String.valueOf(coimbraCount));
        localAll.add(coimbraPercentage);

        localAll.add(String.valueOf(portoCount));
        localAll.add(portoPercentage);

        localAll.add(String.valueOf(tomarCount));
        localAll.add(tomarPercentage);

        localAll.add(String.valueOf(viseuCount));
        localAll.add(viseuPercentage);

        localAll.add(String.valueOf(vilarealCount));
        localAll.add(vilarealPercentage);

        return localAll;

    }
    /**
     * Gets statistics of given contest: number and % of accepted projects, by office
     * @param contestId identifies given contest
     * @return list of string containing relevant information
     */
    public ArrayList<String> projectsAcceptedLocalStats(int contestId) {
        double lisboaCount = 0;
        double coimbraCount = 0;
        double portoCount = 0;
        double tomarCount = 0;
        double viseuCount = 0;
        double vilarealCount = 0;

        ArrayList<String> projectsAccepted = new ArrayList<>();


        List<ContestApplication> projectsGiven = applicationDao.findApplicationsForGivenContestId(contestId);
        double sizeProjects = applicationDao.findApplicationsForGivenContestId(contestId).size();

        for (ContestApplication contestApplication : projectsGiven) {
            int office = contestApplication.getProject().getOffice().ordinal();

            if (contestApplication.isAccepted() && contestApplication.isAnswered()) {
                if (office == 0) {
                    lisboaCount++;
                } else if (office == 1) {
                    coimbraCount++;
                } else if (office == 2) {
                    portoCount++;
                } else if (office == 3) {
                    tomarCount++;
                } else if (office == 4) {
                    viseuCount++;
                } else if (office == 5) {
                    vilarealCount++;
                }

            }
        }
        String lisboaPercentage = String.format("%.2f", (lisboaCount / sizeProjects) * 100);
        String coimbraPercentage = String.format("%.2f", (coimbraCount / sizeProjects) * 100);
        String portoPercentage = String.format("%.2f", (portoCount / sizeProjects) * 100);
        String tomarPercentage = String.format("%.2f", (tomarCount / sizeProjects) * 100);
        String viseuPercentage = String.format("%.2f", (viseuCount / sizeProjects) * 100);
        String vilarealPercentage = String.format("%.2f", (vilarealCount / sizeProjects) * 100);


        projectsAccepted.add(String.valueOf(lisboaCount));
        projectsAccepted.add(lisboaPercentage);

        projectsAccepted.add(String.valueOf(coimbraCount));
        projectsAccepted.add(coimbraPercentage);

        projectsAccepted.add(String.valueOf(portoCount));
        projectsAccepted.add(portoPercentage);

        projectsAccepted.add(String.valueOf(tomarCount));
        projectsAccepted.add(tomarPercentage);

        projectsAccepted.add(String.valueOf(viseuCount));
        projectsAccepted.add(viseuPercentage);

        projectsAccepted.add(String.valueOf(vilarealCount));
        projectsAccepted.add(vilarealPercentage);


        return projectsAccepted;


    }
    /**
     * Gets statistics of given contest: number and % of finished projects, by office
     * @param contestId identifies given contest
     * @return list of string containing relevant information
     */
    public ArrayList<String> projectsFinishedLocalStats(int contestId) {
        double lisboaCount = 0;
        double coimbraCount = 0;
        double portoCount = 0;
        double tomarCount = 0;
        double viseuCount = 0;
        double vilarealCount = 0;

        ArrayList<String> projectsFinished = new ArrayList<>();

        List<ContestApplication> projectsGiven = applicationDao.findApplicationsForGivenContestId(contestId);
        double sizeProjects = applicationDao.findApplicationsForGivenContestId(contestId).size();

        for (ContestApplication contestApplication : projectsGiven) {
            int office = contestApplication.getProject().getOffice().ordinal();
            boolean valid = contestApplication.isAccepted() && contestApplication.isAnswered() && contestApplication.getProject().getStatus().ordinal() == 6;
            if (valid) {
                if (office == 0) {
                    lisboaCount++;
                } else if (office == 1) {
                    coimbraCount++;
                } else if (office == 2) {
                    portoCount++;
                } else if (office == 3) {
                    tomarCount++;
                } else if (office == 4) {
                    viseuCount++;
                } else if (office == 5) {
                    vilarealCount++;
                }
            }
        }
        String lisboaPercentage = String.format("%.2f", (lisboaCount / sizeProjects) * 100);
        String coimbraPercentage = String.format("%.2f", (coimbraCount / sizeProjects) * 100);
        String portoPercentage = String.format("%.2f", (portoCount / sizeProjects) * 100);
        String tomarPercentage = String.format("%.2f", (tomarCount / sizeProjects) * 100);
        String viseuPercentage = String.format("%.2f", (viseuCount / sizeProjects) * 100);
        String vilarealPercentage = String.format("%.2f", (vilarealCount / sizeProjects) * 100);


        projectsFinished.add(String.valueOf(lisboaCount));
        projectsFinished.add(lisboaPercentage);

        projectsFinished.add(String.valueOf(coimbraCount));
        projectsFinished.add(coimbraPercentage);

        projectsFinished.add(String.valueOf(portoCount));
        projectsFinished.add(portoPercentage);

        projectsFinished.add(String.valueOf(tomarCount));
        projectsFinished.add(tomarPercentage);

        projectsFinished.add(String.valueOf(viseuCount));
        projectsFinished.add(viseuPercentage);

        projectsFinished.add(String.valueOf(vilarealCount));
        projectsFinished.add(vilarealPercentage);


        return projectsFinished;


    }
    /**
     * Gets statistics of given contest: average of members per project;
     * @param contestId identifies given contest
     * @return string containing relevant information
     */
    public String averageElementsProject(int contestId) {

        List<ContestApplication> projectsGiven = applicationDao.findApplicationsForGivenContestId(contestId);
        double sizeProjects = applicationDao.findApplicationsForGivenContestId(contestId).size();
        double countmembers = 0;

        for (ContestApplication contestApplication : projectsGiven) {
            countmembers += contestApplication.getProject().getMembersNumber();

        }
        return String.format("%.2f", countmembers / sizeProjects);

    }

    /**
     * Gets statistics of given contest: average time for projects' execution
     * @param contestId identifies given contest
     * @return string containing relevant information
     */
    public String averageExecutionProject(int contestId) {

        List<ContestApplication> projectsGiven = applicationDao.findApplicationsForGivenContestId(contestId);
        double sizeProjects = applicationDao.findAcceptedProjectsForGivenContestId(contestId).size();
        long countDays = 0;

        for (ContestApplication contestApplication : projectsGiven) {
            boolean valid = contestApplication.isAnswered() && contestApplication.isAccepted();
            boolean finished = contestApplication.getProject().getStatus().ordinal() == 6;
            if (valid && finished) {
                long startDate = contestApplication.getContest().getStartDate().getTime();
                long finishedDate = contestApplication.getProject().getFinishDate().getTime();
                countDays += Math.abs(startDate - finishedDate);

            }
        }
        long days = countDays / (1000 * 60 * 60 * 24);

        String result = String.format("%.2f", days / sizeProjects);


        return result;
    }

    /**
     * Verifies if a new contest can be created, because there can only be 1 PLANNING and 1 OPEN at all times
     *
     * @return true if a new contest can be created
     */
    public boolean verifyPermissionToAddNewContest() {
        boolean res = false;

        Long count = contestDao.countPlanningContest();
        if (count == 0) {
            res = true;
        }
        return res;
    }

    /**
     * Get list of contests whose title contains given input (str)
     * @param token identifies session that makes the request
     * @param str represents input that is written by user in frontend
     * @return list of Contest DTO that contains information of contest
     */
    public List<dto.Contest> filterContestsByName(String token, String str) {
        List<dto.Contest> list = new ArrayList<>();
        List<entity.Contest> contestList = contestDao.findContestListContainingStr(str.toLowerCase());

        if (contestList != null) {
            for (entity.Contest c : contestList) {
                list.add(convertContestEntToDto(c));
            }
        }
        return list;
    }
    /**
     * Get list of contests whose startOpenCall date is equal or after given input (startDate)
     * Shows list of contests whose open call is now open or will open in the future
     * startDate needs to be converted to Date()
     * @param startDate represents date input that is selected by user in frontend
     * @return list of Contest DTO that contains information of contest
     */
    public List<dto.Contest> filterContestsByStartDate(String startDate) {
        List<dto.Contest> list = new ArrayList<>();
        long timestamp = Long.parseLong(startDate);
        Date date = new Date(timestamp);
        List<entity.Contest> contestList = contestDao.findContestListWhoseStartOpenCallDateEqualOrAfterGivenDate(date);
        if (contestList != null) {
            for (entity.Contest c : contestList) {
                list.add(convertContestEntToDto(c));
            }
        }
        return list;
    }
    /**
     * Get list of contests whose finishDate date is equal or before given input (finishDate)
     * Shows list of contests whose finishDate is now or already happened
     * finishDate needs to be converted to Date()
     * @param finishDate represents date input that is selected by user in frontend
     * @return list of Contest DTO that contains information of contest
     */
    public List<dto.Contest> filterContestsByFinishDate(String finishDate) {
        List<dto.Contest> list = new ArrayList<>();
        long timestamp = Long.parseLong(finishDate);
        Date date = new Date(timestamp);
        List<entity.Contest> contestList = contestDao.findContestListWhoseFinishDateEqualOrBeforeGivenDate(date);
        if (contestList != null) {
            for (entity.Contest c : contestList) {
                list.add(convertContestEntToDto(c));
            }
        }
        return list;
    }

    /**
     * Checks if project status, whose application response will be accepted by contest manager is CANCELLED
     *
     * @param project represents project
     * @return true if project status is CANCELLED, therefore its application cannot be accepted
     */
    public boolean verifyProjectIsReady(Project project) {
        boolean res = false;

        if (project.getStatus() == StatusProject.CANCELLED) {
            res = true;
        }
        return res;
    }
}










