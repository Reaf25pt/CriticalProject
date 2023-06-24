package bean;

import ENUM.Office;
import ENUM.StatusProject;
import ENUM.StatusTask;
import dto.Keyword;
import dto.Skill;
import dto.Task;
import dto.UserInfo;
import entity.ProjectMember;
import entity.Token;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequestScoped
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(User.class);
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


    public Project() {
    }

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

        if (p.getListKeywords() != null) {
            // converter keyword ENT to DTO

            projDto.setKeywords(convertListKeywordsDTO(p.getListKeywords()));
        }

        if (p.getListSkills() != null) {
            // converter skill ENT to DTO

            projDto.setSkills(convertListSkillsDTO(p.getListSkills()));
        }

        return projDto;
    }


    private List<Keyword> convertListKeywordsDTO(List<entity.Keyword> listKeywords) {
        // convert keyword ENTITY  to keyword DTO list

        List<Keyword> listKeywordDTO = new ArrayList<>();

        for (entity.Keyword k : listKeywords) {
            Keyword keyw = new Keyword();
            keyw.setId(k.getId());
            keyw.setTitle(k.getTitle());

            listKeywordDTO.add(keyw);
        }

        return listKeywordDTO;
    }

    private List<Skill> convertListSkillsDTO(List<entity.Skill> listSkills) {
        // convert skill ENTITY list to skill DTO list

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

        /*    switch (project.getOffice()) {
                case 0:
                    newProjEnt.setOffice(Office.LISBOA);
                    break;
                case 1:
                    newProjEnt.setOffice(Office.COIMBRA);
                    break;
                case 2:
                    newProjEnt.setOffice(Office.PORTO);
                    break;
                case 3:
                    newProjEnt.setOffice(Office.TOMAR);
                    break;
                case 4:
                    newProjEnt.setOffice(Office.VISEU);
                    break;
                case 5:
                    newProjEnt.setOffice(Office.VILAREAL);
                    break;
            }*/
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
                // System.out.println(newProjEnt.getId());


                LOGGER.info("User whose user ID is " + userEnt.getUserId() + " creates a new project, project ID: " + newProjEnt.getId() + ". IP Address of request is " + userBean.getIPAddress());

                associateCreatorToProject(userEnt, newProjEnt);

                //TODO log project before associating keywords? what if something goes wrong in that process?
                associateKeywordsWithProject(project.getKeywords(), newProjEnt);

                if (project.getSkills() != null || project.getSkills().size() != 0) {
                    associateSkillsWithProject(project.getSkills(), newProjEnt);
                }

                res = true;

            }
        }
        return res;
    }

    private void associateCreatorToProject(entity.User user, entity.Project project) {

        ProjectMember projMember = new ProjectMember();
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

        LOGGER.info("User whose ID is " + user.getUserId() + " is a manager of project, project ID: " + project.getId() + ". IP Address of request is " + userBean.getIPAddress());

    }

    private void associateKeywordsWithProject(List<Keyword> keywords, entity.Project newProjEnt) {
        // associar as keywords ao projecto. Se já existir na DB basta adicionar a relação senão é preciso criar a keyword e adicionar à DB
        // se encontrar keyword entity pelo title, apenas associa ao proj.

        deleteKeywordsAssociatedWithProject(newProjEnt);

        for (Keyword k : keywords) {
            System.out.println(k.getTitle());
            entity.Keyword keyw = keywordDao.findKeywordByTitle(k.getTitle().trim());

            if (keyw != null) {
                // já existe na DB, verificar se já tem relação com proj. Se nao basta associar ao proj ---- adicionar a cada uma das listas ?!

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
                // não existe keyword para o title usado. É necessário criar e adicionar à DB
                System.out.println(k.getTitle());
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

    private void deleteKeywordsAssociatedWithProject(entity.Project newProjEnt) {
        // antes de adicionar keywords associadas a projecto, apaga todas as keywords associadas ao projecto
        // permite lidar com keywords removidas ao editar o projecto

        List<entity.Keyword> list = keywordDao.findListOfKeywordsByProjId(newProjEnt.getId());
        System.out.println(newProjEnt.getId());

        if (list != null) {

            //remove cada relação entre keyword e project
            for (entity.Keyword k : list) {

                k.getListProject_Keywords().remove(newProjEnt);
                newProjEnt.getListKeywords().remove(k);
                keywordDao.merge(k);
                projDao.merge(newProjEnt);
            }
        }
    }


    private void associateSkillsWithProject(List<Skill> skills, entity.Project newProjEnt) {
        // associar as skills ao projecto. Se já existir na DB basta adicionar a relação senão é preciso criar a skill e adicionar à DB
        // se encontrar skill entity pelo title, apenas associa ao proj.

        deleteSkillsAssociatedWithProject(newProjEnt);

        for (Skill s : skills) {

            entity.Skill skill = skillDao.findSkillByTitle(s.getTitle().trim());

            if (skill != null) {
                // já existe na DB, basta associar ao proj ---- adicionar a cada uma das listas ?!

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

    private void deleteSkillsAssociatedWithProject(entity.Project newProjEnt) {
        // antes de adicionar skills associadas a projecto, apaga todas as skills associadas ao projecto
        // permite lidar com skills removidas ao editar o projecto
        System.out.println("delete skills");
        List<entity.Skill> list = skillDao.findListOfSkillsByProjId(newProjEnt.getId());

        if (list != null) {
            //remove cada relação entre keyword e project
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


    public boolean addMemberToProject(int projId, int userId, String token) {

        // 1º valida se já há relação entre user convidado e projecto na tabela projectMember. Se houver, apenas actualiza as infos
        // add member to given project. If userId of token == userId to add (self-invitation) sends notification to managers of project
        // if token ID NOT == userID to invite, send notification to user invited

        boolean res = false;

        entity.User user = userDao.findUserById(userId); // a quem convite diz respeito
        entity.User userEnt = tokenDao.findUserEntByToken(token);
        entity.Project project = projDao.findProjectById(projId);

        if (user != null && userEnt != null && project != null) {

            // encontrar se ja existe relação prévia entre user a ser convidado e projecto. Se encontrar, altera-se os campos para novo convite senão faz-se nova antrada na tabela
            ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, userId);
            // pm pode ou não estar answered / accepted / removed
            if (pm != null) {
                System.out.println("existe relacao pm ");
                if (!pm.isRemoved()) {
                    System.out.println("n esta removed ");
                    if (pm.isAnswered()) {
                        System.out.println("está respondido ");
                        // se convite estiver pendente não faz nada. Senão altera info de pm entity

                        pm.setManager(false);
                        pm.setRemoved(false);
                        pm.setAccepted(false);
                        pm.setAnswered(false);

                        if (userEnt.getUserId() == userId) {
                            // self-invitation to participate in project

                            pm.setSelfInvitation(true);

                            projMemberDao.merge(pm);
                            pm.getProjectToParticipate().getListPotentialMembers().add(pm);
                            pm.getUserInvited().getListProjects().add(pm);

                            projDao.merge(pm.getProjectToParticipate());
                            userDao.merge(pm.getUserInvited());

                            communicationBean.notifyNewPossibleProjectMember(pm, pm.getProjectToParticipate(), pm.getUserInvited(), false);
                            res = true;
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
                        }
                    }

                } else {
                    // está removido mas pode querer participar novamente
                    pm.setManager(false);
                    pm.setRemoved(false);
                    pm.setAccepted(false);
                    pm.setAnswered(false);

                    if (userEnt.getUserId() == userId) {
                        // self-invitation to participate in project

                        pm.setSelfInvitation(true);

                        projMemberDao.merge(pm);
                        pm.getProjectToParticipate().getListPotentialMembers().add(pm);
                        pm.getUserInvited().getListProjects().add(pm);

                        projDao.merge(pm.getProjectToParticipate());
                        userDao.merge(pm.getUserInvited());

                        communicationBean.notifyNewPossibleProjectMember(pm, pm.getProjectToParticipate(), pm.getUserInvited(), false);
                        res = true;
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
                    }


                }


            } else {
                System.out.println("entra no else de n pm relation");
                // não há relação prévia. é preciso criar nova associação entre user e projecto
                if (userEnt.getUserId() == userId) {
                    // self-invitation to participate in project
                    //TODO colocar aqui o log de ter convite para participar no projecto ?!!?!
                    ProjectMember projMember = associateUserToProject(user, project, false);
                    communicationBean.notifyNewPossibleProjectMember(projMember, project, user, false);
                    res = true;
                } else {
                    // not self-invitation
                    ProjectMember projMember = associateUserToProject(user, project, true);
                    communicationBean.notifyNewPossibleProjectMember(projMember, project, user, true);
                    res = true;
                }
            }
        }

        return res;
    }

    private ProjectMember associateUserToProject(entity.User user, entity.Project project, boolean selfInvite/*, boolean manager*/) {
        // associa o membro ao projecto, inserindo a info na 3ª tabela e definindo a relação
        // se boolean selfInvite == true, auto-convite .

        ProjectMember projMember = new ProjectMember();
        projMember.setProjectToParticipate(project);
        projMember.setUserInvited(user);
      /*  if(manager) {
            // relação GESTOR
            projMember.setManager(true);
        } else {
            //relação PARTICIPANTE
            projMember.setManager(false);}
*/
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
//int relationId = projMember.getId();
        LOGGER.info("User whose ID is " + user.getUserId() + " is invited to participate in project, project ID: " + project.getId() + ". IP Address of request is " + userBean.getIPAddress());
//TODO change log accordingly self-invitation or not ?!  colocar este log no método de addMember ?

       /* if(manager) {
            // relação GESTOR
            LOGGER.info("User whose ID is " + user.getUserId()+" is a manager of project, project ID: "+project.getId()+". IP Address of request is " + userBean.getIPAddress());

        } else {
            //relação PARTICIPANTE
            LOGGER.info("User whose ID is " + user.getUserId()+" participates in project, project ID: "+project.getId()+". IP Address of request is " + userBean.getIPAddress());
        }*/
        return projMember;
    }


    public boolean isProjManager(String token, int projId) {
        // check if token has permission to modify project's info (is projManager of given project)
        // ir a tabela projMember buscar a entrada (assume-se apenas 1 relação entre proj e user que vai sendo actualizada) cujo user associado ao token tenha relação com o projecto cujo id== projId.
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            ProjectMember projMember = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, user.getUserId());

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

    public List<dto.Project> getAllProjectsList(String token) {

        List<dto.Project> projectsList = new ArrayList<>();

        List<entity.Project> list = projDao.findAll();

        for (entity.Project p : list) {
            projectsList.add(convertProjEntityToDto(p));

        }
        return projectsList;
    }

    public dto.Project getProject(String token, int id) {
        // Obter info de projecto pelo seu ID, incluindo lista de keywords e skills (pode ser nula). Enviar ainda o papel do token no projecto

        dto.Project project = new dto.Project();

        entity.Project projEnt = projDao.findProjectById(id);

        if (projEnt != null) {
            entity.User user = tokenDao.findUserEntByToken(token);
            if (user != null) {
                project = convertProjEntityToDto(projEnt);

// definir relação do token com projecto: membro / gestor ou apenas alguém com interesse em ver detalhes do projecto
                ProjectMember projMember = projMemberDao.findProjectMemberByProjectIdAndUserId(projEnt.getId(), user.getUserId());

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
            }
        }
        return project;
    }

    public List<dto.ProjectMember> getProjectMembers(int id) {
        // obter lista de membros activos de um projecto pelo seu ID

        List<dto.ProjectMember> members = new ArrayList<>();

        List<ProjectMember> membersList = projMemberDao.findListOfMembersByProjectId(id);

        if (membersList != null) {

            for (ProjectMember p : membersList) {
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

    public List<Keyword> getKeywordsList(String title) {
        // lista de keywords da DB que match title inserido

        List<Keyword> listDto = new ArrayList<>();

        List<entity.Keyword> listEnt = keywordDao.findKeywordListContainingStr(title.toLowerCase());

        if (listEnt != null) {

            listDto = convertListKeywordsDTO(listEnt);
        }
        return listDto;
    }

    public boolean addTaskToProject(int projId, Task task, String token) {
        // adiciona nova task ao projecto cujo Id == projId
        boolean res = false;
        entity.Task newTask = new entity.Task();

        if(task!= null){


        newTask.setTitle(task.getTitle());
        newTask.setStartDate(task.getStartDate());
            System.out.println(task.getStartDate());
        newTask.setFinishDate(task.getFinishDate());
            System.out.println(task.getFinishDate());
        newTask.setDetails(task.getDetails());

        newTask.setAdditionalExecutors(task.getAdditionalExecutors());

        //TODO proteger de nulo - pesquisar user à parte e n directamente?
        newTask.setTaskOwner(userDao.findUserById(task.getTaskOwnerId()));

        newTask.setStatus(StatusTask.PLANNED);

        newTask.setProject(projDao.findProjectById(projId));


        taskDao.persist(newTask);

        // persistir 1º tarefa para então associar tarefas precedentes se houver
        System.out.println(task.getPreRequiredTasks().size());
        System.out.println(task.getPreRequiredTasks());
        if (task.getPreRequiredTasks() != null) {

            associatePreRequiredTasksWithCurrentTask(task.getPreRequiredTasks(), newTask);
        }

        res = true;
        }

        return res;
    }

    private void associatePreRequiredTasksWithCurrentTask(List<Task> preRequiredTasks, entity.Task currentTask) {
        // associa cada preRequired task a current task
        System.out.println(preRequiredTasks.size());
        for (Task t : preRequiredTasks) {
            entity.Task taskEnt= taskDao.find(t.getId());
            if (taskEnt.getFinishDate().before(currentTask.getStartDate())){

            // adicionar cada tarefa q seja pre requisito à lista da current task, desde que as datas não se sobreponham!!!
            currentTask.getListPreRequiredTasks().add(taskDao.find(t.getId()));
        }}

        taskDao.merge(currentTask);
        //TODO será necessário fazer merge de cada task t ? em teoria apenas a task tem lista de tarefas required
    }

    public boolean checkTaskInfo(Task task) {
        // verifica se campos obrigatórios da task estão preenchidos

        boolean res = false;

        if (userBean.checkStringInfo(task.getTitle()) || task.getStartDate() == null || task.getFinishDate() == null || userBean.checkStringInfo(task.getDetails())) {
            // TODO falta verificar se id de responsavel está definido ou por oposiçao definir user
            res = true;
        }


        return res;
    }

    public List<Task> getTasksList(int id) {
        // obter lista de tarefas associadas ao projecto ID

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
        if (t.getListPreRequiredTasks()!=null){
            task.setPreRequiredTasks(convertTaskEntToMinimalDto(t.getListPreRequiredTasks()));
        }


        return task;
    }

    private List<Task> convertTaskEntToMinimalDto(List<entity.Task> listPreRequiredTasks) {
        // método que converte task entity em dto, apresentando apenas a info mínima: ID, title e status

        List<Task> list = new ArrayList<>();

        for (entity.Task t : listPreRequiredTasks){
            Task task = new Task();
            task.setId(t.getId());
            task.setTitle(t.getTitle());
            task.setStatus(t.getStatus().getStatus());
            task.setStatusInfo(t.getStatus().ordinal());

            list.add(task);
        }
return list;
    }

    public boolean verifyIfUserHasActiveProject(String token) {
        // verifica se user tem algum projecto 'activo'. Se tiver não poderá criar novo projecto nem participar noutro
        boolean res = false;


        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            List<entity.Project> projectsList = projMemberDao.findListOfProjectsByUserId(user.getUserId());
            if (projectsList != null) {
                int count = 0;
                for (entity.Project p : projectsList) {
                    if (p.getStatus() != StatusProject.CANCELLED || p.getStatus() != StatusProject.FINISHED) {
                        count++;
                    }

                }
                System.out.println("count de projectos activos" + count);
                if (count == 0) {
                    res = true;
                    // pode criar ou participar num proj
                }
            }

        }

        return res;
    }


    public List<Skill> getSkillsList(String str) {
        // retrieve list of skills that contain title
        List<Skill> listSkillDto = new ArrayList<>();


        List<entity.Skill> list = skillDao.findSkillListContainingStr(str.toLowerCase());

        if (list != null) {
            for (entity.Skill s : list) {

                listSkillDto.add(userBean.convertToSkillDto(s));
            }
        }
        return listSkillDto;
    }


    public boolean editProjectInfo(String token, dto.Project editProj) {
        // editar info do projecto

        boolean res = false;

        entity.Project projEnt = projDao.findProjectById(editProj.getId());

        if (projEnt != null) {
            projEnt.setTitle(editProj.getTitle());
            if (editProj.getOffice() != 20) {
                projEnt.setOffice(setOffice(editProj.getOffice()));
          /*      switch (editProj.getOffice()) {
                    case 0:
                        projEnt.setOffice(Office.LISBOA);
                        break;
                    case 1:
                        projEnt.setOffice(Office.COIMBRA);
                        break;
                    case 2:
                        projEnt.setOffice(Office.PORTO);
                        break;
                    case 3:
                        projEnt.setOffice(Office.TOMAR);
                        break;
                    case 4:
                        projEnt.setOffice(Office.VISEU);
                        break;
                    case 5:
                        projEnt.setOffice(Office.VILAREAL);
                        break;
                }*/
            }
            projEnt.setDetails(editProj.getDetails());
            projEnt.setResources(editProj.getResources());
            projEnt.setStatus(setProjectStatus(editProj.getStatusInt()));
            projEnt.setMembersNumber(editProj.getMembersNumber());
            projEnt.setCreationDate(editProj.getCreationDate());

            projDao.merge(projEnt); // TODO será aqui ou depois de associar skills e keywords?

            associateKeywordsWithProject(editProj.getKeywords(), projEnt);

            if (editProj.getSkills() != null || editProj.getSkills().size() != 0) {
                System.out.println("associar skills");
                associateSkillsWithProject(editProj.getSkills(), projEnt);
            }
            projDao.merge(projEnt); // TODO será aqui ou antes de associar skills e keywords?
            res = true;
        }


        return res;
    }

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

    public List<UserInfo> getPossibleMembers(String name) {
        // retorna lista de users que não têm projecto activo, podendo ser sugeridos

        // TODO prevenir sugerir users com convite pendente no projecto em questão?

        List<UserInfo> listToSuggest = new ArrayList<>();
        List<entity.User> tempList = new ArrayList<>();

// implica ir buscar todos os utilizadores que n tenham projecto activo (passa pela tabela projMember e projecto, para obter o status)
        List<entity.User> matchingUsers = userDao.findUserContainingStr(name);

        if (matchingUsers != null) {
            List<entity.User> usersWithActiveProject = projMemberDao.findListOfUsersWithActiveProject();

            if (usersWithActiveProject != null) {
                // retirar estes users da lista all users: adicionando apenas o que n coincidem a uma lista auxiliar
                tempList = matchingUsers.stream().filter(user -> !usersWithActiveProject.contains(user)).collect(Collectors.toList());
                System.out.println(tempList.size());


            }
            if (!tempList.isEmpty()) {
                // significa que tem users para apresentar. Converter para dto
                for (entity.User u : tempList) {
                  /*  UserInfo userDto = new UserInfo();
                    userDto.setId(u.getUserId());
                    userDto.setFirstName(u.getFirstName());
                    userDto.setLastName(u.getLastName());
                    userDto.setNickname(u.getNickname());
                    userDto.setPhoto(u.getPhoto());*/

                    listToSuggest.add(convertUserToUserInfoDto(u));
                }
            }
        }
        return listToSuggest;
    }

    private UserInfo convertUserToUserInfoDto(entity.User u) {
        UserInfo userDto = new UserInfo();
        userDto.setId(u.getUserId());
        userDto.setFirstName(u.getFirstName());
        userDto.setLastName(u.getLastName());
        userDto.setNickname(u.getNickname());
        userDto.setPhoto(u.getPhoto());

        return userDto;
    }

    public boolean deleteProjMember(int userId, int projId, String token) {
        // remove projMember relationship with project (não apaga na BD mas apenas setRemove = true
        // só pode remover se ficar pelo menos 1 gestor no projecto após remoção.
        // Incluir possibilidade de se auto-remover
        boolean delete = false;

        ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, userId);

        if (pm != null) {
            boolean res = hasEnoughManagers(projId, userId);

            if (res) {
                // pode remover user
                if (pm.getProjectToParticipate().getStatus() == StatusProject.CANCELLED || pm.getProjectToParticipate().getStatus() == StatusProject.FINISHED) {
                    pm.setRemoved(true);
                    projMemberDao.merge(pm);
                    delete = true;
                } else {
                    System.out.println("Proj not concluded / finished ");
                    // TODO antes de retirar, confirmar que membro n é responsavel por nenhuma tarefa. se for tem de alterar isso se projecto n estiver finished ou cancelled .
                    boolean canLeave = dealWithTasksBeforeLeavingProject(userId, pm.getProjectToParticipate());

                    if (canLeave) {
                        System.out.println("pode sair");
                        pm.setRemoved(true);
                        projMemberDao.merge(pm);
                        delete = true;
                    }
                }

            }
        }

        return delete;
    }

    public boolean hasEnoughManagers(int projId, int userId) {
        // verifica numero de gestores do projecto. se for 2 ou maior é ok. Se for 1 tem de verificar se userId a remover é igual a userID de gestor
        boolean res = false;

        List<entity.User> managersList = projMemberDao.findListOfManagersByProjectId(projId);
        if (managersList != null && managersList.size() != 0) {
            if (managersList.size() >= 2) {
                // pode remover à vontade
                res = true;
            } else {
                // só tem 1 gestor. É preciso garantir que id do gestor não é o mesmo do user a remover
                for (entity.User u : managersList) {
                    if (u.getUserId() != userId) {
                        // gestor é outro user. pode remover à vontade
                        res = true;
                    }
                }
            }
        }
        return res;
    }

    public boolean verifyPermissionToAddMember(String token, int projId, int userId) {
        // verifica se token é gestor se for diferente do user id ou   se userid == token (auto-convite)

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

    public boolean verifyPermissionToDeleteUser(String token, int projId, int userId) {
        // verifica se token é gestor. se for diferente do user id ou se userID == token (auto-remove do projecto)
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

    public boolean verifyIfProjectHasAvailableSpots(int projId) {
        //verifica se projecto tem vagas disponíveis para adicionar membro
        // Compara o numero de membros activos com número máx de participantes que projecto pode ter
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            List<ProjectMember> activeMembers = projMemberDao.findListOfMembersByProjectId(projId);

            if (activeMembers != null) {
                if (activeMembers.size() < project.getMembersNumber()) {
                    res = true;
                }
            }

        }
        return res;
    }

    private int getNumberOfAvailableSpots(entity.Project p) {
        //retorna número de vagas disponíveis

        int count = 0;

        List<ProjectMember> activeMembers = projMemberDao.findListOfMembersByProjectId(p.getId());
        if (activeMembers != null) {
            count = p.getMembersNumber() - activeMembers.size();
        } else {
            count = p.getMembersNumber();
        }

        return count;
    }

    public boolean changeMemberRole(int userId, int projId, String token, int role) {
        // altera o papel do userId. 1 - gestor   /   0 - participante normal

        boolean res = false;

        // encontrar relação entre user e projecto, para garantir que está válida
        ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, userId);
        if (pm != null) {

            if (pm.isAccepted() && !pm.isRemoved()) {

                if (role == 0) {
                    // tem de 1º verificar se numero de gestores é >=2 ou, sendo 1 que não é o do pp
                    if (hasEnoughManagers(projId, userId)) {
                        pm.setManager(false);
                        projMemberDao.merge(pm);
                        res = true;
                    }
                } else if (role == 1) {

                    pm.setManager(true);
                    projMemberDao.merge(pm);
                    res = true;
                }
            }
        }
        return res;
    }

    public boolean editProjectStatus(String token, int projId, int status) {
        // altera estado do projecto, por intervenção de gestor do projecto
        // apenas considera os estados planning, ready, in progress, cancelled, finished. os outros 2 serão automáticos

        boolean res = false;
        System.out.println("tentar alterar " + status);

        // TODO acabar de implementar

        entity.Project project = projDao.findProjectById(projId);
        if (project != null) {
            switch (status) {
                case 0:
                    // definir como planning: proj está em ready e é preciso alterar status para permitir editar info do projecto
                    System.out.println("0 ");
                    project.setStatus(StatusProject.PLANNING);
                    projDao.merge(project);
                    res = true;
                    break;
                case 1:
                    //definir como ready: projecto está em planning. Proj ready é um projecto que está pronto para ser apresentado a um concurso
                    // não permite edição de info e tem de se assegurar que tarefa final existe

                    // TODO implementar parte da tarefa final
                    System.out.println("1 ");
                    project.setStatus(StatusProject.READY);
                    projDao.merge(project);
                    res = true;
                    break;
                case 4:
                    // definir como in progress: se n for automático - proj está approved
                    res = true;

                    break;
                case 5:
                    // cancelar projecto pode ser feito em qq altura
                     // TODO inactivar chat

                    project.setStatus(StatusProject.CANCELLED);
                    projDao.merge(project);
                    res = true;
                    break;
                case 6:
                    // TODO acabar de implementar
                    //definir como finished: proj tem de estar in progress e verificar se precisa de ter tarefas todas concluidas ou outras verificações
                    res = true;
                    break;
                case 7:
                    // TODO acabar de implementar
                    //definir como ready um projecto cancelado se não está associado a nenhum concurso
                    res = true;
                    break;
            }
        }
        return res;
    }

    public boolean dealWithTasksBeforeLeavingProject(int userId, entity.Project project) {
        // método que verifica se user tem tarefas à sua responsabilidade que não estejam finished antes de poder sair / ser retirado do projecto.
        // se tiver, terá de ser escolhido outro membro para ser responsável ou, n havendo mais nenhum membro, n pode sair
        boolean res = false;
        List<entity.Task> taskList = taskDao.findListOfTasksFromProjectByProjIdWhoseTaskOwnerIsGivenUserId(project.getId(), userId);
// se lista for nula não precisa de fazer nada
        System.out.println("metodo deal with tasks before leave project ");
        if (taskList != null) {
            // precisa de ser atribuido um membro activo do projecto a cada uma das tarefas. Gestor, porque poderá alterar e não ficar à espera de outros para alterar essa nova info, se quiser

            List<entity.User> managersList = projMemberDao.findListOfManagersByProjectId(project.getId());
            if (managersList != null) {
                // retirar o user que será removido, caso seja manager
                List<entity.User> tempList = managersList.stream().filter(user -> user.getUserId() != userId).collect(Collectors.toList());
                int count = 0;
                for (entity.Task t : taskList) {
                    entity.User randomManager = selectRandomUserFromList(tempList);
                    if (randomManager != null) {
                        t.setTaskOwner(randomManager);
                        // TODO Será preciso remover da lista do user que será removido?
                        // TODO adicionar registo no historico do projecto e notificação para novo owner. talvez mais avisos ?!
                        randomManager.getListTasks().add(t);
                        userDao.merge(randomManager);
                        taskDao.merge(t);
                        count++;

                    }
                }
                if (count == taskList.size()) {
                    System.out.println("all tasks dealt with");
                    // significa que todas as tarefas foram dealed with
                    res = true;
                }

            }


        } else {
            System.out.println("lista de tarefas nula");
            // lista nula, n tem de fazer nada
            res=true;
        }

        return res;
    }

    private entity.User selectRandomUserFromList(List<entity.User> tempList) {
// permite atribuir um user random da lista
        Random value = new Random();
        int index = value.nextInt(tempList.size());
        return tempList.get(index);
    }

/*    public boolean verifyProjectStatusToChangeTask(int projId) {
        // impedir caso projecto tenha status finished, cancelled, proposed, approved pq nestes casos o plano de execução não pode ser alterado
boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if(project!=null){
            if (project.getStatus()==StatusProject.PROPOSED || project.getStatus()==StatusProject.APPROVED|| project.getStatus()==StatusProject.CANCELLED|| project.getStatus()==StatusProject.FINISHED){
                res = true;
                // tarefas do plano de execução não poderão ser alteradas
            }

        }
        return res;
    }*/

    public boolean verifyProjectStatusToModifyTask(int projId) {
        // impedir caso projecto tenha status finished, cancelled, proposed, approved, ready pq nestes casos o plano de execução não pode ser alterado
        // vale para adicionar, editar, apagar tarefa
        boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if(project!=null){
            if (project.getStatus()==StatusProject.READY ||project.getStatus()==StatusProject.PROPOSED || project.getStatus()==StatusProject.APPROVED|| project.getStatus()==StatusProject.CANCELLED|| project.getStatus()==StatusProject.FINISHED ){
                res = true;
                // tarefas do plano de execução não poderão ser alteradas
            }

        }
        return res;
    }



    public boolean verifyIfTaskBelongsToProject(int taskId, int projId) {
        // verifica se task realmente está associada ao projecto
boolean res=false;
        entity.Task task = taskDao.find(taskId);

        if(task!=null){
            if (task.getProject().getId()==projId){
                res = true;
            }
        }

      return res;
    }

    public boolean deleteTask(String token, int taskId) {
        // apaga tarefa da BD se não tiver associação com nenhuma outra tarefa, nos 2 sentidos
        // TODO nao finalizado, encontrar todas as tasks que tenham a que sera apagada associada ???

        boolean res = false;
        entity.Task taskEnt = taskDao.find(taskId);
        if (taskEnt != null) {

            List<entity.Task> listTasksWhichCurrentTaskIsPreRequired = findTasksWhoHaveCurrentTaskAsPrecedent(taskEnt);
            // tarefa a ser apagar pode ser precedente de outras tarefas
            //TODO Decidir o que fazer, permitir agora apaga apenas se n tiver relação com outras?!
            if (listTasksWhichCurrentTaskIsPreRequired.isEmpty() && taskEnt.getListPreRequiredTasks().isEmpty()) {
                      /*  if(taskEnt.getListPreRequiredTasks()!=null){
                /*for (entity.Task t : taskEnt.getListPreRequiredTasks()){
                    taskEnt.getListPreRequiredTasks().remove(t);*/
             /*   taskEnt.getListPreRequiredTasks().clear();
                    taskDao.merge(taskEnt);
                }*/
                entity.Project projEnt = taskEnt.getProject();

                if (projEnt != null) {
                    projEnt.getListTasks().remove(taskEnt);

                    entity.User user = taskEnt.getTaskOwner();

                    if (user != null) {
                        user.getListTasks().remove(taskEnt);
                        userDao.merge(user);
                    }

                    projDao.merge(projEnt);

                }
                taskDao.remove(taskEnt);
                res = true;

            }
        }
        return res;
    }

    private List<entity.Task> findTasksWhoHaveCurrentTaskAsPrecedent(entity.Task taskEnt) {
        // método que procura lista de tarefas que tenham taskEnt como pre required

        List<entity.Task> list = new ArrayList<>();
        List<entity.Task> allTasks = taskDao.findAll();

        if(allTasks!=null){
            for (entity.Task t : allTasks){
                if (t.getListPreRequiredTasks().contains(taskEnt)){
                    list.add(t);
                }
            }
        }


        return list;
    }



    public boolean verifyProjectStatusToEditTaskStatus(int projId) {
//status  de tarefa apenas pode ser alterado se projecto estiver em fase IN PROGRESS
boolean res = false;
        entity.Project project = projDao.findProjectById(projId);
        if(project!=null){
            if (project.getStatus()!=StatusProject.PROGRESS  ){
                res = true;
                // status de tarefas do plano de execução não poderá ser alterado
            }

        }
        return res;
    }

    public boolean editTask(String token, Task editTask) {
        // editar tarefa: preciso sempre garantir que datas não impactam outras tarefas associadas. Projecto in progress tb precisa de verificar data de concurso

        // TODO alterar para caso em que tarefas sao modificadas com projecto em modo progress - verificar datas

        boolean res = false;
        boolean res1= false;
        boolean res2= false;
            entity.Task taskEnt = taskDao.find(editTask.getId());

            if(taskEnt!= null){
// TODO falta implementar para projecto que esteja in progress
                if(taskEnt.getProject().getStatus()!=StatusProject.PROGRESS){
                    // editar tarefa num projecto que não está em andamento. Não é preciso confirmar datas de concurso
                    if(editTask.getStartDate()!= taskEnt.getStartDate()){
                        //significa que data inicio foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, precendentes

                         res1 = checkNewDatesCompatibilityWithPreRequiredTasks(editTask);


                    }
                    if (editTask.getFinishDate()!=taskEnt.getFinishDate()){
                        //significa que data final foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, que tenham esta tarefa como precedente
                        res2=checkNewDatesCompatibilityWithAssociatedTasks(editTask, taskEnt);
                    }

                    if(!res1 && !res2 ){
                        //ambos são false, significa que n há conflitos de datas. A tarefa pode ser editada

                        taskEnt.setTitle(editTask.getTitle());
                        taskEnt.setStartDate(editTask.getStartDate());
                        taskEnt.setFinishDate(editTask.getFinishDate());
                        taskEnt.setDetails(editTask.getDetails());
                        taskEnt.setAdditionalExecutors(editTask.getAdditionalExecutors());

                        if(taskEnt.getTaskOwner().getUserId()!=editTask.getTaskOwnerId()){
                            //alteração de membro responsável. Verificar se é membro do projecto
                            // TODO redundante pq no frontend so aparecem membros
                            ProjectMember pm = projMemberDao.findProjectMemberByProjectIdAndUserId(taskEnt.getProject().getId(), editTask.getTaskOwnerId());

                            if(pm !=null){
                                if(pm.isAccepted() && !pm.isRemoved()){
                                    // relação com projecto está activa
                                    entity.User user = userDao.findUserById(editTask.getTaskOwnerId());
                                    if(user!=null){
                                        taskEnt.setTaskOwner(user);
                                    }
                                }
                            }
                        }
                        taskDao.merge(taskEnt); // merge antes de associar, pq lá já faz merge da taskEnt e resultava na duplicação das pre required tasks
                        if(editTask.getPreRequiredTasks()!=null || editTask.getPreRequiredTasks().size()!=0){

                           // deletePreRequiredTasksWithCurrentTask(taskEnt);
                            taskEnt.getListPreRequiredTasks().clear();
                            taskDao.merge(taskEnt);

                            associatePreRequiredTasksWithCurrentTask(editTask.getPreRequiredTasks(), taskEnt);
                        }

                        res=true;
                    }
                }

            }
        return res;
    }

/*
    private void deletePreRequiredTasksWithCurrentTask( entity.Task currentTask) {
        // apaga cada preRequired task a current task  //
        System.out.println("metodo apagar pre required ");
        List<entity.Task> tempList = currentTask.getListPreRequiredTasks();

        for(entity.Task t : tempList){
            currentTask.getListPreRequiredTasks().remove(t);
            System.out.println("apaga task " + t.getId());
        }
        taskDao.merge(currentTask);
    }
*/

    private boolean checkNewDatesCompatibilityWithPreRequiredTasks(Task editTask) {
        //significa que data inicio foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, precendentes
        // comparar nova data inserida com data final de todas as tarefas que venham do frontend como sendo precedentes, pq lista actual na BD pode n igual
        boolean res = false;
        int count = 0;

        if(editTask.getPreRequiredTasks()!=null){
            for (Task t : editTask.getPreRequiredTasks()){
                // para cada Dto que vem do frontend é preciso ir buscar ENT à DB para comparar data final com nova data de inicio
                entity.Task taskE = taskDao.find(t.getId());

                if (!taskE.getFinishDate().before(editTask.getStartDate())){
                    System.out.println("finish " + taskE.getFinishDate());
                    System.out.println("start " + editTask.getStartDate());
                    //significa que data não é compatível
                    count++;
                }

            }
            if (count!=0){
                // significa que há conflito de datas com alguma tarefa
                res=true;
            }
        }
return res;
    }

    private boolean checkNewDatesCompatibilityWithAssociatedTasks(Task editTask, entity.Task taskEnt) {
        //significa que data final foi alterada e é preciso garantir que não interfere com datas de tarefas associadas, que tenham esta tarefa como precedente
        // é preciso obter lista de tarefas que tenham a tarefa a editar como sendo prerequired e comparar a nova data final com data de inicio de cada uma dessas tarefas
    boolean res= false;

    int count =0;

    List<entity.Task> tasksWhoHaveCurrentTaskAsPrecedent = findTasksWhoHaveCurrentTaskAsPrecedent(taskEnt);

    if(tasksWhoHaveCurrentTaskAsPrecedent != null){
        for (entity.Task t : tasksWhoHaveCurrentTaskAsPrecedent){
            // para cada tarefa, comparar data de inicio da tarefa t com nova data final do DTO
            if (!t.getStartDate().after(editTask.getFinishDate())){
                //significa que data não é compatível
                System.out.println("finish " + editTask.getFinishDate());
                System.out.println("start " + t.getStartDate());
                count++;
            }
        }
        if (count!=0){
            // significa que há conflito de datas com alguma tarefa
            res=true;
        }
    }


    return res;
    }

    public boolean verifyTaskStatusToEditTask(int id) {
        // verifica se status da tarefa é planning ou in progress, para que possa ser alterada
        boolean res=false;

        entity.Task task = taskDao.find(id);
        if(task!=null){
            if(task.getStatus()==StatusTask.FINISHED){
                res=true;
                // tarefa n pode ser alterada
            }
        }


        return res;
    }

    public boolean editTaskStatus(String token, Task editTask) {
        // editar status da tarefa: tarefa pode passar de planned para in progress  ou de in progress para finished, nunca 'retroceder na escala'
        // envia do frontend statusInfo = 1  para mudar para IN PROGRESS  / statusInfo = 2 para mudar para FINISHED

        boolean res=false;

        entity.Task taskEnt = taskDao.find(editTask.getId());

        if(taskEnt!=null) {
            if (taskEnt.getListPreRequiredTasks() != null) {
                if (checkPreRequiredTasksAreFinished(taskEnt.getListPreRequiredTasks())) {

                    if (editTask.getStatusInfo() == 1 && taskEnt.getStatus().ordinal() == 0) {
                        taskEnt.setStatus(StatusTask.PROGRESS);
                        taskDao.merge(taskEnt);
                        res = true;
                    } else if (editTask.getStatusInfo() == 2 && taskEnt.getStatus().ordinal() == 1) {
                        taskEnt.setStatus(StatusTask.FINISHED);
                        taskDao.merge(taskEnt);
                        res = true;
                    } else {
                        res = false;
                    }

                }
            }
        }

        return res;
    }

    private boolean checkPreRequiredTasksAreFinished(List<entity.Task> listPreRequiredTasks) {
        // verifica se tarefas precedentes estão concluídas para que status de task possa ser alterado
        // se count for diferente de 0, significa que alguma tarefa precedente n está concluída, n podendo mudar status da tarefa
      boolean res=true;
        int count = 0;
        for (entity.Task t: listPreRequiredTasks){
            if(t.getStatus()!=StatusTask.FINISHED){
                count++;
            }
        }

        if (count!=0){
            res=false;
        }

     return res;
    }

    public boolean verifyPermissionToEditTaskStatus(String token, int taskId) {
        // verifica se token é gestor do projecto ou owner da tarefa - únicas pessoas com autorização para editar status de tarefa
boolean res=false;


        entity.Task task = taskDao.find(taskId);
        if(task!=null){

            if(isProjManager(token, task.getProject().getId())){
                // é gestor, pode mudar
                res=true;
            } else {
                // verificar se token é owner da tarefa
                entity.User loggedUser=tokenDao.findUserEntByToken(token);
                if(loggedUser.getUserId()==task.getTaskOwner().getUserId()){
                    // token é owner da tarefa, podendo alterar o seu status
                    res=true;
                }

            }


        }


return res;
    }

    public boolean verifyPermisionToEditProjectInfo(int id) {
        // verifica se status do projecto é planning, pois só assim os detalhes do projecto podem ser alterados
        boolean res = false;

        entity.Project project = projDao.findProjectById(id);

        if(project!=null){
            if(project.getStatus()!=StatusProject.PLANNING){
                res=true;
                // projecto n pode ser editado
            }
        }

        return res;
    }
}
