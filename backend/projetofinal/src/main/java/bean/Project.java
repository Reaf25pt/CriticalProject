package bean;

import ENUM.Office;
import ENUM.StatusProject;
import ENUM.StatusTask;
import dto.Keyword;
import dto.Skill;
import dto.Task;
import entity.ProjectMember;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    public Project(){
    }

    public dto.Project convertProjEntityToDto(entity.Project p){

        dto.Project projDto = new dto.Project();

        projDto.setId(p.getId());
        projDto.setTitle(p.getTitle());

        if(p.getOffice()!=null){

        projDto.setOffice(p.getOffice().ordinal());
        }
        projDto.setDetails(p.getDetails());
        projDto.setResources(p.getResources());
        projDto.setStatus(p.getStatus());
     //   projDto.setStatus(p.getStatus().ordinal());
        projDto.setMembersNumber(p.getMembersNumber());
        projDto.setCreationDate(p.getCreationDate());

        if(p.getListKeywords()!=null) {
            // converter keyword ENT to DTO

            projDto.setKeywords(convertListKeywordsDTO(p.getListKeywords()));
        }

        if(p.getListSkills()!=null) {
            // converter skill ENT to DTO

            projDto.setSkills(convertListSkillsDTO(p.getListSkills()));
        }

return projDto;
    }

    private List<Keyword> convertListKeywordsDTO(List<entity.Keyword> listKeywords) {
        // convert keyword ENTITY  to keyword DTO list

        List<Keyword> listKeywordDTO = new ArrayList<>();

        for (entity.Keyword k : listKeywords){
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

        for (entity.Skill s : listSkills){
            Skill skill = new Skill();
            skill.setId(s.getSkillId());
            skill.setTitle(s.getTitle());
            skill.setSkillType(s.getType().ordinal());

            listSkillsDTO.add(skill);
        }

        return listSkillsDTO;
    }

    public boolean createNewProject (dto.Project project, String token){
//TODO verificar se criador tem projecto e n pode criar novo
        boolean res= false;

        entity.User userEnt = tokenDao.findUserEntByToken(token);

if (userEnt != null) {
    if (project != null && !projInfoIsFilledIn(project)) {

        entity.Project newProjEnt = new entity.Project();
        newProjEnt.setCreationDate(Date.from(Instant.now()));
        newProjEnt.setStatus(StatusProject.PLANNING);
        newProjEnt.setTitle(project.getTitle());
        newProjEnt.setDetails(project.getDetails());

        if (project.getOffice() != 20) {

            switch (project.getOffice()) {
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
            }
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


        LOGGER.info("User whose user ID is " + userEnt.getUserId() + " creates a new project, project ID: "+newProjEnt.getId()+". IP Address of request is " + userBean.getIPAddress());

        associateCreatorToProject(userEnt, newProjEnt);

        //TODO log project before associating keywords? what if something goes wrong in that process?
        associateKeywordsWithProject(project.getKeywords(), newProjEnt);

        if(project.getSkills()!=null || project.getSkills().size()!=0 ){
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

            LOGGER.info("User whose ID is " + user.getUserId()+" is a manager of project, project ID: "+project.getId()+". IP Address of request is " + userBean.getIPAddress());

    }

    private void associateKeywordsWithProject(List<Keyword> keywords, entity.Project newProjEnt) {
        // associar as keywords ao projecto. Se já existir na DB basta adicionar a relação senão é preciso criar a keyword e adicionar à DB
        // se encontrar keyword entity pelo title, apenas associa ao proj.

        for (Keyword k: keywords) {

            entity.Keyword keyw = keywordDao.findKeywordByTitle(k.getTitle().trim());

            if (keyw!= null){
                // já existe na DB, basta associar ao proj ---- adicionar a cada uma das listas ?!
                keyw.getListProject_Keywords().add(newProjEnt);
                newProjEnt.getListKeywords().add(keyw);

                projDao.merge(newProjEnt);
                keywordDao.merge(keyw);

                LOGGER.info("Keyword " + keyw.getId() + " is associated with project, project ID: "+newProjEnt.getId()+". IP Address of request is " + userBean.getIPAddress());


            } else {
                // não existe keyword para o title usado. É necessário criar e adicionar à DB

                entity.Keyword newKeyW = new entity.Keyword();
                newKeyW.setTitle(k.getTitle().trim());
                newKeyW.getListProject_Keywords().add(newProjEnt);

                keywordDao.persist(newKeyW);
                newProjEnt.getListKeywords().add(newKeyW);
                projDao.merge(newProjEnt);

                LOGGER.info("Keyword " + newKeyW.getId() + " is persisted in database and associated with project, project ID: "+newProjEnt.getId()+". IP Address of request is " + userBean.getIPAddress());
            }
        }
    }


    private void associateSkillsWithProject(List<Skill> skills, entity.Project newProjEnt) {
        // associar as skills ao projecto. Se já existir na DB basta adicionar a relação senão é preciso criar a skill e adicionar à DB
        // se encontrar skill entity pelo title, apenas associa ao proj.

        for (Skill s: skills) {

            entity.Skill skill = skillDao.findSkillByTitle(s.getTitle().trim());

            if (skill!= null){
                // já existe na DB, basta associar ao proj ---- adicionar a cada uma das listas ?!
                skill.getListProject_Skills().add(newProjEnt);
                newProjEnt.getListSkills().add(skill);

                projDao.merge(newProjEnt);
                skillDao.merge(skill);

                LOGGER.info("Skill " + skill.getSkillId() + " is associated with project, project ID: "+newProjEnt.getId()+". IP Address of request is " + userBean.getIPAddress());


            } else {
                // não existe skill para o title usado. É necessário criar e adicionar à DB

                entity.Skill newSkill = new entity.Skill();
                newSkill.setTitle(s.getTitle().trim());
                userBean.attributeSkillType(s.getSkillType(), newSkill);
                newSkill.getListProject_Skills().add(newProjEnt);

                skillDao.persist(newSkill);
                newProjEnt.getListSkills().add(newSkill);
                projDao.merge(newProjEnt);

                LOGGER.info("Skill " + newSkill.getSkillId() + " is persisted in database and associated with project, project ID: "+newProjEnt.getId()+". IP Address of request is " + userBean.getIPAddress());
            }

        }
    }

    private boolean projInfoIsFilledIn(dto.Project project) {
        // registo inicial do proj tem de incluir, no mínimo nome e descrição e ainda 1 keyword associada ao proj
        boolean res= false;

        if(userBean.checkStringInfo(project.getTitle()) || userBean.checkStringInfo(project.getDetails()) || project.getKeywords().isEmpty()){
            res=true;
            // projecto não inclui info indispensável no momento da sua criação
        }

        return res;
    }




    public boolean addMemberToProject (int projId, int userId, String token){
        // TODO validar aqui se ja existe relação??  SIM
        // 1º valida se já há relação entre user convidado e projecto na tabela projectMember. Se houver, apenas actualiza as infos
        // add member to given project. If userId of token == userId to add (self-invitation) sends notification to managers of project
        // if token ID NOT == userID to invite, send notification to user invited
        // TODO verify if userID is in active project or not even show in the frontend those users?! papel de gestor ou participante é definido posteriorment, de acordo com enunciado

        //TODO preparar para receber do frontend email, alcunha ou nome

        boolean res = false;

        entity.User user = userDao.findUserById(userId);
        entity.User userEnt = tokenDao.findUserEntByToken(token);
        entity.Project project= projDao.findProjectById(projId);

        if(user!=null && userEnt!= null && project!=null) {



            if (userEnt.getUserId()== userId){
                // self-invitation to participate in project
                //TODO colocar aqui o log de ter convite para participar no projecto ?!!?!
                ProjectMember projMember= associateUserToProject(user, project, false);
                communicationBean.notifyNewPossibleProjectMember(projMember, project, user, false);
                res=true;
            } else {
                // not self-invitation
                ProjectMember projMember= associateUserToProject(user, project, true);
                communicationBean.notifyNewPossibleProjectMember(projMember, project, user, true);
                res=true;
            }
        }


        return res;
    }

    private ProjectMember associateUserToProject(entity.User user, entity.Project project, boolean selfInvite/*, boolean manager*/) {
        // associa o membro ao projecto, inserindo a info na 3ª tabela e definindo a relação (gestor / participante)
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

        if(selfInvite){
            projMember.setSelfInvitation(true);
        } else {
            projMember.setSelfInvitation(false);
        }


        projMemberDao.persist(projMember);

        project.getListPotentialMembers().add(projMember);
        user.getListProjects().add(projMember);

        projDao.merge(project);
        userDao.merge(user);
int relationId = projMember.getId();
        LOGGER.info("User whose ID is " + user.getUserId()+" is invited to participate in project, project ID: "+project.getId()+". IP Address of request is " + userBean.getIPAddress());
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
        // ir a tabela projMember buscar todas as entradas cujo user associado ao token tenha relação com o projecto cujo id== projId.
        // TODO SIM FAZER assume-se que cada user tem apenas 1a relação com cada projecto. Talvez seja necessário mudar e proteger no método addMember para verificar 1º se relação já existe
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
       if (user!=null){
        ProjectMember projMember = projMemberDao.findProjectMemberByProjectIdAndUserId(projId, user.getUserId());

        if(projMember!= null){
            if(projMember.isManager()){
                res=true;
                // token que faz request é manager do projecto, tendo permissão para fazer o request
            }

        }}

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

        if(projEnt!= null){
            entity.User user = tokenDao.findUserEntByToken(token);
            if(user!=null){
project = convertProjEntityToDto(projEnt);

// definir relação do token com projecto: membro / gestor ou apenas alguém com interesse em ver detalhes do projecto
ProjectMember projMember = projMemberDao.findProjectMemberByProjectIdAndUserId(projEnt.getId(), user.getUserId());

if (projMember != null){
    if (projMember.isAccepted() && !projMember.isRemoved()) {
        // significa que user tem relação com projecto. Resta saber se membro ou gestor
        project.setMember(true);
        if (projMember.isManager()){
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

        List<ProjectMember> membersList= projMemberDao.findListOfMembersByProjectId(id);

        if(membersList!=null) {
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

        if(listEnt!= null){

                listDto = convertListKeywordsDTO(listEnt);
        }
        return listDto;
    }

    public boolean addTaskToProject(int projId, Task task, String token) {
        // adiciona nova task ao projecto cujo Id == projId
boolean res = false;
        entity.Task newTask = new entity.Task();

        newTask.setTitle(task.getTitle());
        newTask.setStartDate(task.getStartDate());
        newTask.setFinishDate(task.getFinishDate());
        newTask.setDetails(task.getDetails());

        newTask.setAdditionalExecutors(task.getAdditionalExecutors());

        newTask.setTaskOwner(userDao.findUserById(task.getTaskOwnerId()));

        newTask.setStatus(StatusTask.PLANNED);

        newTask.setProject(projDao.findProjectById(projId));


        taskDao.persist(newTask);

        // persistir 1º tarefa para então associar tarefas precedentes se houver
        System.out.println(task.getPreRequiredTasks().size());
        System.out.println(task.getPreRequiredTasks());
        if (task.getPreRequiredTasks()!= null ){

            associatePreRequiredTasksWithCurrentTask(task.getPreRequiredTasks(), newTask);
        }

        res=true;


return res;
    }

    private void associatePreRequiredTasksWithCurrentTask(List<Task> preRequiredTasks, entity.Task currentTask) {
        // associa cada preRequired task a current task
// TODO verificar se é necessário garantir que n adiciona a pp tarefa como pre required
        for (Task t: preRequiredTasks){
            // adicionar cada tarefa q seja pre requisito à lista da current task
            currentTask.getListPreRequiredTasks().add(taskDao.find(t.getId()));
        }

        taskDao.merge(currentTask);
   //TODO será necessário fazer merge de cada task t ? em teoria apenas a task tem lista de tarefas required
    }

    public boolean checkTaskInfo(Task task) {
        // verifica se campos obrigatórios da task estão preenchidos

        boolean res=false;

        if (userBean.checkStringInfo(task.getTitle()) || task.getStartDate()==null || task.getFinishDate() == null || userBean.checkStringInfo(task.getDetails())){
            // TODO falta verificar se id de responsavel está definido ou por oposiçao definir nome
        res=true;
        }


        return res;
    }

    public List<Task> getTasksList(int id) {
        // obter lista de tarefas associadas ao projecto ID

        List<Task> taskList = new ArrayList<>();

        entity.Project proj = projDao.findProjectById(id);

        if(proj!=null){
List<entity.Task> listEnt = taskDao.findTasksFromProjectByProjId(id);

if (listEnt!=null){
    for (entity.Task t : listEnt){
        taskList.add(convertTaskEntToDto(t));
    }
}
        }


        return taskList;
    }

    private Task convertTaskEntToDto(entity.Task t) {
        Task task = new Task();

        task.setTitle(t.getTitle());
        task.setStartDate(t.getStartDate());
        task.setFinishDate(t.getFinishDate());
        task.setDetails(t.getDetails());
        task.setStatus(t.getStatus());
        task.setTaskOwnerId(t.getTaskOwner().getUserId());
        task.setTaskOwnerFirstName(t.getTaskOwner().getFirstName());
        task.setTaskOwnerLastName(t.getTaskOwner().getLastName());
        task.setTaskOwnerPhoto(t.getTaskOwner().getPhoto());
        task.setAdditionalExecutors(t.getAdditionalExecutors());
        // TODO acrescentar lista de tarefas precedentes

        return task;
    }

    public boolean verifyIfUserCanCreateNewProject(String token) {
        // verifica se user tem algum projecto 'activo'. Se tiver não poderá criar novo projecto
        boolean res=false;

//TODO finish to implement

        return res;
    }


    public List<Skill> getSkillsList(String str) {
        // retrieve list of skills that contain title
        List<Skill> listSkillDto= new ArrayList<>();


            List<entity.Skill> list = skillDao.findSkillListContainingStr(str.toLowerCase());

            if(list!=null){
                for (entity.Skill s : list) {

                        listSkillDto.add(userBean.convertToSkillDto(s));
                    }}return listSkillDto;
    }



}
