package bean;

import ENUM.StatusContest;
import ENUM.StatusProject;
import dto.Application;
import dto.Task;
import entity.ContestApplication;
import entity.Project;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestScoped
public class Contest {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(User.class);
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

    public Contest(){
    }


    public boolean createNewContest(dto.Contest contestInfo, String token){
        // datas são verificadas no frontend
        boolean res = false;

        if (contestInfo != null){
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
            communicationBean.notifyAllContestManagers(0, "");
            res=true;
        }
        return res;
    }


    public boolean verifyUserProfile(String token) {
        // verifica se user profile é do tipo PERFIL A - gestor de concursos
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if(user!=null){
            if (user.isContestManager()){
                res=true;
            }
        }

        return res;
    }

    public List<dto.Contest> getAllContests(String token) {
        // obter a lista de todos os concursos na DB

        List<dto.Contest> list = new ArrayList<>();

        List<entity.Contest> tempList = contestDao.findAll();

        for (entity.Contest c : tempList) {
            list.add(convertContestEntToDto(c));
        }
        return list;
    }

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

        if(c.getWinner()!=null){
            contestDto.setWinnerProjectId(c.getWinner().getId());
        }
        return contestDto;
    }

    public dto.Contest getContest(String token, int id) {
        //obter info de um concurso, pelo seu ID
        dto.Contest contest = new dto.Contest();
        entity.Contest contestEnt= contestDao.find(id);

        if(contestEnt!= null){
            contest = convertContestEntToDto(contestEnt);
        }
        return contest;
    }

    public boolean verifyPermissionToModifyContest(int id) {
        // verifica se status do concurso é planning, pois só poderá ser editado / apagado neste caso
        boolean res=false;

        entity.Contest contest = contestDao.find(id);

        if(contest!=null){
            if(contest.getStatus() == StatusContest.PLANNING){
                res=true;
                // concurso pode ser editado/ apagado
            }
        }

        return res;
    }

    public dto.Contest editContestInfo(String token, dto.Contest editContest) {
        // edita as informações do concurso e retorna DTO para actualizar no frontend

        dto.Contest contestDto = new dto.Contest();

        entity.Contest contestEnt = contestDao.find(editContest.getId());

        if(contestEnt!=null){
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
        }


        return contestDto;
    }

    public dto.Contest editContestStatus(String token, int contestId, int status) {
        // edita o status do concurso: n permite voltar a status Planning
        boolean res=false;
        dto.Contest contestDto =null;
                entity.Contest contest = contestDao.find(contestId);

        if(contest!=null) {

            switch (status) {
                case 1:
                    // mudar para open: data de openCall tem de ser igual ou anterior a date.now()

                    if (validateDate(contest.getStartOpenCall())) {
                        contest.setStatus(StatusContest.OPEN);
                        contestDao.merge(contest);
                        contestDto = convertContestEntToDto(contest);
                       communicationBean.notifyAllUsers(contest);
                        res = true;
                    }

                    break;
                case 2:
                    // mudar para ongoing: data de startDate tem de ser igual ou anterior a date.now()
                    if (validateDate(contest.getStartDate())) {
                        contest.setStatus(StatusContest.ONGOING);
                        contestDao.merge(contest);
                        contestDto = convertContestEntToDto(contest);
                        communicationBean.notifyProjectMembersExecutionHasStarted(contest);
                        refuseUnansweredApplications(contest);

                        res = true;
                    }
                    break;
                case 3:
                    // mudar para concluded: data de finishDate tem de ser igual ou anterior a date.now()
                    // TODO será triggered automaticamente com escolha de vencedor
                    if (validateDate(contest.getFinishDate()) && checkWinner(contest)) {
                        contest.setStatus(StatusContest.CONCLUDED);
                        contestDao.merge(contest);
                        contestDto = convertContestEntToDto(contest);
                        communicationBean.notifyContestHasFinished(contest);
                        res = true;
                    }
                    break;
            }



        }
        return contestDto;
    }

    private boolean checkWinner(entity.Contest contest) {
        // verifica se concurso tem projecto vencedor atribuido
        boolean res = false;

        if (contest.getWinner()!=null){
            System.out.println(contest.getWinner());
            res=true;
        }

        return res;
    }

    private boolean validateDate(Date date) {
        // valida se data é igual ou anterior a today
        boolean res=false;

        Date today = Date.from(Instant.now());

        if(date.equals(today) || date.before(today)){
            res=true;
            System.out.println("Data ok para mudar status");
        }
        return res;
    }

    public boolean verifyPermissionToApply(int contestId) {
        // verifica se status do concurso é OPEN pois só pode receber / responder a candidaturas neste caso

        boolean res=false;

        entity.Contest contest = contestDao.find(contestId);

        if (contest!=null){
            if(contest.getStatus()==StatusContest.OPEN){
                res=true;
                // pode receber candidaturas
            }
        }

        System.out.println("result verify contest is open: " + res);
        return res;
    }

    public boolean applyToContest(int contestId, String token) {
        // projecto activo do token concorre a concurso
        boolean res= false;


        entity.Contest contest = contestDao.find(contestId);

        if(contest!=null){
        entity.User user = tokenDao.findUserEntByToken(token);

        if(user!=null){
            entity.Project project = projMemberDao.findActiveProjectByUserId(user.getUserId());

            if(project!=null) {

                // verifica se projecto tem relação com contest: se for aceite não faz nada, se em espera não faz nada, se recusado tem de permitir actualizar a relação
                // projecto pode ter sido recusado, melhorar os dados e voltar a concorrer
                ContestApplication applicationEnt = applicationDao.findApplicationForGivenContestIdAndProjectId(contest.getId(), project.getId());

                if(applicationEnt!=null){
                    if(applicationEnt.isAccepted() || !applicationEnt.isAnswered()){
                        // candidatura está aceite ou à espera de resposta
                        res=true; // não precisa de fazer nada
                    } else {
                        // candidatura foi previamente recusada

                        applicationEnt.setAnswered(false);
                        applicationEnt.setAccepted(false);
                        applicationDao.merge(applicationEnt);
                        // alterar status de projecto para proposed
                        project.setStatus(StatusProject.PROPOSED);
                        projDao.merge(project);
                        communicationBean.notifyAllContestManagers(2, contest.getTitle());
                        res=true; // nova candidatura - precisa de actualizar os atributos que definem a relação concurso - projecto
                    }
                } else {
                    // não há relação, é a 1ª candidatura

                    ContestApplication application = new ContestApplication();
                    application.setContest(contest);
                    application.setProject(project);
                    application.setAnswered(false);
                    application.setAccepted(false);

                    applicationDao.persist(application);
                    communicationBean.notifyAllContestManagers(2, contest.getTitle());


// alterar status de projecto para proposed
                    //TODO registar no historico do projecto
                    project.setStatus(StatusProject.PROPOSED);
                    projDao.merge(project);
                    res=true;
                }
            }}}

        return res;
    }



    public List<Application> getAllApplications(String token, int contestId) {
        // get all projects that applied for given contest: approved, refused and waiting for response

        List<Application> list = new ArrayList<>();

        List<ContestApplication> applications = applicationDao.findApplicationsForGivenContestId(contestId);

        if(applications!=null){
            for (ContestApplication a : applications){
                list.add(convertApplicationToDto(a));
            }
        }

        return list;
    }

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

    public boolean replyToApplication(String token, int applicationId, int answer) {
        // Perfil A responde a candidatura de projecto aceitando (answer = 1) ou rejeitando (answer =0)
        System.out.println("metodo " + applicationId);
boolean res=false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if(user!=null){

        ContestApplication applicationEnt = applicationDao.find(applicationId);

        if(applicationEnt!=null){
            System.out.println("encontrou applicaion");
            if (answer==1){
                // TODO verificar se entretanto foi cancelado ?! pode ser aprovado e isso dar ânimo a membros de projecto e o re-activem ?
                applicationEnt.setAnswered(true);
                applicationEnt.setAccepted(true);
                applicationDao.merge(applicationEnt);
                // Alterar status do projecto para approved
                applicationEnt.getProject().setStatus(StatusProject.APPROVED);
                projDao.merge(applicationEnt.getProject());
                System.out.println("record application call");

                communicationBean.recordProjectApplicationResult(user, applicationEnt.getProject(), answer);
                        //TODO testar notificacoes

                // sempre que aceita algum projecto tem de verificar se limite de projectos a concurso foi atingido. Se for, terá de automaticamente recusar os restantes projectos
                verifyLimitApplicationsToContestHasBeanReached(applicationEnt.getContest());
                res=true;
            } else if (answer==0){
                applicationEnt.setAnswered(true);
                applicationEnt.setAccepted(false);
                applicationDao.merge(applicationEnt);

                applicationEnt.getProject().setStatus(StatusProject.READY);
                projDao.merge(applicationEnt.getProject());
                res=true;
                System.out.println("record application call");

                communicationBean.recordProjectApplicationResult(user, applicationEnt.getProject(), answer);

            }
            communicationBean.notifyProjectMembersOfApplicationResponse(applicationEnt.getProject(), answer);
        }}

return res;
    }

    private void verifyLimitApplicationsToContestHasBeanReached(entity.Contest contest) {
        // verifica se limite de projectos aceites a um concurso foi atingido. Se sim, terá de recusar todos os projectos à espera de resposta
        System.out.println("verifica limite vagas atingido");

        boolean res = checkApplicationsLimit(contest.getId());

        if(res){
            refuseUnansweredApplications(contest);

        }

    }

    private void refuseUnansweredApplications(entity.Contest contest){
        List<ContestApplication> applicationsWaitingForResponse = applicationDao.findApplicationsNotAnsweredForGivenContestId(contest.getId());

        if(applicationsWaitingForResponse!=null){
            for (ContestApplication a : applicationsWaitingForResponse){
                a.setAnswered(true);
                a.setAccepted(false);
                applicationDao.merge(a);
                //TODO Registar no historico do proj


            }
        }
    }



    public boolean checkApplicationsLimit(int contestId) {
        // verifica se concurso já atingiu limite de projectos que pode aceitar
boolean res=false;

        entity.Contest contest = contestDao.find(contestId);
if (contest!=null){
        List<ContestApplication> list = applicationDao.findAcceptedApplicationsForGivenContestId(contestId);

        if(list!=null) {
            if(list.size()==contest.getMaxNumberProjects()){
                res=true;
                // limite foi atingido, não poderá aceitar mais projectos a concurso
    }}}
    return res;}

    public boolean newDatesAreWithinContestPeriod(entity.Task taskEnt, Task editTask) {
        // verifica se datas de uma tarefa editada estão dentro do periodo de execução do concurso
        // startDate da task igual ou after startDate do concurso
        // finishDate da task igual ou anterior a finishDate do concurso
boolean res=false;
        ContestApplication acceptedApplication = applicationDao.findAcceptedApplicationForGivenProjectId(taskEnt.getProject().getId());
        // representa a candidatura aceita do projecto da tarefa.

        if(acceptedApplication!=null){
            entity.Contest contest = acceptedApplication.getContest();

            if((editTask.getStartDate().equals(contest.getStartDate()) || editTask.getStartDate().after(contest.getStartDate()) ) && (editTask.getFinishDate().equals(contest.getFinishDate()) || editTask.getFinishDate().before(contest.getFinishDate()) )){
                res=true;
            }

        }


return res;
    }
}
