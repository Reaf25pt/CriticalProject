package bean;

import ENUM.Office;
import ENUM.StatusProject;
import ENUM.StatusTask;
import dto.*;
import entity.Contest;
import entity.ContestApplication;
import entity.ProjectChatMessage;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Project.class);
    @EJB
    dao.User userDao;
    @EJB
    dao.Token tokenDao;
    @EJB
    dao.Project projDao;
    @EJB
    dao.Keyword keywordDao;
    @Inject
    User userBean;
    @EJB
    dao.ProjectMember projMemberDao;
    @Inject
    Communication communicationBean;
    @EJB
    dao.Task taskDao;
    @EJB
    dao.Skill skillDao;
    @EJB
    dao.Contest contestDao;
    @EJB
    dao.ContestApplication applicationDao;
    @Inject
    bean.Contest contestBean;
    @EJB
    dao.ProjectHistory recordDao;
    @EJB
    dao.ProjectChatMessage projChatDao;


    public Project() {
    }

    /**
     * Converts project entity to DTO, including skills and keywords associated with project
     *
     * @param p represents project entity
     * @return Project DTO
     */
    public dto.Project convertProjEntityToDto(entity.Project p) {

        dto.Project projDto = new dto.Project();

        projDto.setId(p.getId());
        projDto.setTitle(p.getTitle());

        if (p.getOffice() != null) {
            projDto.setOfficeInfo(p.getOffice().getCity());
            projDto.setOffice(p.getOffice().ordinal());
        } else {
            projDto.setOffice(20);
        }
        projDto.setDetails(p.getDetails());
        projDto.setResources(p.getResources());
        projDto.setStatus(p.getStatus().getStatus());
        projDto.setStatusInt(p.getStatus().ordinal());
        projDto.setAvailableSpots(getNumberOfAvailableSpots(p));

        projDto.setMembersNumber(p.getMembersNumber());
        projDto.setCreationDate(p.getCreationDate());
        projDto.setFinishedDate(p.getFinishDate());

        if (p.getListKeywords() != null) {
            projDto.setKeywords(convertListKeywordsDTO(p.getListKeywords()));
        }

        if (p.getListSkills() != null) {
            projDto.setSkills(convertListSkillsDTO(p.getListSkills()));
        }
        return projDto;
    }

    /**
     * Converts a list of keyword entities into list of keyword DTOs
     *
     * @param listKeywords representes list of keyword entities
     * @return list of keyword DTOs
     */
    private List<Keyword> convertListKeywordsDTO(List<entity.Keyword> listKeywords) {

        List<Keyword> listKeywordDTO = new ArrayList<>();

        for (entity.Keyword k : listKeywords) {
            Keyword keyw = new Keyword();
            keyw.setId(k.getId());
            keyw.setTitle(k.getTitle());
            listKeywordDTO.add(keyw);
        }
        return listKeywordDTO;
    }

    /**
     * Converts a list of skill entities into list of skill DTOs
     *
     * @param listSkills representes list of skill entities
     * @return list of skill DTOs
     */
    public List<Skill> convertListSkillsDTO(List<entity.Skill> listSkills) {

        List<Skill> listSkillsDTO = new ArrayList<>();

        for (entity.Skill s : listSkills) {
            Skill skill = new Skill();
            skill.setId(s.getSkillId());
            skill.setTitle(s.getTitle());
            skill.setSkillType(s.getType().ordinal());
            listSkillsDTO.add(skill);
        }
        return listSkillsDTO;
    }

    /**
     * Creates and persists in database a new project
     * Associates keywords and skills with new project
     * Defines token that makes the request has project manager
     *
     * @param project contains information to define new project
     * @param token   represents session of logged user that makes the request
     * @return true if new project is persisted in database successfully
     */
    public boolean createNewProject(dto.Project project, String token) {

        boolean res = false;

        entity.User userEnt = tokenDao.findUserEntByToken(token);

        if (userEnt != null) {
            if (project != null && !projInfoIsFilledIn(project)) {

                entity.Project newProjEnt = new entity.Project();
                newProjEnt.setCreationDate(Date.from(Instant.now()));
                newProjEnt.setStatus(StatusProject.PLANNING);
                newProjEnt.setTitle(project.getTitle());
                newProjEnt.setDetails(project.getDetails());

                if (project.getOffice() != 20) {
                    newProjEnt.setOffice(setOffice(project.getOffice()));
                }

                if (project.getResources() != null) {
                    newProjEnt.setResources(project.getResources());
                }

                if (project.getMembersNumber() != 0) {
                    newProjEnt.setMembersNumber(project.getMembersNumber());
                } else {
                    newProjEnt.setMembersNumber(4);
                }

                projDao.persist(newProjEnt);

                LOGGER.info("User whose user ID is " + userEnt.getUserId() + " creates a new project, project ID: " + newProjEnt.getId() + ". IP Address of request is " + userBean.getIPAddress());

                associateCreatorToProject(userEnt, newProjEnt);

                associateKeywordsWithProject(project.getKeywords(), newProjEnt);

                if (project.getSkills() != null || project.getSkills().size() != 0) {
                    associateSkillsWithProject(project.getSkills(), newProjEnt);
                }

                res = true;

                communicationBean.recordProjectCreation(newProjEnt, userEnt);
            }
        }
        return res;
    }

    /**
     * Defines user that creates a new project as its manager
     *
     * @param user    represents user that creates new project
     * @param project represents new project
     */
    private void associateCreatorToProject(entity.User user, entity.Project project) {

        entity.ProjectMember projMember = new entity.ProjectMember();
        projMember.setProjectToParticipate(project);
        projMember.setUserInvited(user);

        projMember.setManager(true);

        projMember.setAnswered(true);
        projMember.setAccepted(true);
        projMember.setRemoved(false);

        projMemberDao.persist(projMember);

        project.getListPotentialMembers().add(projMember);
        user.getListProjects().add(projMember);

        projDao.merge(project);
        userDao.merge(user);

        LOGGER.info("User whose ID is " + user.getUserId() + " is defined as manager of project, project ID: " + project.getId() + ". IP Address of request is " + userBean.getIPAddress());

    }

    /**
     * Associates keyword(s) with given project
     * First, all keywords associated with given project have such association deleted, so that updated list of keywords is associated with project thus preventing duplication or problems with hypothetical keywords that were no longer to be associated with project (intended to be removed)
     * If keyword title is already persisted in dabatase, keyword is associated with project
     * If keyword title is not found in database, a new keyword is created and persisted in dabatase. Keyword is then associated with project
     *
     * @param keywords   represents list of keywords to be associated with given project
     * @param newProjEnt represents given project
     */
    private void associateKeywordsWithProject(List<Keyword> keywords, entity.Project newProjEnt) {

        deleteKeywordsAssociatedWithProject(newProjEnt);

        for (Keyword k : keywords) {
            entity.Keyword keyw = keywordDao.findKeywordByTitle(k.getTitle().trim());

            if (keyw != null) {
                // já existe na DB, verifica se já tem relação com proj. Desnecessário porque nunca vai ter relação com keywords, à partida

                Long count = keywordDao.findRelationBetweenProjAndKeyword(keyw.getId(), newProjEnt.getId());
                if (count == 0) {
                    // significa que n há relação entre keyword e proj
                    keyw.getListProject_Keywords().add(newProjEnt);
                    newProjEnt.getListKeywords().add(keyw);

                    projDao.merge(newProjEnt);
                    keywordDao.merge(keyw);

                    LOGGER.info("Keyword " + keyw.getId() + " is associated with project, project ID: " + newProjEnt.getId() + ". IP Address of request is " + userBean.getIPAddress());
                }


            } else {
                // não existe keyword para o title - É necessário criar e adicionar à DB
                entity.Keyword newKeyW = new entity.Keyword();
                newKeyW.setTitle(k.getTitle().trim());
                newKeyW.getListProject_Keywords().add(newProjEnt);

                keywordDao.persist(newKeyW);
                newProjEnt.getListKeywords().add(newKeyW);
                projDao.merge(newProjEnt);

                LOGGER.info("Keyword " + newKeyW.getId() + " is persisted in database and associated with project, project ID: " + newProjEnt.getId() + ". IP Address of request is " + userBean.getIPAddress());
            }
        }
    }

    /**
     * Deletes relationship keyword - project for all keywords associated with given project
     * Allows to then add list of keywords to be associated with project, preventing duplications or problems with hypothetical keywords that were no longer to be associated with project (intended to be removed)
     *
     * @param newProjEnt represents given project
     */
    private void deleteKeywordsAssociatedWithProject(entity.Project newProjEnt) {

        List<entity.Keyword> list = keywordDao.findListOfKeywordsByProjId(newProjEnt.getId());

        if (list != null) {
            for (entity.Keyword k : list) {

                k.getListProject_Keywords().remove(newProjEnt);
                newProjEnt.getListKeywords().remove(k);
                keywordDao.merge(k);
                projDao.merge(newProjEnt);
            }
        }
    }

    /**
     * Associates skill(s) with given project
     * First, all skills associated with given project have such association deleted, so that updated list of skills is associated with project thus preventing duplication or problems with hypothetical skills that were no longer to be associated with project (intended to be removed)
     * If skill title is already persisted in dabatase, skill is associated with project
     * If skill title is not found in database, a new skill is created and persisted in dabatase. Skill is then associated with project
     *
     * @param skills     represents list of skills to be associated with given project
     * @param newProjEnt represents given project
     */
    private void associateSkillsWithProject(List<Skill> skills, entity.Project newProjEnt) {

        deleteSkillsAssociatedWithProject(newProjEnt);

        for (Skill s : skills) {

            entity.Skill skill = skillDao.findSkillByTitle(s.getTitle().trim());

            if (skill != null) {
                // já existe na DB, basta associar ao proj

                Long count = skillDao.findRelationBetweenProjAndSkill(skill.getSkillId(), newProjEnt.getId());

                if (count == 0) {
                    skill.getListProject_Skills().add(newProjEnt);
                    newProjEnt.getListSkills().add(skill);

                    projDao.merge(newProjEnt);
                    skillDao.merge(skill);

                    LOGGER.info("Skill " + skill.getSkillId() + " is associated with project, project ID: " + newProjEnt.getId() + ". IP Address of request is " + userBean.getIPAddress());
                }

            } else {
                // não existe skill para o title usado. É necessário criar e adicionar à DB

                entity.Skill newSkill = new entity.Skill();
                newSkill.setTitle(s.getTitle().trim());
                userBean.attributeSkillType(s.getSkillType(), newSkill);
                newSkill.getListProject_Skills().add(newProjEnt);

                skillDao.persist(newSkill);
                newProjEnt.getListSkills().add(newSkill);
                projDao.merge(newProjEnt);

                LOGGER.info("Skill " + newSkill.getSkillId() + " is persisted in database and associated with project, project ID: " + newProjEnt.getId() + ". IP Address of request is " + userBean.getIPAddress());
            }

        }
    }

    /**
     * Deletes relationship skill - project for all skills associated with given project
     * Allows to then add list of skills to be associated with project, preventing duplications or problems with hypothetical skills that were no longer to be associated with project (intended to be removed)
     *
     * @param newProjEnt represents given project
     */
    private void deleteSkillsAssociatedWithProject(entity.Project newProjEnt) {

        List<entity.Skill> list = skillDao.findListOfSkillsByProjId(newProjEnt.getId());

        if (list != null) {
            for (entity.Skill s : list) {
                s.getListProject_Skills().remove(newProjEnt);
                newProjEnt.getListSkills().remove(s);

                skillDao.merge(s);
                projDao.merge(newProjEnt);
            }
        }
    }

    private boolean projInfoIsFilledIn(dto.Project project) {
        // registo inicial do proj tem de incluir, no mínimo nome e descrição e ainda 1 keyword associada ao proj
        boolean res = false;

        if (userBean.checkStringInfo(project.getTitle()) || userBean.checkStringInfo(project.getDetails()) || project.getKeywords().isEmpty()) {
            res = true;
            // projecto não inclui info indispensável no momento da sua criação
        }

        return res;
    }

    /**
     * Adds a ProjectMember relationship for user and project if it is not defined yet
     * Updates existing ProjectMember relationship if it's already defined
     * An existing relationship can be removed (user left project and wants to participate again or invited again)
     * An existing relationship can be not removed and not answered - waiting for response. Nothing is changed
     * An existing relationship can be not removed and answered - previous invitation refused but user is again invited or self-invites itself
     * If tokenId === userId is a self-invite to participate in project, if not it's a user added by project manager
     *
     * @param projId identifies project
     * @param userId identifies user to be added to given project
     * @param token  identifies session that makes the request
     * @return
     */
    public boolean addMemberToProject(int projId, int userId, String token) {

        boolean res = false;

        entity.User user = userDao.findUserById(userId); // a quem convite diz respeito
        entity.User userEnt = tokenDao.findUserEntByToken(token);
        entity.Project project = projDao.findProjectById(projId);

        if (user != null && userEnt != null && project != null) {

            entity.ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, userId);

            if (pm != null) {
                //existe relação pm

                if (!pm.isRemoved()) {
                    // não está removed
                    if (pm.isAnswered()) {
                        // está respondido - recusado (pq os users aceites não são sugeridos no frontend. User pode ter decidido tentar novamente participar

                        pm.setManager(false);
                        pm.setRemoved(false);
                        pm.setAccepted(false);
                        pm.setAnswered(false);

                        if (userEnt.getUserId() == userId) {
                            // self-invitation to participate in project
                            pm.setSelfInvitation(true);

                            pm.getProjectToParticipate().getListPotentialMembers().add(pm);
                            pm.getUserInvited().getListProjects().add(pm);

                            projDao.merge(pm.getProjectToParticipate());
                            userDao.merge(pm.getUserInvited());
                            projMemberDao.merge(pm);

                            communicationBean.notifyNewPossibleProjectMember(pm, pm.getProjectToParticipate(), pm.getUserInvited(), false);
                            res = true;
                            LOGGER.info("User ID " + userId + " asks to participate in project ID " + pm.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());

                        } else {
                            // not self-invitation
                            pm.setSelfInvitation(false);
                            pm.getProjectToParticipate().getListPotentialMembers().add(pm);
                            pm.getUserInvited().getListProjects().add(pm);

                            projDao.merge(pm.getProjectToParticipate());
                            userDao.merge(pm.getUserInvited());
                            projMemberDao.merge(pm);

                            communicationBean.notifyNewPossibleProjectMember(pm, pm.getProjectToParticipate(), pm.getUserInvited(), true);
                            res = true;
                            LOGGER.info("User ID " + userId + " is invited to participate in project ID " + pm.getProjectToParticipate().getId() + " by project manager whose account ID is " + userEnt.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

                        }
                    } else {
                        // está à espera de resposta, não faz nada mesmo que o campo self-invitation pudesse ser alterado
                        res = true;
                    }

                } else {
                    // está removido do projecto mas pode querer participar novamente / novamente convidado
                    pm.setManager(false);
                    pm.setRemoved(false);
                    pm.setAccepted(false);
                    pm.setAnswered(false);

                    if (userEnt.getUserId() == userId) {
                        // self-invitation to participate in project

                        pm.setSelfInvitation(true);

                        pm.getProjectToParticipate().getListPotentialMembers().add(pm);
                        pm.getUserInvited().getListProjects().add(pm);

                        projDao.merge(pm.getProjectToParticipate());
                        userDao.merge(pm.getUserInvited());
                        projMemberDao.merge(pm);

                        communicationBean.notifyNewPossibleProjectMember(pm, pm.getProjectToParticipate(), pm.getUserInvited(), false);
                        res = true;
                        LOGGER.info("User ID " + userId + " asks to participate in project ID " + pm.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());

                    } else {
                        // not self-invitation
                        pm.setSelfInvitation(false);
                        projMemberDao.merge(pm);
                        pm.getProjectToParticipate().getListPotentialMembers().add(pm);
                        pm.getUserInvited().getListProjects().add(pm);

                        projDao.merge(pm.getProjectToParticipate());
                        userDao.merge(pm.getUserInvited());

                        communicationBean.notifyNewPossibleProjectMember(pm, pm.getProjectToParticipate(), pm.getUserInvited(), true);
                        res = true;
                        LOGGER.info("User ID " + userId + " is invited to participate in project ID " + pm.getProjectToParticipate().getId() + " by project manager whose account ID is " + userEnt.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

                    }
                }
            } else {

                // não há relação prévia. é preciso criar nova associação entre user e projecto
                if (userEnt.getUserId() == userId) {
                    // self-invitation to participate in project
                    entity.ProjectMember projMember = associateUserToProject(user, project, true);
                    communicationBean.notifyNewPossibleProjectMember(projMember, project, user, false);
                    res = true;
                    LOGGER.info("User ID " + userId + " asks to participate in project ID " + pm.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());

                } else {
                    // not self-invitation
                    entity.ProjectMember projMember = associateUserToProject(user, project, false);
                    communicationBean.notifyNewPossibleProjectMember(projMember, project, user, true);
                    res = true;
                    LOGGER.info("User ID " + userId + " is invited to participate in project ID " + pm.getProjectToParticipate().getId() + " by project manager whose account ID is " + userEnt.getUserId() + ". IP Address of request is " + userBean.getIPAddress());
                }
            }
        }

        return res;
    }

    /**
     * Persists in table projectmembers a new association between user and project
     *
     * @param user       represents user to participate in project, if invite is accepted
     * @param project    represents project
     * @param selfInvite is true if user self-invites itself to participate in project; Is false if user is invited by project manager
     * @return ProjectMember DTO that defines relationship between user and project
     */
    private entity.ProjectMember associateUserToProject(entity.User user, entity.Project project, boolean selfInvite) {

        entity.ProjectMember projMember = new entity.ProjectMember();
        projMember.setProjectToParticipate(project);
        projMember.setUserInvited(user);
        projMember.setManager(false);
        projMember.setAnswered(false);
        projMember.setAccepted(false);
        projMember.setRemoved(false);

        if (selfInvite) {
            projMember.setSelfInvitation(true);
        } else {
            projMember.setSelfInvitation(false);
        }

        projMemberDao.persist(projMember);

        project.getListPotentialMembers().add(projMember);
        user.getListProjects().add(projMember);

        projDao.merge(project);
        userDao.merge(user);


        return projMember;
    }

    /**
     * Verifies if user that makes the request is project manager to be allowed to complete the request
     * There is only one entry in table ProjectMembers for each relationship between 1 user and 1 project
     * Attributes that define such relationship are updated when necessary
     *
     * @param token  identifies session that makes the request
     * @param projId identifies project
     * @return true if token is indeed project manager
     */
    public boolean isProjManager(String token, int projId) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.ProjectMember projMember = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, user.getUserId());

            if (projMember != null) {
                if (!projMember.isRemoved()) {
                    if (projMember.isManager()) {
                        res = true;
                        // token que faz request é manager do projecto, tendo permissão para fazer o request
                    }
                }
            }
        }

        return res;
    }

    /**
     * Gets all projects persisted in database
     *
     * @return list of Project DTO
     */
    public List<dto.Project> getAllProjectsList() {

        List<dto.Project> projectsList = new ArrayList<>();

        List<entity.Project> list = projDao.findAll();

        for (entity.Project p : list) {
            projectsList.add(convertProjEntityToDto(p));

        }
        return projectsList;
    }

    /**
     * Gets project information, including skills and keywords associated
     * Includes information of token relationship with project: manager, normal member or app user
     * Includes information if project is a winner of contest
     *
     * @param token identifies session that makes the request
     * @param id    identifies project
     * @return Project DTO
     */
    public dto.Project getProject(String token, int id) {

        dto.Project project = new dto.Project();

        entity.Project projEnt = projDao.findProjectById(id);

        if (projEnt != null) {
            entity.User user = tokenDao.findUserEntByToken(token);
            if (user != null) {
                project = convertProjEntityToDto(projEnt);

                entity.ProjectMember projMember = projMemberDao.findProjectMemberByProjectIdAndUserId(projEnt.getId(), user.getUserId());

                if (projMember != null) {
                    if (projMember.isAccepted() && !projMember.isRemoved()) {
                        // significa que user tem relação com projecto. Resta saber se membro ou gestor
                        project.setMember(true);
                        if (projMember.isManager()) {
                            project.setManager(true);
                        } else {
                            project.setManager(false);
                        }
                    } else if (projMember.isRemoved()) {
                        // já teve relação mas já não tem
                        project.setManager(false);
                        project.setMember(false);
                    }
                } else {
                    // significa que user n tem relação com projecto
                    project.setMember(false);
                    project.setManager(false);
                }
                if (projEnt.getStatus() == StatusProject.FINISHED) {
                    //could be a winner
                    project.setWinner(checksIfProjectIsWinner(id));
                } else {
                    project.setWinner(false);
                }
            }
        }
        return project;
    }

    /**
     * Checks if project has been declared winner of contest
     *
     * @param id identifies project
     * @return true if project is winner of contest
     */
    private boolean checksIfProjectIsWinner(int id) {
        boolean res = false;
        List<dto.Project> winnerProjects = filterWinnerProjects();
        if (winnerProjects != null) {
            for (dto.Project p : winnerProjects) {
                if (p.getId() == id) {
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * Gets list of active project members (projectMembers accepted and not removed) of given project
     *
     * @param id identifies project
     * @return list of ProjectMember DTO
     */
    public List<dto.ProjectMember> getProjectMembers(int id) {

        List<dto.ProjectMember> members = new ArrayList<>();

        List<entity.ProjectMember> membersList = projMemberDao.findListOfMembersByProjectId(id);

        if (membersList != null) {

            for (entity.ProjectMember p : membersList) {
                dto.ProjectMember pm = new dto.ProjectMember();
                pm.setId(p.getId());
                pm.setProjectId(p.getProjectToParticipate().getId());
                pm.setUserInvitedId(p.getUserInvited().getUserId());
                pm.setUserInvitedFirstName(p.getUserInvited().getFirstName());
                pm.setUserInvitedLastName(p.getUserInvited().getLastName());
                if (p.getUserInvited().getPhoto() != null) {
                    pm.setUserInvitedPhoto(p.getUserInvited().getPhoto());
                }
                pm.setManager(p.isManager());

                members.add(pm);
            }
        }

        return members;
    }

    /**
     * Get list of keywords whose name contains given input (title), to suggest
     *
     * @param title represents input that is written by user in frontend
     * @return list of Keyword - DTO that contains information of given keyword
     */
    public List<Keyword> getKeywordsList(String title) {

        List<Keyword> listDto = new ArrayList<>();

        List<entity.Keyword> listEnt = keywordDao.findKeywordListContainingStr(title.toLowerCase());

        if (listEnt != null) {
            listDto = convertListKeywordsDTO(listEnt);
        }
        return listDto;
    }

    /**
     * Verifies needed validations to add a new task to a project, depending on project's status
     * If project status is PLANNING, task can be added with no extra validation
     * If project status is IN PROGRESS, task can only be added if its finish date is before final task start date
     *
     * @param projId identifies project
     * @param task   represents task details to be persisted in database
     * @param token  identifies session that makes the request
     * @return true if task is added to given project successfully
     */
    public boolean addTaskDependingOnProjectStatus(int projId, Task task, String token) {
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);

        if (project != null) {
            if (project.getStatus() == StatusProject.PLANNING) {
                res = addTaskToProject(projId, task, token);
            } else if (project.getStatus() == StatusProject.PROGRESS) {
                if (validateTaskDatesBeforeFinalTaskDates(task, project)) {
                    res = addTaskToProject(projId, task, token);
                }
            }
        }
        return res;
    }

    /**
     * Verifies if finish date of task to be added to project is before final task start date
     *
     * @param task    represents task details to be persisted in database
     * @param project represents project
     * @return true if dates are valid and task can be added to given project
     */
    private boolean validateTaskDatesBeforeFinalTaskDates(Task task, entity.Project project) {
        boolean res = false;
        entity.Task finalTask = taskDao.findFinalTaskByProjectId(project.getId());
        if (finalTask != null) {
            if (finalTask.getStartDate().after(task.getFinishDate())) {
                res = true;
            }
        }
        return res;
    }


    /**
     * Adds a new task to given project
     * TaskOwner must be a project member (active and not removed)
     * In case task to be added has pre-required tasks that must be associated with new task to be added, an association between new task and pre-required task is defined
     * If new task dates clash with pre-required task dates, association between 2 tasks is not defined
     *
     * @param projId identifies project
     * @param task   represents task details to be persisted in database
     * @param token  identifies session that makes the request
     * @return true if a new task is successfully persisted in database and associated with given project
     */
    public boolean addTaskToProject(int projId, Task task, String token) {
        boolean res = false;
        entity.Task newTask = new entity.Task();
        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            if (task != null) {
                entity.ProjectMember pm = projMemberDao.findActiveProjectMemberByProjectIdAndUserId(projId, task.getTaskOwnerId());
                if (pm.getUserInvited() != null) {

                    newTask.setTitle(task.getTitle());
                    newTask.setStartDate(task.getStartDate());
                    newTask.setFinishDate(task.getFinishDate());
                    newTask.setDetails(task.getDetails());

                    newTask.setAdditionalExecutors(task.getAdditionalExecutors());
                    newTask.setTaskOwner(pm.getUserInvited());

                    newTask.setStatus(StatusTask.PLANNED);

                    newTask.setProject(projDao.findProjectById(projId));
                    newTask.setFinalTask(false);

                    taskDao.persist(newTask);

                    // persistir 1º tarefa para então associar tarefas precedentes se houver

                    if (task.getPreRequiredTasks() != null) {
                        associatePreRequiredTasksWithCurrentTask(task.getPreRequiredTasks(), newTask);
                    }

                    res = true;
                    communicationBean.notifyNewOwnerOfTask(pm.getUserInvited(), task.getTitle());
                    LOGGER.info("Task ID " + newTask.getId() + " is added to project " + projId + " by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

                }
            }
        }

        return res;
    }

    /**
     * Associates pre-required tasks to given task, if dates of given task do not clash with dates of pre-required task
     * finishDate of pre-required task must be before startDate of given task
     *
     * @param preRequiredTasks represents list of pre-required tasks to be associated with given task
     * @param currentTask      represents given task
     */
    private void associatePreRequiredTasksWithCurrentTask(List<Task> preRequiredTasks, entity.Task currentTask) {
        for (Task t : preRequiredTasks) {
            entity.Task taskEnt = taskDao.find(t.getId());
            if (taskEnt.getFinishDate().before(currentTask.getStartDate())) {

                currentTask.getListPreRequiredTasks().add(taskDao.find(t.getId()));
            }
        }
        taskDao.merge(currentTask);
    }

    /**
     * Verifies if attributes mandatory to add new task to project are filled in: title, start and finish date and task details
     *
     * @param task represents task to be added
     * @return true
     */
    public boolean checkTaskInfo(Task task) {

        boolean res = false;

        if (userBean.checkStringInfo(task.getTitle()) || task.getStartDate() == null || task.getFinishDate() == null || userBean.checkStringInfo(task.getDetails())) {
            res = true;
        }

        return res;
    }

    /**
     * Gets list of all tasks associated with given project
     *
     * @param id identifies project
     * @return list of Task DTO
     */
    public List<Task> getTasksList(int id) {

        List<Task> taskList = new ArrayList<>();

        entity.Project proj = projDao.findProjectById(id);

        if (proj != null) {
            List<entity.Task> listEnt = taskDao.findTasksFromProjectByProjId(id);

            if (listEnt != null) {
                for (entity.Task t : listEnt) {
                    taskList.add(convertTaskEntToDto(t));
                }
            }
        }
        return taskList;
    }

    /**
     * Converts task entity to task DTO
     *
     * @param t represents task entity
     * @return task DTO
     */
    private Task convertTaskEntToDto(entity.Task t) {
        Task task = new Task();

        task.setId(t.getId());
        task.setTitle(t.getTitle());
        task.setStartDate(t.getStartDate());
        task.setFinishDate(t.getFinishDate());
        task.setDetails(t.getDetails());
        task.setStatusInfo(t.getStatus().ordinal());

        task.setStatus(t.getStatus().getStatus());
        task.setTaskOwnerId(t.getTaskOwner().getUserId());
        task.setTaskOwnerFirstName(t.getTaskOwner().getFirstName());
        task.setTaskOwnerLastName(t.getTaskOwner().getLastName());
        task.setTaskOwnerPhoto(t.getTaskOwner().getPhoto());
        task.setAdditionalExecutors(t.getAdditionalExecutors());
        if (t.getListPreRequiredTasks() != null) {
            task.setPreRequiredTasks(convertTaskEntToMinimalDto(t.getListPreRequiredTasks()));
        }
        return task;
    }

    /**
     * Converts task entity to task DTO containing minimal required information to display pre-required tasks
     *
     * @param listPreRequiredTasks represents list of pre-required tasks
     * @return list of Task DTO
     */
    private List<Task> convertTaskEntToMinimalDto(List<entity.Task> listPreRequiredTasks) {
        List<Task> list = new ArrayList<>();

        for (entity.Task t : listPreRequiredTasks) {
            Task task = new Task();
            task.setId(t.getId());
            task.setTitle(t.getTitle());
            task.setStatus(t.getStatus().getStatus());
            task.setStatusInfo(t.getStatus().ordinal());

            list.add(task);
        }
        return list;
    }

    /**
     * Verifies if user has an active project (one project whose status is not CANCELLED or FINISHED)
     * User can only create a new project or participate in another if it has no active project
     *
     * @param token represents session of user that makes the request
     * @return true if user has no active project, therefore can create or participate in project
     */
    public boolean verifyIfUserHasActiveProject(String token) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            List<entity.Project> projectsList = projMemberDao.findListOfProjectsByUserId(user.getUserId());
            if (projectsList != null) {
                int count = 0;
                for (entity.Project p : projectsList) {
                    if (p.getStatus() != StatusProject.CANCELLED && p.getStatus() != StatusProject.FINISHED) {
                        count++;
                    }
                }
                if (count == 0) {
                    res = true;
                    // pode criar ou participar num proj
                }
            }
        }
        return res;
    }

    /**
     * Get list of skills whose name contains given input (str), to suggest
     *
     * @param str represents input that is written by user in frontend
     * @return list of Skill - DTO that contains information of given skill
     */
    public List<Skill> getSkillsList(String str) {
        List<Skill> listSkillDto = new ArrayList<>();

        List<entity.Skill> list = skillDao.findSkillListContainingStr(str.toLowerCase());

        if (list != null) {
            for (entity.Skill s : list) {
                listSkillDto.add(userBean.convertToSkillDto(s));
            }
        }
        return listSkillDto;
    }

    /**
     * Edits project details, excluding execution plan and members
     * Associates keywords and skills with given project
     *
     * @param token    identifies session that makes the request
     * @param editProj contains project details
     * @return true if project details are edited successfully
     */
    public boolean editProjectInfo(String token, dto.Project editProj) {

        boolean res = false;
        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Project projEnt = projDao.findProjectById(editProj.getId());

            if (projEnt != null) {
                projEnt.setTitle(editProj.getTitle());
                if (editProj.getOffice() != 20) {
                    projEnt.setOffice(setOffice(editProj.getOffice()));
                }
                projEnt.setDetails(editProj.getDetails());
                projEnt.setResources(editProj.getResources());
                projEnt.setStatus(setProjectStatus(editProj.getStatusInt()));
                projEnt.setMembersNumber(editProj.getMembersNumber());
                projEnt.setCreationDate(editProj.getCreationDate());

                projDao.merge(projEnt);

                associateKeywordsWithProject(editProj.getKeywords(), projEnt);

                if (editProj.getSkills() != null || editProj.getSkills().size() != 0) {
                    associateSkillsWithProject(editProj.getSkills(), projEnt);
                }
                projDao.merge(projEnt);
                res = true;
                communicationBean.recordProjectEdition(projEnt, user);
                LOGGER.info("User ID " + user.getUserId() + " edits details of project ID " + projEnt.getId() + ". IP Address of request is " + userBean.getIPAddress());

            }
        }
        return res;
    }

    /**
     * Gets StatusProject ENUM value according to statusInt value
     *
     * @param statusInt value correspondes to equivalent StatusProject
     * @return StatusProject Enum
     */
    private StatusProject setProjectStatus(int statusInt) {
        // define o status do projecto de acordo com info que vem do frontend

        StatusProject st = null;

        switch (statusInt) {
            case 0:
                st = StatusProject.PLANNING;
                break;
            case 1:
                st = StatusProject.READY;
                break;
            case 2:
                st = StatusProject.PROPOSED;
                break;
            case 3:
                st = StatusProject.APPROVED;
                break;
            case 4:
                st = StatusProject.PROGRESS;
                break;
            case 5:
                st = StatusProject.CANCELLED;
                break;
            case 6:
                st = StatusProject.FINISHED;
                break;
        }
        return st;
    }

    /**
     * Gets Office ENUM value according to office value
     *
     * @param office value correspondes to equivalent Office
     * @return Office Enum
     */
    public Office setOffice(int office) {
        // define o office de acordo com info que vem do frontend

        Office value = null;

        switch (office) {
            case 0:
                value = Office.LISBOA;
                break;
            case 1:
                value = Office.COIMBRA;
                break;
            case 2:
                value = Office.PORTO;
                break;
            case 3:
                value = Office.TOMAR;
                break;
            case 4:
                value = Office.VISEU;
                break;
            case 5:
                value = Office.VILAREAL;
                break;
        }
        return value;
    }

    /**
     * Gets list of users who can be suggested to be invited to participate in project, according to input inserted in frontend (name)
     * Users whose name or nickname contain input (name)
     * It will only show users who do not have an active project and are not contest managers (profile A)
     *
     * @param name
     * @return
     */
    public List<UserInfo> getPossibleMembers(String name) {

        List<UserInfo> listToSuggest = new ArrayList<>();
        List<entity.User> tempList = new ArrayList<>();

        List<entity.User> matchingUsers = userDao.findUserContainingStr(name);

        if (matchingUsers != null) {
            List<entity.User> usersWithActiveProject = projMemberDao.findListOfUsersWithActiveProject();

            if (usersWithActiveProject != null) {
                // retirar estes users da lista all users: adicionando apenas o que n coincidem a uma lista auxiliar
                tempList = matchingUsers.stream().filter(user -> !usersWithActiveProject.contains(user))
                        .filter(user -> !user.isContestManager())
                        .collect(Collectors.toList());
            }
            if (!tempList.isEmpty()) {
                // significa que tem users para apresentar. Converter para dto
                for (entity.User u : tempList) {
                    listToSuggest.add(convertUserToUserInfoDto(u));
                }
            }
        }
        return listToSuggest;
    }

    /**
     * Convert User entity to UserInfo DTO, which contains minimal necessary information
     *
     * @param u represents User entity
     * @return UserInfo DTO
     */
    private UserInfo convertUserToUserInfoDto(entity.User u) {
        UserInfo userDto = new UserInfo();
        userDto.setId(u.getUserId());
        userDto.setFirstName(u.getFirstName());
        userDto.setLastName(u.getLastName());
        userDto.setNickname(u.getNickname());
        userDto.setPhoto(u.getPhoto());

        return userDto;
    }

    /**
     * Removes member from given project by updating ProjectMember that defines relationship between user and project (entry is not removed from database)
     * Member can only be removed if project continues to have a manager after member removal
     * Member can only be removed if all tasks where taskOwner is member to be removed and task status is NOT FINISHED have another project member designated taskOwner
     *
     * @param userId identifies user to be removed from project
     * @param projId identifies project
     * @param token  identifies session that makes the request
     * @return true
     */
    public boolean deleteProjMember(int userId, int projId, String token) {

        boolean delete = false;

        entity.ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, userId);

        if (pm != null) {
            boolean res = hasEnoughManagers(projId, userId);

            if (res) {
                // tem managers suficientes
                boolean canLeave = dealWithTasksBeforeLeavingProject(userId, pm.getProjectToParticipate());

                if (canLeave) {
                    // tarefas à responsabilidade do membro que vai sair têm novo responsável atribuído
                    pm.setRemoved(true);
                    projMemberDao.merge(pm);

                    entity.User loggedUser = tokenDao.findUserEntByToken(token);
                    if (loggedUser != null) {
                        if (loggedUser.getUserId() != userId) {
                            // pessoa foi excluída do projecto. Notificar pessoa porque senão não perceberá porque deixa de ter acesso a página completa do projecto
                            // communicationBean.notifyProjectMembersOfMemberLeavingProject(pm.getProjectToParticipate(), pm.getUserInvited());
                            communicationBean.notifyUserHasBeenExcludedFromProject(pm.getProjectToParticipate(), pm.getUserInvited());
                        }
                    }
                    delete = true;
                    LOGGER.info("User ID " + userId + " no longer participates in project " + pm.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());

                    communicationBean.recordMemberRemovalFromProject(pm.getUserInvited(), pm.getProjectToParticipate());
                }
            }
        }
        return delete;
    }

    /**
     * Verifies if given project has enough managers to ensure that it will always have a project manager regardless of changes in members role
     * Method is called before changing user's profile to contest manager, before project member role is changed from project manager to 'normal' member, and before project member leaving project
     * If there is only 1 project manager, it need to be verified if userId is not a project manager, in which case it will not be possible to complete request
     *
     * @param projId identifies project
     * @param userId identifies user whose project role/ participation might be altered
     * @return true if project has at least 2 project managers or in case project has 1 manager whose ID is not the same has userID whose role will be changed
     */
    public boolean hasEnoughManagers(int projId, int userId) {
        boolean res = false;

        List<entity.User> managersList = projMemberDao.findListOfManagersByProjectId(projId);
        if (managersList != null && managersList.size() != 0) {
            if (managersList.size() >= 2) {
                res = true;
            } else {
                for (entity.User u : managersList) {
                    if (u.getUserId() != userId) {
                        // gestor é outro user. pode alterar à vontade
                        res = true;
                    }
                }
            }
        }
        return res;
    }

    /**
     * Verifies if token has permission to add member (is project manager) or token who wants to participate in project has no active project (self-invitation)
     * It is a self-invitation if userId is equal to token ID (userId from token's account)
     *
     * @param token  identifies session that makes the request
     * @param projId identifies project
     * @param userId identifies user to be invited to participate in project
     * @return true if token has permission to complete request (either is a manager or has no active project)
     */
    public boolean verifyPermissionToAddMember(String token, int projId, int userId) {
        boolean res = false;  // nao pode

        entity.User loggedUser = tokenDao.findUserEntByToken(token);

        if (loggedUser.getUserId() != userId) {
            // não é auto-convite para participar num projecto. tem de verificar se logged user é gestor
            res = isProjManager(token, projId);
        } else {
            // auto-convite para participar num projecto. Tem de verificar se tem algum projecto activo
            res = verifyIfUserHasActiveProject(token);
        }
        return res;
    }

    /**
     * Verifies if token has permission to delete member (is project manager) or token itself wants to leave project
     *
     * @param token  identifies session that makes the request
     * @param projId identifies project
     * @param userId identifies user to leave project
     * @return true if token has permission to complete request
     */
    public boolean verifyPermissionToDeleteUser(String token, int projId, int userId) {
        boolean res = false; // n pode

        entity.User loggedUser = tokenDao.findUserEntByToken(token);

        if (loggedUser.getUserId() != userId) {
            // não é o pp a tentar sair do projecto. tem de se verificar se é gestor do projecto
            res = isProjManager(token, projId);
        } else {
            // auto-remoção do projecto. A pessoa não tem de ter nenhuma outra verificação
            res = true;

        }


        return res;
    }

    /**
     * Verifies if project has available spots for participating members
     * Compares number of active members (accepted and not removed) with maximum members project can have
     *
     * @param projId identifies project
     * @return true if there are available spots
     */
    public boolean verifyIfProjectHasAvailableSpots(int projId) {
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            List<entity.ProjectMember> activeMembers = projMemberDao.findListOfMembersByProjectId(projId);

            if (activeMembers != null) {
                if (activeMembers.size() < project.getMembersNumber()) {
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * Calculates number of available spots in given project
     *
     * @param p represents project
     * @return int of available spots
     */
    private int getNumberOfAvailableSpots(entity.Project p) {

        int count = 0;

        List<entity.ProjectMember> activeMembers = projMemberDao.findListOfMembersByProjectId(p.getId());
        if (activeMembers != null) {
            count = p.getMembersNumber() - activeMembers.size();
        } else {
            count = p.getMembersNumber();
        }
        return count;
    }

    /**
     * Modifies role that a member plays in given project: project manager (1) or 'normal' member (0)
     * Role of a user can only be changed to normal (0) if project still has at least one project manager after role is changed
     *
     * @param userId identifies user whose role is to be changed
     * @param projId identifies project
     * @param token  identifies session that makes the request
     * @param role   is 0 to change role to 'normal' member and 1 is to change role to project manager
     * @return true if role is modified successfully
     */
    public boolean changeMemberRole(int userId, int projId, String token, int role) {

        boolean res = false;

        entity.ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, userId);
        if (pm != null) {
            if (pm.isAccepted() && !pm.isRemoved()) {

                if (role == 0) {
                    // tem de 1º verificar se numero de gestores é >=2 ou, sendo 1 que não é o do pp
                    if (hasEnoughManagers(projId, userId)) {
                        pm.setManager(false);
                        projMemberDao.merge(pm);
                        res = true;
                        LOGGER.info("User ID " + userId + " is no longer project manager of project " + pm.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());
                        communicationBean.recordProjectMemberRoleChange(token, userId, projId, role);
                    }
                } else if (role == 1) {

                    pm.setManager(true);
                    projMemberDao.merge(pm);
                    res = true;
                    LOGGER.info("User ID " + userId + " is now a project manager of project " + pm.getProjectToParticipate().getId() + ". IP Address of request is " + userBean.getIPAddress());
                    communicationBean.recordProjectMemberRoleChange(token, userId, projId, role);
                }
            }
        }
        return res;
    }

    /**
     * Modifies project status
     * Some project status (PROPOSED and APPROVED) are defined elsewhere as those are automatic based on contest appplication
     *
     * @param token     identifies session that makes the request
     * @param projId    identifies project
     * @param status    value represents new status project. Values are the same defined for StatusProject ENUM. Value 7 means project is to be re-activated
     * @param finalTask represents final task information that is added when project status is to be changed to READY
     * @return true if project status is changed successfully
     */
    public boolean editProjectStatus(String token, int projId, int status, Task finalTask) {

        boolean res = false;

        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            switch (status) {
                case 0:
                    // mudar para PLANNING: proj está em ready e é preciso alterar status para permitir editar info do projecto
                    res = changeProjStatusToPlanning(project, token);
                    break;
                case 1:
                    // mudar para READY: projecto está em planning. Proj ready é um projecto que está pronto para ser apresentado a um concurso

                    res = changeProjStatusToReady(project, finalTask, token);
                    break;
                case 4:
                    // mudar para IN PROGRESS, proj está approved
                    res = changeProjStatusToProgress(project, token);

                    break;
                case 5:
                    // CANCELAR projecto pode ser feito em qq altura

                    res = changeProjStatusToCancelled(project, token);
                    break;
                case 6:
                    // mudar para FINISHED: proj tem de estar in progress e verificar se precisa de ter tarefas todas concluidas ou outras verificações
                    res = changeProjStatusToFinished(project, token);
                    break;
                case 7:
                    // REACTIVAR projecto - PLANNING: um projecto cancelado se não está associado a nenhum concurso
                    // só pode acontecer se nenhum membro tem algum projecto activo no momento da reactivação
                    res = reactivateCancelledProj(project, token);
                    break;
            }
        }
        return res;
    }

    /**
     * Reactivates a CANCELLED project, setting its status as PLANNING
     * Project must not participate in a contest
     * Project members must not have another active project
     * No project member must have Profile A (contest manager)
     *
     * @param project represents project
     * @param token   identifies session that makes the request
     * @return true if project is reactivated sucessfully
     */
    private boolean reactivateCancelledProj(entity.Project project, String token) {
        boolean res = false;

        ContestApplication application = applicationDao.findAcceptedApplicationForGivenProjectId(project.getId());

        if (application == null) {
            if (verifyExistingMembersHaveNoActiveProject(project) && verifyExistingMembersAreContestManagers(project)) {

                project.setStatus(StatusProject.PLANNING);
                projDao.merge(project);
                res = true;

                entity.User user = tokenDao.findUserEntByToken(token);
                communicationBean.recordProjectStatusChange(project, user, 7);
                LOGGER.info("Project " + project.getId() + " is reactivated by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

            }
        }

        return res;
    }

    /**
     * Verifies if active members of given project have an active project (useful when given project is CANCELLED but to be reactivated)
     * All members must have no active project so that CANCELLED project is reactivated
     *
     * @param project represents given project
     * @return true if all members have no active project
     */
    private boolean verifyExistingMembersHaveNoActiveProject(entity.Project project) {
        boolean res = false;
        int count = 0;  // conta o número de membros com projecto activo. Se no final o valor n for 0, não pode reactivar projecto

        List<entity.User> members = projMemberDao.findListOfUsersByProjectId(project.getId());
        if (members != null) {
            for (entity.User u : members) {
                entity.Project activeProj = projMemberDao.findActiveProjectByUserId(u.getUserId());
                if (project != null) {
                    // à partida nunca será o ID do projecto a ser reactivado
                    count++;
                }
            }
            if (count == 0) {
                res = true;
            }
        }
        return res;
    }

    /**
     * Verifies if active members of given project are contest managers (profile A) - (useful when given project is CANCELLED but to be reactivated)
     *
     * @param project represents given project
     * @return true if all members have profile B
     */
    private boolean verifyExistingMembersAreContestManagers(entity.Project project) {
        boolean res = false;
        int count = 0;  // conta o número de membros que são PERFIL A. Se no final o valor n for 0, não pode reactivar projecto

        List<entity.User> members = projMemberDao.findListOfUsersByProjectId(project.getId());
        if (members != null) {
            for (entity.User u : members) {
                if (u.isContestManager()) {
                    count++;
                }
            }
            if (count == 0) {
                res = true;
            }
        }
        return res;

    }

    /**
     * Modifies project status to FINISHED
     * All project tasks must be FINISHED
     *
     * @param project represents project
     * @param token   identifies session that makes the request
     * @return true if project status is modified sucessfully
     */
    private boolean changeProjStatusToFinished(entity.Project project, String token) {
        boolean res = false;

        Long count = taskDao.countNotFinishedTasksFromProjectByProjId(project.getId());
        if (count == 0) {
            project.setStatus(StatusProject.FINISHED);
            project.setFinishDate(Date.from(Instant.now()));
            projDao.merge(project);
            res = true;
            entity.User user = tokenDao.findUserEntByToken(token);
            communicationBean.recordProjectStatusChange(project, user, 6);
            LOGGER.info("Status of project " + project.getId() + " is FINISHED. Change by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

        }
        return res;
    }

    /**
     * Project status is modified to CANCELLED
     * If project application is accepted in contest, final task is not deleted (project can never be reactivated)
     * If not, final task is deleted - if reactivated, project status will be PLANNING
     *
     * @param project represents project
     * @param token   identifies session that makes the request
     * @return true if project is cancelled successfully
     */
    private boolean changeProjStatusToCancelled(entity.Project project, String token) {
        boolean res = false;

        ContestApplication acceptedApplication = applicationDao.findAcceptedApplicationForGivenProjectId(project.getId());

        if (acceptedApplication == null) {
            // projecto não está aceite em nenhum concurso. é preciso apagar a tarefa final pq ao retomar terá status planning
            deleteFinalTaskOfProject(project.getId());
            project.setStatus(StatusProject.CANCELLED);
            projDao.merge(project);
            res = true;
            entity.User user = tokenDao.findUserEntByToken(token);
            communicationBean.recordProjectStatusChange(project, user, 5);
            LOGGER.info("Status of project " + project.getId() + " is CANCELLED. Change by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

        } else {
            // projecto está em concurso. Cancela sem fazer mais nada
            project.setStatus(StatusProject.CANCELLED);
            projDao.merge(project);
            res = true;
            entity.User user = tokenDao.findUserEntByToken(token);
            communicationBean.recordProjectStatusChange(project, user, 5);
            LOGGER.info("Status of project " + project.getId() + " is CANCELLED. Change by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

        }

        return res;
    }

    /**
     * Project status is modified to IN PROGRESS
     *
     * @param project represents project
     * @param token   identifies session that makes the request
     * @return true if project status is modified sucessfully
     */
    private boolean changeProjStatusToProgress(entity.Project project, String token) {

        boolean res = false;
        if (project.getStatus() == StatusProject.APPROVED) {
            project.setStatus(StatusProject.PROGRESS);
            projDao.merge(project);
            res = true;
            entity.User user = tokenDao.findUserEntByToken(token);
            communicationBean.recordProjectStatusChange(project, user, 4);
            LOGGER.info("Status of project " + project.getId() + " is IN PROGRESS. Change by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

        }
        return res;
    }

    /**
     * Project status is modified to READY. Final task is added to project if finalTask startDate is after finishDate of every task associated with project
     *
     * @param finalTask represents final task information
     * @param project   represents project
     * @param token     identifies session that makes the request
     * @return true if project status is modified sucessfully to READY and final task added to project
     */
    private boolean changeProjStatusToReady(entity.Project project, Task finalTask, String token) {
        boolean res = false;

        if (finalTask != null) {
            if (verifyFinalTaskDateIsAfterAllProjectTasks(project, finalTask.getStartDate())) {

                boolean res1 = addFinalTaskToProject(project, finalTask);

                if (res1) {
                    project.setStatus(StatusProject.READY);
                    projDao.merge(project);
                    res = true;
                    entity.User user = tokenDao.findUserEntByToken(token);
                    communicationBean.recordProjectStatusChange(project, user, 1);
                    LOGGER.info("Status of project " + project.getId() + " is READY. Change by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

                }
            }
        }

        return res;
    }

    /**
     * Verifies if startDate of final task is after finishDate of every other project tasks
     * Final task, final presentation, can only be defined and occur after all other project tasks
     *
     * @param project   represents project
     * @param startDate represents final task start date
     * @return true if final task really is last task defined for given project
     */
    private boolean verifyFinalTaskDateIsAfterAllProjectTasks(entity.Project project, Date startDate) {

        boolean res = false;
        int count = 0;
        List<entity.Task> taskList = taskDao.findTasksFromProjectByProjId(project.getId());

        if (taskList != null) {
            for (entity.Task t : taskList) {
                //basta que finish date da tarefa seja igual ou posterior a startDate para não estar de acordo com as regras
                if (t.getFinishDate().equals(startDate) || t.getFinishDate().after(startDate)) {
                    count++;
                }
            }
            if (count == 0) {
                res = true;
                // data é ok, tarefa final é mesmo a última tarefa do plano de execução
            }
        }
        return res;
    }

    /**
     * Adds final task to given project
     * Final task title is predefined as 'Apresentação final'
     * Final task is defined for 1 day - startDate and finalDate have same value
     * Final task has no pre-required tasks defined
     *
     * @param project   represents project
     * @param finalTask represents final task information
     * @return true if final task is persisted in database and associated with project
     */
    private boolean addFinalTaskToProject(entity.Project project, Task finalTask) {
        boolean res = false;
        entity.User user = userDao.findUserById(finalTask.getTaskOwnerId());
        if (user != null) {
            Long timestamp = finalTask.getStartDate().getTime();
            Long oneDay = (long) (24 * 60 * 60 * 1000);
            Date finishDate = new Date(timestamp + oneDay);

            entity.Task taskEnt = new entity.Task();
            taskEnt.setTitle("Apresentação final");
            taskEnt.setStartDate(finalTask.getStartDate());
            taskEnt.setFinishDate(finishDate);
            //  taskEnt.setFinishDate(finalTask.getStartDate());
            taskEnt.setDetails(finalTask.getDetails());
            taskEnt.setTaskOwner(user);
            taskEnt.setStatus(StatusTask.PLANNED);
            taskEnt.setProject(project);
            taskEnt.setFinalTask(true);
            taskDao.persist(taskEnt);
            res = true;
            communicationBean.notifyNewOwnerOfTask(user, taskEnt.getTitle());
        }
        return res;
    }

    /**
     * Project status is modified to PLANNNING if it's currently READY
     * FinalTask defined is deleted
     *
     * @param project represents project
     * @param token   identifies session that makes the request
     * @return true if project status is modified sucessfully to PLANNING
     */
    private boolean changeProjStatusToPlanning(entity.Project project, String token) {
        boolean res = false;
        if (project.getStatus() == StatusProject.READY) {
            project.setStatus(StatusProject.PLANNING);
            projDao.merge(project);

            deleteFinalTaskOfProject(project.getId());

            res = true;
            entity.User user = tokenDao.findUserEntByToken(token);
            communicationBean.recordProjectStatusChange(project, user, 0);
            LOGGER.info("Status of project " + project.getId() + " is PLANNING. Change by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

        }
        return res;
    }

    /**
     * Deletes from database (table task) final task of given project
     * When project status is modified to PLANNING or CANCELLED with no contest associated final task is deleted
     *
     * @param id identifies project
     */
    private void deleteFinalTaskOfProject(int id) {
        entity.Task finalTask = taskDao.findFinalTaskByProjectId(id);
        if (finalTask != null) {
            taskDao.remove(finalTask);
        }
    }

    /**
     * Verifies if userId is task owner of any project task whose status is NOT finished. If so, a new task owner must be assigned
     * A new task owner is assigned from list of project managers (minu userId if it is project manager) - because they are the only ones that can modify project plan, they can easily change that if they want to
     * Assigning directly to normal project member meant that they would have to wait for a project manager to change in case they could not complete task
     * Method is called before changing user's profile to contest manager, and before project member leaving project
     *
     * @param userId  identifies user whose participation in project will end
     * @param project identifies project
     * @return true if user has no tasks to be dealt with or if all tasks where dealt with successfully (new task owner assigned)
     */
    public boolean dealWithTasksBeforeLeavingProject(int userId, entity.Project project) {
        boolean res = false;
        List<entity.Task> taskList = taskDao.findListOfTasksFromProjectByProjIdWhoseTaskOwnerIsGivenUserId(project.getId(), userId);

        if (taskList != null) {

            List<entity.User> managersList = projMemberDao.findListOfManagersByProjectId(project.getId());
            if (managersList != null) {
                // retirar o user que será removido, caso seja manager
                List<entity.User> tempList = managersList.stream().filter(user -> user.getUserId() != userId).collect(Collectors.toList());
                int count = 0;
                for (entity.Task t : taskList) {
                    entity.User randomManager = selectRandomUserFromList(tempList);
                    if (randomManager != null) {
                        t.setTaskOwner(randomManager);

                        randomManager.getListTasks().add(t);
                        userDao.merge(randomManager);
                        taskDao.merge(t);
                        communicationBean.notifyNewOwnerOfTask(randomManager, t.getTitle());
                        count++;

                    }
                }
                if (count == taskList.size()) {
                    // significa que todas as tarefas foram tratadas
                    res = true;
                }
            }

        } else {
            // lista nula, n tem de fazer nada
            res = true;
        }
        return res;
    }

    /**
     * Allows to choose random user from users list, when assigning a new task owner
     *
     * @param tempList represents list of users (specifically list of managers of given project)
     * @return User randomly choosen
     */
    private entity.User selectRandomUserFromList(List<entity.User> tempList) {
        Random value = new Random();
        int index = value.nextInt(tempList.size());
        return tempList.get(index);
    }

    /**
     * Verifies if project status is PLANNING or IN PROGRESS
     * Members might be added / removed , tasks might be added, edited if project status is PLANNING or IN PROGRESS
     *
     * @param projId identifies project
     * @return true if project status does not allow changes
     */
    public boolean verifyProjectStatusToModifyTask(int projId) {
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            if (project.getStatus() == StatusProject.READY || project.getStatus() == StatusProject.PROPOSED || project.getStatus() == StatusProject.APPROVED || project.getStatus() == StatusProject.CANCELLED || project.getStatus() == StatusProject.FINISHED) {
                res = true;
                // plano de execução, membros não podem sair
            }
        }
        return res;
    }

    /**
     * Verifies if project status is PLANNING
     * Task might be removed if project status is PLANNING
     *
     * @param projId identifies project
     * @return true if project status allows changes
     */
    public boolean verifyProjectIsPlanning(int projId) {
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            if (project.getStatus() == StatusProject.PLANNING) {
                res = true;
            }
        }
        return res;
    }

    /**
     * Verifies if given task is associated with given project
     *
     * @param taskId identifies task
     * @param projId identifies project
     * @return true if task is associated with given project
     */
    public boolean verifyIfTaskBelongsToProject(int taskId, int projId) {
        boolean res = false;
        entity.Task task = taskDao.find(taskId);

        if (task != null) {
            if (task.getProject().getId() == projId) {
                res = true;
            }
        }
        return res;
    }

    /**
     * Removes task from database only if it has no association with any other tasks and is not final task
     * Removes association between task and project and between task and user (taskOwner)
     *
     * @param token  identifies session that makes the request
     * @param taskId identifies task to be deleted
     * @return true if task is deleted from database along with its associations with project and user
     */
    public boolean deleteTask(String token, int taskId) {

        boolean res = false;
        entity.Task taskEnt = taskDao.find(taskId);
        if (taskEnt != null && !taskEnt.isFinalTask()) {

            List<entity.Task> listTasksWhichCurrentTaskIsPreRequired = findTasksWhoHaveCurrentTaskAsPrecedent(taskEnt);

            if (listTasksWhichCurrentTaskIsPreRequired.isEmpty() && taskEnt.getListPreRequiredTasks().isEmpty()) {

                entity.Project projEnt = taskEnt.getProject();

                if (projEnt != null) {
                    projEnt.getListTasks().remove(taskEnt);

                    entity.User user = taskEnt.getTaskOwner();

                    if (user != null) {
                        user.getListTasks().remove(taskEnt);
                        userDao.merge(user);
                        communicationBean.notifyTaskWasRemoved(user, taskEnt.getTitle());
                    }

                    projDao.merge(projEnt);

                }
                taskDao.remove(taskEnt);
                res = true;

            }
        }
        return res;
    }

    /**
     * Finds all tasks of given project who have current task as pre-required task
     * Useful to find tasks associated with current task, whether before deleting task or editing task dates
     *
     * @param taskEnt represents current task
     * @return list of Task entities associated with current task
     */
    private List<entity.Task> findTasksWhoHaveCurrentTaskAsPrecedent(entity.Task taskEnt) {

        List<entity.Task> list = new ArrayList<>();
        List<entity.Task> allTasks = taskDao.findAll();

        if (allTasks != null) {
            for (entity.Task t : allTasks) {
                if (t.getListPreRequiredTasks().contains(taskEnt)) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    /**
     * Verifies if project status is IN PROGRESS, so that task status can be modified
     *
     * @param projId identifies task
     * @return true if task status cannot be altered
     */
    public boolean verifyProjectStatusToEditTaskStatus(int projId) {
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            if (project.getStatus() != StatusProject.PROGRESS) {
                res = true;
                // status de tarefas do plano de execução não poderá ser alterado
            }
        }
        return res;
    }

    /**
     * Validates all hypothetical scenarios regarding task dates before edit task details
     * Dates must not collide with dates of tasks which are associated with given task
     * If project is IN PROGRESS, dates must be within ONGOING period of contest and if task to be edited is final task, dates must be after finishDate of all project tasks
     * If startDate is edited, must verify finishDate of pre-required tasks
     * If finishDate is edited must verify startDate of tasks who have current task as pre-required task
     *
     * @param token    identifies session that makes the request
     * @param editTask contains task information to be edited
     * @return true
     */
    public boolean editTask(String token, Task editTask) {

        boolean res = false;
        boolean res1 = false;
        boolean res2 = false;

        entity.Task taskEnt = taskDao.find(editTask.getId());

        if (taskEnt != null) {
            if (taskEnt.getProject().getStatus() == StatusProject.PLANNING) {
                // editar tarefa num projecto Planning - Não é preciso confirmar datas de concurso

                if (editTask.getStartDate() != taskEnt.getStartDate()) {
                    //significa que data inicio foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, precedentes
                    res1 = checkNewDatesCompatibilityWithPreRequiredTasks(editTask);
                }
                if (editTask.getFinishDate() != taskEnt.getFinishDate()) {
                    //significa que data final foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, que tenham esta tarefa como precedente
                    res2 = checkNewDatesCompatibilityWithAssociatedTasks(editTask, taskEnt);
                }
                if (!res1 && !res2) {
                    //ambos são false, significa que n há conflitos de datas. A tarefa pode ser editada
                    res = taskCanBeEdited(taskEnt, editTask);

                }
            } else {
                // tarefa pertence a projecto in progress. Verificar periodo do concurso e final task dates

                if (contestBean.newDatesAreWithinContestPeriod(taskEnt, editTask) && !taskEnt.isFinalTask()) {
                    // datas são compatíveis com concurso e não sendo final task, não precisa de verificação extra

                    if (editTask.getStartDate() != taskEnt.getStartDate()) {
                        //significa que data inicio foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, precendentes
                        res1 = checkNewDatesCompatibilityWithPreRequiredTasks(editTask);

                    }
                    if (editTask.getFinishDate() != taskEnt.getFinishDate()) {
                        //significa que data final foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, que tenham esta tarefa como precedente
                        res2 = checkNewDatesCompatibilityWithAssociatedTasks(editTask, taskEnt);
                    }

                    if (!res1 && !res2) {
                        //ambos são false, significa que n há conflitos de datas. A tarefa pode ser editada
                        res = taskCanBeEdited(taskEnt, editTask);

                    }

                } else if (contestBean.newDatesAreWithinContestPeriod(taskEnt, editTask) && taskEnt.isFinalTask()) {
                    // precisa de verificar datas da final task n colidem com datas de restantes tarefas do projecto

                    if (verifyFinalTaskDateIsAfterAllProjectTasks(taskEnt.getProject(), editTask.getStartDate())) {
                        // A tarefa pode ser editada. Sendo final task n precisa de verificar todas as outras tarefas

                        res = taskCanBeEdited(taskEnt, editTask);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Edits task details
     *
     * @param taskEnt  represents task entity to be edited
     * @param editTask represents task information to edit
     * @return true if task is edited successfully
     */
    private boolean taskCanBeEdited(entity.Task taskEnt, Task editTask) {
        boolean res = false;
        if (editTask != null) {
            taskEnt.setTitle(editTask.getTitle());
            taskEnt.setStartDate(editTask.getStartDate());
            taskEnt.setFinishDate(editTask.getFinishDate());
            taskEnt.setDetails(editTask.getDetails());
            taskEnt.setAdditionalExecutors(editTask.getAdditionalExecutors());

            if (taskEnt.getTaskOwner().getUserId() != editTask.getTaskOwnerId()) {
                //alteração de membro responsável. Verificar se é membro do projecto
                entity.ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(taskEnt.getProject().getId(), editTask.getTaskOwnerId());
                entity.User previousOwner = taskEnt.getTaskOwner();
                if (pm != null) {
                    if (pm.isAccepted() && !pm.isRemoved()) {
                        // relação com projecto está activa
                        entity.User user = userDao.findUserById(editTask.getTaskOwnerId());
                        if (user != null) {
                            taskEnt.setTaskOwner(user);
                            communicationBean.notifyNewOwnerOfTask(user, editTask.getTitle());
                            communicationBean.notifyTaskIsNoLongerMemberResponsability(previousOwner, editTask.getTitle());
                        }
                    }
                }
            }
            taskDao.merge(taskEnt); // merge antes de associar, pq lá já faz merge da taskEnt e resultava na duplicação das pre required tasks
            if (editTask.getPreRequiredTasks() != null || editTask.getPreRequiredTasks().size() != 0) {

                taskEnt.getListPreRequiredTasks().clear();
                taskDao.merge(taskEnt);

                associatePreRequiredTasksWithCurrentTask(editTask.getPreRequiredTasks(), taskEnt);
            }
            res = true;
            LOGGER.info("Details of task ID " + taskEnt.getId() + " are edited. IP Address of request is " + userBean.getIPAddress());

        }
        return res;
    }

    /**
     * Verifies if start date of given task is after finish date of every pre-required task of given task
     * For that verification, list of pre-required tasks evaluated is the one sent from frontend because list associated with task entity might have differences (that might be one of the changes to make with task editing)
     *
     * @param editTask represents task with information to edit
     * @return true if there is conflict between dates
     */
    private boolean checkNewDatesCompatibilityWithPreRequiredTasks(Task editTask) {
        boolean res = false;
        int count = 0;

        if (editTask.getPreRequiredTasks() != null) {
            for (Task t : editTask.getPreRequiredTasks()) {
                // para cada Dto que vem do frontend é preciso ir buscar ENT à DB para comparar data final com nova data de inicio
                entity.Task taskE = taskDao.find(t.getId());

                if (!taskE.getFinishDate().before(editTask.getStartDate())) {
                    //significa que data não é compatível
                    count++;
                }
            }
            if (count != 0) {
                // significa que há conflito de datas com alguma tarefa
                res = true;
            }
        }
        return res;
    }

    /**
     * Verifies if finish date of given task is before start date of every task which have given task as pre-required
     *
     * @param editTask represents task with information to edit
     * @return true if there is conflict between dates
     */
    private boolean checkNewDatesCompatibilityWithAssociatedTasks(Task editTask, entity.Task taskEnt) {
        boolean res = false;

        int count = 0;

        List<entity.Task> tasksWhoHaveCurrentTaskAsPrecedent = findTasksWhoHaveCurrentTaskAsPrecedent(taskEnt);

        if (tasksWhoHaveCurrentTaskAsPrecedent != null) {
            for (entity.Task t : tasksWhoHaveCurrentTaskAsPrecedent) {
                if (!editTask.getFinishDate().before(t.getStartDate())) {
                    //significa que data não é compatível

                    count++;
                }
            }
            if (count != 0) {
                // significa que há conflito de datas com alguma tarefa
                res = true;
            }
        }
        return res;
    }

    /**
     * Verifies task status is NOT FINISHED, so that it can be edited
     *
     * @param id identfies task
     * @return true if task status is FINISHED, therefore it cannot be edited
     */
    public boolean verifyTaskStatusToEditTask(int id) {
        boolean res = false;

        entity.Task task = taskDao.find(id);
        if (task != null) {
            if (task.getStatus() == StatusTask.FINISHED) {
                res = true;
                // tarefa n pode ser alterada
            }
        }
        return res;
    }

    /**
     * Validates before editing task status according to information sent from frontend
     * StatusInfo = 1 edits to PROGRESS; statusInfo = 2 edits to FINISHED
     * If given task has pre-required tasks, it needs to be verified, before status editing, if all pre-required tasks for given task are FINISHED. If not, status editing cannot happen
     * Extra validation is needed if task is finalTask - verify if all project tasks are FINISHED
     *
     * @param token    identifies session that makes the request
     * @param editTask contains information to edit task status: taskId and statusInfo
     * @return true if task status is edited
     */
    public boolean validateEditTaskStatus(String token, Task editTask) {
        boolean res = false;
        entity.Task taskEnt = taskDao.find(editTask.getId());

        if (taskEnt != null) {
            if (taskEnt.getListPreRequiredTasks() != null) {
                if (checkPreRequiredTasksAreFinished(taskEnt.getListPreRequiredTasks())) {
                    res = editTaskStatusAfterValidateEverything(taskEnt, editTask, token);
                }
            } else {
                // lista de tarefas precedentes é nula, pode editar status sem verificar mais nada se n for finalTask. se for finalTask tem de verificar q todas as tarefas do projecto estão finished
                if (!taskEnt.isFinalTask()) {
                    res = editTaskStatusAfterValidateEverything(taskEnt, editTask, token);
                } else {
                    // final task
                    if (!validateProjectTasksAreFinished(taskEnt)) {
                        res = editTaskStatusAfterValidateEverything(taskEnt, editTask, token);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Validates if all tasks minus final task of given project are FINISHED before changing finalTask status
     * @param taskEnt represents task entity
     * @return true if final task status cannot be changed
     */
    private boolean validateProjectTasksAreFinished(entity.Task taskEnt) {
        boolean res = false;
        List<entity.Task> tasks = taskDao.findTasksFromProjectByProjId(taskEnt.getProject().getId());
        List<entity.Task> tempList = tasks.stream().filter(task -> task.getId() != taskEnt.getId()).collect(Collectors.toList());
        int count = 0;
        if (tempList != null) {
            for (entity.Task t : tempList) {
                if (t.getStatus() != StatusTask.FINISHED) {
                    count++; // conta tasks não acabadas
                }
            }

            if (count != 0) {
                res = true; // n pode alterar status de final task
            }
        }
        return res;
    }

    /**
     * Edits task status according to information sent from frontend if all validations are checked
     * StatusInfo = 1 edits to PROGRESS; statusInfo = 2 edits to FINISHED
     *
     * @param taskEnt  represents task entity
     * @param token    identifies session that makes the request
     * @param editTask contains information to edit task status: taskId and statusInfo
     * @return true if task status is edited
     */
    private boolean editTaskStatusAfterValidateEverything(entity.Task taskEnt, Task editTask, String token) {
        boolean res = false;
        if (editTask.getStatusInfo() == 1 && taskEnt.getStatus().ordinal() == 0) {
            taskEnt.setStatus(StatusTask.PROGRESS);
            taskDao.merge(taskEnt);
            res = true;
            entity.User user = tokenDao.findUserEntByToken(token);
            communicationBean.recordTaskStatusEdit(user, taskEnt, 1);
            LOGGER.info("User ID " + user.getUserId() + " edits status of task " + editTask.getId() + " to IN PROGRESS. IP Address of request is " + userBean.getIPAddress());

        } else if (editTask.getStatusInfo() == 2 && taskEnt.getStatus().ordinal() == 1) {
            taskEnt.setStatus(StatusTask.FINISHED);
            taskDao.merge(taskEnt);
            // avisar todos os membros do projecto que tarefa está concluída, em x de avisar potenciais tarefas que precisem desta para avançar
            communicationBean.notifyAllMembersTaskIsFinished(taskEnt);
            res = true;
            entity.User user = tokenDao.findUserEntByToken(token);
            communicationBean.recordTaskStatusEdit(user, taskEnt, 2);
            LOGGER.info("User ID " + user.getUserId() + " edits status of task " + editTask.getId() + " to FINISHED. IP Address of request is " + userBean.getIPAddress());

        } else {
            res = false;
        }
        return res;
    }


    /**
     * Verifies if all pre-required tasks of given task are FINISHED (useful to edit given task status)
     *
     * @param listPreRequiredTasks represents list of pre-required tasks whose status needs to be checked
     * @return true if all pre-required tasks are FINISHED
     */
    private boolean checkPreRequiredTasksAreFinished(List<entity.Task> listPreRequiredTasks) {

        boolean res = true;
        int count = 0;
        for (entity.Task t : listPreRequiredTasks) {
            if (t.getStatus() != StatusTask.FINISHED) {
                count++;
            }
        }

        if (count != 0) {
            // alguma pre-required task não está finished
            res = false;
        }
        return res;
    }

    /**
     * Verifies if token is project manager or taskOwner, since those are the only project members that can edit task status
     *
     * @param token  identifies session that makes the request
     * @param taskId identifies task
     * @return true if token is allowed to edit task status
     */
    public boolean verifyPermissionToEditTaskStatus(String token, int taskId) {
        boolean res = false;
        entity.Task task = taskDao.find(taskId);
        if (task != null) {

            if (isProjManager(token, task.getProject().getId())) {
                // é gestor, pode mudar
                res = true;
            } else {
                // verificar se token é owner da tarefa
                entity.User loggedUser = tokenDao.findUserEntByToken(token);
                if (loggedUser.getUserId() == task.getTaskOwner().getUserId()) {
                    // token é owner da tarefa, podendo alterar o seu status
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * Verifies if project status is PLANNING
     * Project details (excluding execution plan) can only be modified when project status is PLANNING
     *
     * @param id identifies project
     * @return true if project status is NOT PLANNING
     */
    public boolean verifyPermisionToEditProjectInfo(int id) {
        boolean res = false;

        entity.Project project = projDao.findProjectById(id);

        if (project != null) {
            if (project.getStatus() != StatusProject.PLANNING) {
                res = true;
                // projecto n pode ser editado
            }
        }
        return res;
    }

    /**
     * Verifies if token, representing logged user that makes request, has an active project whose status is READY
     * Verifies if project has a final task defined whose date is compatible with contest's final days
     * Verifies if all tasks defined in project execution plan are within contest ONGOING period
     * These are mandatory validations to apply to given contest
     *
     * @param token     identifies session that makes the request
     * @param contestId identifies contest
     * @return true if project checks all validations: status is READY, all tasks, including final task defined within contest ONGOING period
     */
    public boolean verifyProjectCanApply(String token, int contestId) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);

        if (user != null) {
            entity.Project project = projMemberDao.findActiveProjectByUserId(user.getUserId());

            if (project != null) {

                if (verifyProjectStatusIsReady(project, contestId) && !verifyProjectTasksDates(project, contestId)) {
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * Verifies if all tasks defined in project execution plan are within contest ONGOING period
     *
     * @param project   represents project
     * @param contestId identifies contest
     * @return true if there is any task where start date or finish date is not within contest ONGOING period
     */
    private boolean verifyProjectTasksDates(entity.Project project, int contestId) {
        boolean res = false;
        int count = 0; // conta número de ocorrências em que task dates are not within contest ongoing period
        List<entity.Task> tasksList = taskDao.findTasksFromProjectByProjId(project.getId());

        if (tasksList != null) {
            Contest contest = contestDao.find(contestId);
            if (contest != null) {
                for (entity.Task t : tasksList) {
                    if (t.getStartDate().before(contest.getStartDate()) || t.getFinishDate().after(contest.getFinishDate())) {
                        count++;
                    }
                }
                if (count != 0) {
                    res = true; // alguma tarefa tem datas não compatíveis com ongoing period
                }
            }
        }
        return res;
    }


    /**
     * Verifies if project status is READY and has a final task defined whose date is compatible with contest's final days
     *
     * @param project   represents active project
     * @param contestId identifies contest
     * @return true if project can apply to contest: status is READY and final task is within contest ONGOING period
     */
    private boolean verifyProjectStatusIsReady(entity.Project project, int contestId) {
        boolean res = false;

        if (project.getStatus() == StatusProject.READY) {

            entity.Task finalTask = taskDao.findFinalTaskByProjectId(project.getId());

            if (finalTask != null) {
                if (verifyFinalTaskDate(finalTask, contestId)) {
                    res = true;
                }
            }
        }
        return res;
    }

    /**
     * Verifies if final task date is compatible with contest's final days
     *
     * @param finalTask represents final task of active project
     * @param contestId identifies contest
     * @return true if final task date is within contest ONGOING period
     */
    private boolean verifyFinalTaskDate(entity.Task finalTask, int contestId) {
        boolean res = false;
        Contest contest = contestDao.find(contestId);

        if (contest != null) {
            if (finalTask.getStartDate().after(contest.getStartDate()) && finalTask.getFinishDate().before(contest.getFinishDate())) {

                res = true;
            }
        }
        return res;
    }

    /**
     * Get list of potential members of given project (waiting for response - not answered)
     *
     * @param id identifies project
     * @return list of PotentialProjMember DTO
     */
    public List<PotentialProjMember> getPotentialProjectMembers(int id) {

        List<dto.PotentialProjMember> list = new ArrayList<>();

        List<entity.ProjectMember> entList = projMemberDao.findListOfPotentialMembersByProjectId(id);

        if (entList != null) {

            for (entity.ProjectMember p : entList) {
                dto.PotentialProjMember pm = new dto.PotentialProjMember();
                pm.setId(p.getId());
                pm.setProjectId(p.getProjectToParticipate().getId());
                pm.setUserInvitedId(p.getUserInvited().getUserId());
                pm.setUserInvitedFirstName(p.getUserInvited().getFirstName());
                pm.setUserInvitedLastName(p.getUserInvited().getLastName());
                if (p.getUserInvited().getPhoto() != null) {
                    pm.setUserInvitedPhoto(p.getUserInvited().getPhoto());
                }
                pm.setAnswered(p.isAnswered());
                pm.setSelfInvitation(p.isSelfInvitation());

                list.add(pm);
            }
        }
        return list;
    }

    /**
     * Verifies of token is an active project member, since those are the only ones who have access to some project information: chat, records, tasks
     *
     * @param projId identifies project
     * @param token  identifies session that makes the request
     * @return true if token is indeed an active project member
     */
    public boolean isProjMember(int projId, String token) {
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.ProjectMember projMember = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, user.getUserId());

            if (projMember != null) {
                if (projMember.isAccepted() && !projMember.isRemoved()) {
                    res = true;
                    // token que faz request é membro do projecto, tendo permissão para fazer o request
                }
            }
        }

        return res;
    }

    /**
     * Get list of records of given project
     * Reverses list to present most recent ones at the top in frontend
     *
     * @param projId identifies project
     * @return list of ProjectHistory DTO
     */
    public List<ProjectHistory> getProjectRecords(int projId) {

        List<ProjectHistory> recordsList = new ArrayList<>();

        List<entity.ProjectHistory> list = recordDao.findListOfRecordsByProjId(projId);

        if (list != null) {
            for (entity.ProjectHistory r : list) {
                recordsList.add(convertRecordEntToDto(r));
            }
            Collections.reverse(recordsList);
        }
        return recordsList;
    }

    /**
     * Converts ProjectHistory entity to ProjectHistory DTO
     *
     * @param r represents ProjectHistory entity
     * @return ProjectHistory DTO
     */
    private ProjectHistory convertRecordEntToDto(entity.ProjectHistory r) {
        ProjectHistory recordDto = new ProjectHistory();
        recordDto.setId(r.getId());
        recordDto.setMessage(r.getMessage());
        recordDto.setCreationTime(r.getCreationTime());

        if (r.getTask() != null) {
            recordDto.setTaskId(r.getTask().getId());
            recordDto.setTaskTitle(r.getTask().getTitle());
        }

        recordDto.setAuthorId(r.getAuthor().getUserId());
        recordDto.setAuthorPhoto(r.getAuthor().getPhoto());
        recordDto.setAuthorFirstName(r.getAuthor().getFirstName());
        recordDto.setAuthorLastName(r.getAuthor().getLastName());

        return recordDto;
    }

    /**
     * Updates ProjectMember attributes that define relationship between given user and given project
     * Project Manager accepts (1) or rejects (0) other user self-invitation to participate in project
     * If user is to be accepted to participate in project, it needs to be first verified if there are available spots in project
     * After being accepted in project, user's pending invitations for other projects are rejected
     *
     * @param projMemberId identifies ProjectMember (entry in corresponding table)
     * @param projId       identfies project
     * @param token        identifies session that makes the request
     * @param answer       is 0 if participation in project is to be rejected and 1 if it is to be accepted
     * @return true
     */
    public boolean replyToSelfInvitation(int projMemberId, int projId, String token, int answer) {

        boolean res = false;

        entity.User loggedUser = tokenDao.findUserEntByToken(token);
        if (loggedUser != null) {
            entity.ProjectMember pm = projMemberDao.find(projMemberId);

            if (pm != null) {
                if (answer == 0) {
                    // REJECT não precisa de mais nenhuma validação
                    pm.setAnswered(true);
                    pm.setAccepted(false);
                    projMemberDao.merge(pm);
                    res = true;
                    LOGGER.info("User ID " + pm.getUserInvited().getUserId() + " is rejected to participate in project ID " + pm.getProjectToParticipate().getId() + " by user ID " + loggedUser.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

                    communicationBean.recordManagerResponseToSelfInvitation(loggedUser, pm.getUserInvited(), pm.getProjectToParticipate(), answer);
                    communicationBean.notifyPotentialMemberOfSelfInvitationResponse(pm, answer);

                } else if (answer == 1) {
                    // ACCEPT é preciso garantir que há vagas disponíveis

                    if (verifyIfProjectHasAvailableSpots(projId)) {
                        pm.setAnswered(true);
                        pm.setAccepted(true);
                        projMemberDao.merge(pm);
                        res = true;
                        LOGGER.info("User ID " + pm.getUserInvited().getUserId() + " is accepted to participate in project ID " + pm.getProjectToParticipate().getId() + " by user ID " + loggedUser.getUserId() + ". IP Address of request is " + userBean.getIPAddress());

                        communicationBean.recordManagerResponseToSelfInvitation(loggedUser, pm.getUserInvited(), pm.getProjectToParticipate(), answer);
                        communicationBean.notifyPotentialMemberOfSelfInvitationResponse(pm, answer);
                        userBean.refusePendingInvitations(pm.getUserInvited().getUserId());

                    }
                }
            }
        }

        return res;
    }

    /**
     * Gets list of project chat messages of given project
     *
     * @param token  identifies session that makes the request
     * @param projId identifies project
     * @return list of ProjectChat DTO
     */
    public List<ProjectChat> getProjectChatList(String token, int projId) {

        List<ProjectChat> listDto = new ArrayList<>();

        List<ProjectChatMessage> listEnt = projChatDao.findListOfMessagesByProjId(projId);
        if (listEnt != null) {
            for (ProjectChatMessage m : listEnt) {
                listDto.add(convertProjChatEntToDto(m));
            }
        }
        return listDto;
    }

    /**
     * Converts ProjectChatMessage entity to ProjectChatMessage DTO
     *
     * @param m represents ProjectChatMessage entity
     * @return ProjectChatMessage DTO
     */
    private ProjectChat convertProjChatEntToDto(ProjectChatMessage m) {
        ProjectChat message = new ProjectChat();
        message.setChatMessageId(m.getId());
        message.setCreationTime(m.getCreationTime());
        message.setMessage(m.getMessage());
        message.setUserSenderId(m.getMessageSender().getUserId());
        message.setUserSenderFirstName(m.getMessageSender().getFirstName());
        message.setUserSenderLastName(m.getMessageSender().getLastName());
        message.setUserSenderPhoto(m.getMessageSender().getPhoto());
        message.setProjectId(m.getProject().getId());

        return message;
    }

    /**
     * Verifies project status is not CANCELLED or FINISHED
     * Project chat can only be used to exchange messages while project status is not CANCELLED or FINISHED
     *
     * @param projId identifies project
     * @return true if there is no permission to chat
     */
    public boolean verifyPermissionToChat(int projId) {

        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            if (project.getStatus() == StatusProject.CANCELLED || project.getStatus() == StatusProject.FINISHED) {
                res = true; // n pode enviar mensagem
            }
        }
        return res;
    }

    /**
     * Adds a new message and associates it to given project
     *
     * @param projId  identifies project
     * @param message represents message details
     * @param token   identifies session that makes the request
     * @return ProjectChat DTO
     */
    public ProjectChat addMessageToProjectChat(int projId, ProjectChat message, String token) {
        ProjectChat messageDto = new ProjectChat();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Project project = projDao.findProjectById(projId);
            if (project != null) {
                ProjectChatMessage newMessage = new ProjectChatMessage();
                newMessage.setCreationTime(Date.from(Instant.now()));
                newMessage.setProject(project);
                newMessage.setMessageSender(user);
                newMessage.setMessage(message.getMessage());
                projChatDao.persist(newMessage);

                messageDto = convertProjChatEntToDto(newMessage);

                communicationBean.notifyProjectChatRealTime(messageDto, project);
            }
        }
        return messageDto;
    }

    /**
     * Verifies project status is IN PROGRESS because only then a manual record can be added
     *
     * @param projId identifies project
     * @return true if record is persisted successfully in database
     */
    public boolean verifyPermissionToAddManualRecord(int projId) {
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            if (project.getStatus() == StatusProject.PROGRESS) {
                res = true; //pode adicionar registo manual
            }
        }
        return res;

    }

    /**
     * Adds manual record to given project. It might or might not have a task to associate record with
     *
     * @param projId identifies project
     * @param record representes record details
     * @param token  identifies session that makes the request
     * @return ProjectHistory DTO
     */
    public ProjectHistory addManualRecord(int projId, ProjectHistory record, String token) {

        ProjectHistory recordDto = new ProjectHistory();

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            entity.Project project = projDao.findProjectById(projId);
            if (project != null) {
                entity.ProjectHistory recordEnt = new entity.ProjectHistory();
                recordEnt.setMessage(record.getMessage());
                recordEnt.setCreationTime(Date.from(Instant.now()));
                recordEnt.setAuthor(user);
                recordEnt.setProject(project);

                if (record.getTaskId() != 0) {
                    // tarefa foi definida para associar a registo
                    entity.Task task = taskDao.find(record.getTaskId());

                    if (task != null) {
                        recordEnt.setTask(task);
                    }
                }

                recordDao.persist(recordEnt);
                recordDto = convertRecordEntToDto(recordEnt);
                LOGGER.info("A new manual record ID " + recordEnt.getId() + " is associated with project ID: " + projId + " by user ID " + user.getUserId() + ". IP Address of request is " + userBean.getIPAddress());
            }
        }
        return recordDto;
    }

    /**
     * Filters projects that were declared winner in a contest
     *
     * @return list of Project DTO
     */
    public List<dto.Project> filterWinnerProjects() {
        List<dto.Project> projects = new ArrayList<>();
        List<entity.Project> list = contestDao.findListOfWinnerProjects();

        if (list != null) {
            for (entity.Project p : list) {
                projects.add(convertProjEntityToDto(p));
            }
        }
        return projects;
    }

    /**
     * Filters projects whose name contains given input (str) or has associated a skill or a keyword whose name contains given input
     *
     * @param str represents input that is written by user in frontend
     * @return list of Project DTO
     */
    public List<dto.Project> filterProjectsByNameSkillsAndKeywords(String str) {
        List<dto.Project> list = new ArrayList<>();
        Set<entity.Project> mergeSet = new HashSet<>();
        List<entity.Project> projSkills = skillDao.filterProjectsWhoHaveSkillMatchingStr(str.toLowerCase());
        List<entity.Project> projKeywords = keywordDao.filterProjectsWhoHaveKeywordMatchingStr(str.toLowerCase());
        List<entity.Project> projTitle = projDao.findProjectListContainingStr((str.toLowerCase()));

        if (projSkills != null) {
            mergeSet.addAll(projSkills);
        }

        if (projKeywords != null) {
            mergeSet.addAll(projKeywords);
        }

        if (projTitle != null) {
            mergeSet.addAll(projTitle);

        }

        List<entity.Project> mergeList = new ArrayList<>(mergeSet);

        if (mergeList != null) {
            for (entity.Project p : mergeList) {
                list.add(convertProjEntityToDto(p));
            }
        }

        return list;
    }
}
