package bean;

import ENUM.StatusProject;
import dto.Keyword;
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


    public Project(){
    }

    public dto.Project convertProjEntityToDto(entity.Project p){

        dto.Project projDto = new dto.Project();

        projDto.setId(p.getId());
        projDto.setTitle(p.getTitle());
        projDto.setOffice(p.getOffice());
        projDto.setDetails(p.getDetails());
        projDto.setResources(p.getResources());
        projDto.setStatus(p.getStatus());
        projDto.setMembersNumber(p.getMembersNumber());
        projDto.setCreationDate(p.getCreationDate());

        if(p.getListKeywords()!=null) {
            // converter keyword ENT to DTO

            projDto.setKeywords(retrieveListKeywordsDTO(p.getListKeywords()));
        }
return projDto;
    }

    private List<Keyword> retrieveListKeywordsDTO(List<entity.Keyword> listKeywords) {
        // get and convert keyword ENTITY associated with project to keyword DTO

        List<Keyword> listKeywordDTO = new ArrayList<Keyword>();

        for (entity.Keyword k : listKeywords){
            Keyword keyw = new Keyword();
            keyw.setId(k.getId());
            keyw.setTitle(k.getTitle());

            listKeywordDTO.add(keyw);
        }

        return listKeywordDTO;
    }


    public boolean createNewProject (dto.Project project, String token){

        boolean res= false;

        entity.User userEnt = tokenDao.findUserEntByToken(token);

if (userEnt != null) {
    if (project != null && !projInfoIsFilledIn(project)) {

        entity.Project newProjEnt = new entity.Project();
        newProjEnt.setCreationDate(Date.from(Instant.now()));
        newProjEnt.setStatus(StatusProject.PLANNING);
        newProjEnt.setTitle(project.getTitle());
        newProjEnt.setDetails(project.getDetails());

        if (project.getOffice() != null) {
            newProjEnt.setOffice(project.getOffice());
        }

        if (project.getResources() != null) {
            newProjEnt.setResources(project.getResources());
        }

        if (project.getMembersNumber() != 0) {
            // TODO no frontend colocar 0 se não houver input ?!
            newProjEnt.setMembersNumber(project.getMembersNumber());
        } else {
            newProjEnt.setMembersNumber(4);
        }

        projDao.persist(newProjEnt);
        System.out.println(newProjEnt.getId());


        LOGGER.info("User whose user ID is " + userEnt.getUserId() + " creates a new project, project ID: "+newProjEnt.getId()+". IP Address of request is " + userBean.getIPAddress());

        associateCreatorToProject(userEnt, newProjEnt);

        //TODO log project before associating keywords? what if something goes wrong in that process?
        associateKeywordsWithProject(project.getKeywords(), newProjEnt);

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
            entity.Keyword keyw = keywordDao.findKeywordByTitle(k.getTitle());

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
                newKeyW.setTitle(k.getTitle());
                newKeyW.getListProject_Keywords().add(newProjEnt);

                keywordDao.persist(newKeyW);
                newProjEnt.getListKeywords().add(newKeyW);
                projDao.merge(newProjEnt);

                LOGGER.info("Keyword " + newKeyW.getId() + " is persisted in database and associated with project, project ID: "+newProjEnt.getId()+". IP Address of request is " + userBean.getIPAddress());
            }
        }
// TODO  colocar TRIM() e testar - associado a keywords

    }

    private boolean projInfoIsFilledIn(dto.Project project) {
        // registo inicial do proj tem de incluir, no mínimo nome e descrição e ainda 1 keyword associada ao proj
        boolean res= false;

        if(checkStringInfo(project.getTitle()) || checkStringInfo(project.getDetails()) || project.getKeywords().isEmpty()){
            res=true;
            // projecto não inclui info indispensável no momento da sua criação
        }

        return res;
    }

    private boolean checkStringInfo(String str) {
        // check if a string info is null or blank
        boolean res = false;

        if(str == null || str.isBlank()){
            res=true;
            // info is not filled in as it should
        }

        return res;
    }


    public boolean addMemberToProject (int projId, int userId, String token){
        // TODO validar aqui se ja existe relação??
        // 1º valida se já há relação entre user convidado e projecto na tabela projectMember. Se houver, apenas actualiza as infos
        // add member to given project. If userId of token == userId to add (self-invitation) sends notification to managers of project
        // if token ID NOT == userID to invite, send notification to user invited
        // TODO verify if userID is in active project or not even show in the frontend those users?! papel de gestor ou participante é definido posteriorment, de acordo com enunciado

        boolean res = false;

        entity.User user = userDao.findUserById(userId);
        entity.User userEnt = tokenDao.findUserEntByToken(token);
        entity.Project project= projDao.findProjectById(projId);

        if(user!=null && userEnt!= null && project!=null) {

            int relationId= associateUserToProject(user, project);

            if (userEnt.getUserId()== userId){
                // self-invitation to participate in project
                //TODO colocar aqui o log de ter convite para participar no projecto ?!!?!
                communicationBean.notifyNewPossibleProjectMember(relationId, project, user, false);
                res=true;
            } else {
                // not self-invitation
                communicationBean.notifyNewPossibleProjectMember(relationId, project, user, true);
                res=true;
            }
        }


        return res;
    }

    private int associateUserToProject(entity.User user, entity.Project project/*, boolean manager*/) {
        // associa o membro ao projecto, inserindo a info na 3ª tabela e definindo a relação (gestor / participante)
        // se boolean manager for true - relação do user com projecto é de GESTOR.

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
return relationId;
    }


    public boolean isProjManager(String token, int projId) {
        // check if token has permission to modify project's info (is projManager of given project)
        // ir a tabela projMember buscar todas as entradas cujo user associado ao token tenha relação com o projecto cujo id== projId.
        // TODO assume-se que cada user tem apenas 1a relação com cada projecto. Talvez seja necessário mudar e proteger no método addMember para verificar 1º se relação já existe
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
}
