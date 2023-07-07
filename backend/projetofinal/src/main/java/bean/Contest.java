package bean;

import ENUM.StatusContest;
import ENUM.StatusProject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Application;
import dto.Task;
import entity.ContestApplication;
import entity.Project;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

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
    @EJB
    dao.Task taskDao;

    public Contest() {
    }


    public boolean createNewContest(dto.Contest contestInfo, String token) {
        // datas encadeadas são verificadas no frontend
        // verificar se datas de openCall não sobrepõem as de outros concursos para garantir que só há 1 openCall de cada x
        boolean res = false;

        if (contestInfo != null) {

            if(!verifyOpenCallDates(contestInfo.getStartOpenCall(),contestInfo.getFinishOpenCall() )){

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
            res = true;
        }}
        return res;
    }

    private boolean verifyOpenCallDates(Date startOpenCall, Date finishOpenCall) {
        // verifica se datas se sobrepõe a alguma open call de outro concurso
        // Confirmar que startOpenCall é posterior a finishOpenCall de concursos já existentes
        // Confirmar que finishOpenCall é anterior a startOpenCall de concursos já existentes
        boolean res=false;
        int count =0;  // contador para número de ocorrências que não permitem validar as datas

        List<entity.Contest> listAllContests = contestDao.findAll();
        for(entity.Contest c : listAllContests){
            if (!startOpenCall.after(c.getFinishOpenCall()) || !finishOpenCall.before(c.getStartOpenCall())){
                count++;
            }
        }

        if(count!=0){
            res=true; // datas não permitem que novo concurso seja criado
        }

        return res;
    }


    public boolean verifyUserProfile(String token) {
        // verifica se user profile é do tipo PERFIL A - gestor de concursos
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {
            if (user.isContestManager()) {
                res = true;
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

    public List<dto.Contest> getActiveContests(String token) {
        // para apresentar na página inicial da app
        List<dto.Contest> list = new ArrayList<>();

        List<entity.Contest> tempList = contestDao.findActiveContests();

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

        if (c.getWinner() != null) {
            contestDto.setWinnerProjectId(c.getWinner().getId());
        }
        return contestDto;
    }

    public dto.Contest getContest(String token, int id) {
        //obter info de um concurso, pelo seu ID
        dto.Contest contest = new dto.Contest();
        entity.Contest contestEnt = contestDao.find(id);

        if (contestEnt != null) {
            contest = convertContestEntToDto(contestEnt);
        }
        return contest;
    }

    public boolean verifyPermissionToModifyContest(int id) {
        // verifica se status do concurso é planning, pois só poderá ser editado / apagado neste caso
        boolean res = false;

        entity.Contest contest = contestDao.find(id);

        if (contest != null) {
            if (contest.getStatus() == StatusContest.PLANNING) {
                res = true;
                // concurso pode ser editado/ apagado
            }
        }

        return res;
    }

    public dto.Contest editContestInfo(String token, dto.Contest editContest) {
        // edita as informações do concurso e retorna DTO para actualizar no frontend
        // verificar se datas de OpenCall batem certo
        dto.Contest contestDto = new dto.Contest();

        entity.Contest contestEnt = contestDao.find(editContest.getId());

        if (contestEnt != null) {
            if(!verifyOpenCallDates(editContest.getStartOpenCall(),editContest.getFinishOpenCall() )){

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
        }}


        return contestDto;
    }

    public dto.Contest editContestStatus(String token, int contestId, int status) {
        // edita o status do concurso: n permite voltar a status Planning
        boolean res = false;
        dto.Contest contestDto = null;
        entity.Contest contest = contestDao.find(contestId);

        if (contest != null) {

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

        if (contest.getWinner() != null) {
            System.out.println(contest.getWinner());
            res = true;
        }

        return res;
    }

    private boolean validateDate(Date date) {
        // valida se data é igual ou anterior a today
        boolean res = false;

        Date today = Date.from(Instant.now());

        if (date.equals(today) || date.before(today)) {
            res = true;
            System.out.println("Data ok para mudar status");
        }
        return res;
    }

    public boolean verifyPermissionToApply(int contestId) {
        // verifica se status do concurso é OPEN pois só pode receber / responder a candidaturas neste caso

        boolean res = false;

        entity.Contest contest = contestDao.find(contestId);

        if (contest != null) {
            if (contest.getStatus() == StatusContest.OPEN) {
                res = true;
                // pode receber candidaturas
            }
        }

        System.out.println("result verify contest is open: " + res);
        return res;
    }

    public boolean applyToContest(int contestId, String token) {
        // projecto activo do token concorre a concurso
        boolean res = false;


        entity.Contest contest = contestDao.find(contestId);

        if (contest != null) {
            entity.User user = tokenDao.findUserEntByToken(token);

            if (user != null) {
                entity.Project project = projMemberDao.findActiveProjectByUserId(user.getUserId());

                if (project != null) {

                    // verifica se projecto tem relação com contest: se for aceite não faz nada, se em espera não faz nada, se recusado tem de permitir actualizar a relação
                    // projecto pode ter sido recusado, melhorar os dados e voltar a concorrer
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
                            // alterar status de projecto para proposed
                            project.setStatus(StatusProject.PROPOSED);
                            projDao.merge(project);
                            communicationBean.notifyAllContestManagers(2, contest.getTitle());
                            res = true; // nova candidatura - precisa de actualizar os atributos que definem a relação concurso - projecto
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
                        res = true;
                    }
                }
            }
        }

        return res;
    }


    public List<Application> getAllApplications(String token, int contestId) {
        // get all projects that applied for given contest: approved, refused and waiting for response

        List<Application> list = new ArrayList<>();

        List<ContestApplication> applications = applicationDao.findApplicationsForGivenContestId(contestId);

        if (applications != null) {
            for (ContestApplication a : applications) {
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
        boolean res = false;

        entity.User user = tokenDao.findUserEntByToken(token);
        if (user != null) {

            ContestApplication applicationEnt = applicationDao.find(applicationId);

            if (applicationEnt != null) {
                System.out.println("encontrou applicaion");
                if (answer == 1) {
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
                    res = true;
                } else if (answer == 0) {
                    applicationEnt.setAnswered(true);
                    applicationEnt.setAccepted(false);
                    applicationDao.merge(applicationEnt);

                    applicationEnt.getProject().setStatus(StatusProject.READY);
                    projDao.merge(applicationEnt.getProject());
                    res = true;
                    System.out.println("record application call");

                    communicationBean.recordProjectApplicationResult(user, applicationEnt.getProject(), answer);

                }
                communicationBean.notifyProjectMembersOfApplicationResponse(applicationEnt.getProject(), answer);
            }
        }

        return res;
    }

    private void verifyLimitApplicationsToContestHasBeanReached(entity.Contest contest) {
        // verifica se limite de projectos aceites a um concurso foi atingido. Se sim, terá de recusar todos os projectos à espera de resposta
        System.out.println("verifica limite vagas atingido");

        boolean res = checkApplicationsLimit(contest.getId());

        if (res) {
            refuseUnansweredApplications(contest);

        }

    }

    private void refuseUnansweredApplications(entity.Contest contest) {
        List<ContestApplication> applicationsWaitingForResponse = applicationDao.findApplicationsNotAnsweredForGivenContestId(contest.getId());

        if (applicationsWaitingForResponse != null) {
            for (ContestApplication a : applicationsWaitingForResponse) {
                a.setAnswered(true);
                a.setAccepted(false);
                applicationDao.merge(a);
                //TODO Registar no historico do proj


            }
        }
    }


    public boolean checkApplicationsLimit(int contestId) {
        // verifica se concurso já atingiu limite de projectos que pode aceitar
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

    public boolean newDatesAreWithinContestPeriod(entity.Task taskEnt, Task editTask) {
        // verifica se datas de uma tarefa editada estão dentro do periodo de execução do concurso
        // startDate da task igual ou after startDate do concurso
        // finishDate da task igual ou anterior a finishDate do concurso
        boolean res = false;
        ContestApplication acceptedApplication = applicationDao.findAcceptedApplicationForGivenProjectId(taskEnt.getProject().getId());
        // representa a candidatura aceita do projecto da tarefa.

        if (acceptedApplication != null) {
            entity.Contest contest = acceptedApplication.getContest();

            if ((editTask.getStartDate().equals(contest.getStartDate()) || editTask.getStartDate().after(contest.getStartDate())) && (editTask.getFinishDate().equals(contest.getFinishDate()) || editTask.getFinishDate().before(contest.getFinishDate()))) {
                res = true;
            }

        }


        return res;
    }


    public boolean verifyPermissionToChooseWinner(int contestId) {


        // verifica se data .now() é posterior a data de task final de todos os projectos aceites
        // poderia verificar se todos os prjectos estão finished e task final finished mas isso poderia significar que o concurso nunca poderia ficar concluído por algum gestor de projecto não marcar o projecto / task final como finished
        // verifica tb se concurso status é ongoing
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

    private boolean checkProjectsAcceptedFinalTaskDates(int contestId) {
        // verifica se data .now() é posterior a data de task final de todos os projectos aceites
        // poderia verificar se todos os prjectos estão finished e task final finished mas isso poderia significar que o concurso nunca poderia ficar concluído por algum gestor de projecto não marcar o projecto / task final como finished
        boolean res = false;

        List<Project> projectsAccepted = applicationDao.findAcceptedProjectsForGivenContestId(contestId);
        if (projectsAccepted != null) {
            // verificar se data de final task é anterior a data.now(). Dá-se hipótese a todos os projectos para cumprirem o seu calendário
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

    public boolean verifyProjectIsFinished(int projId) {
        // verifica se status do projecto (a declarar vencedor) é finished. Só estes projectos poderão ser declarados vencedores
        boolean res = false;
        Project project = projDao.findProjectById(projId);

        if (project != null) {
            if (project.getStatus() == StatusProject.FINISHED) {
                res = true;
            }
        }
        return res;
    }

    public boolean chooseContestWinner(int contestId, int projId, String token) {
        // declara o projecto vencedor de um dado concurso e automaticamente termina o concurso
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

                }
            }
        }
        return res;
    }


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

        stats.put("averages",averages);

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

    public String averageElementsProject(int contestId) {

        List<ContestApplication> projectsGiven = applicationDao.findApplicationsForGivenContestId(contestId);
        double sizeProjects = applicationDao.findApplicationsForGivenContestId(contestId).size();
        double countmembers = 0;

        for (ContestApplication contestApplication : projectsGiven) {
            countmembers += contestApplication.getProject().getMembersNumber();

        }
        return String.format("%.2f", countmembers / sizeProjects);

    }


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

        String result = String.format("%.2f",days/sizeProjects);


        return result;
    }


    public boolean verifyPermissionToAddNewContest() {
        // só pode haver 1 concurso PLANNING e 1 concurso OPEN at all times

        boolean res=false;

Long count = contestDao.countPlanningContest();
if (count==0 ){
    System.out.println("count planning contest " + count );
    res=true; // pode criar novo concurso
}

        return res;
    }

    public List<dto.Contest> filterContestsByName(String token, String str) {
        // filtrar contests que tenham nome que faça match com str
        List < dto.Contest> list = new ArrayList<>();
       // Set<entity.Contest> mergeSet = new HashSet<>();
        List <entity.Contest> contestList = contestDao.findContestListContainingStr(str.toLowerCase());

       if(contestList!=null){
           for (entity.Contest c : contestList){
               list.add(convertContestEntToDto(c));
           }
       }
return list;
    }

    public List<dto.Contest> filterContestsByStartDate(String startDate) {
        // filters contests whose startDate is equal or after startDate inserted - considers starOpenCall
        List<dto.Contest> list = new ArrayList<>();
        long timestamp=Long.parseLong(startDate);
        Date date = new Date(timestamp);
        List<entity.Contest> contestList = contestDao.findContestListWhoseStartOpenCallDateEqualOrAfterGivenDate(date);
        if(contestList!=null){
            for (entity.Contest c : contestList){
                list.add(convertContestEntToDto(c));
            }
        }
        return list;
    }

    public List<dto.Contest> filterContestsByFinishDate(String finishDate) {
        // filter contests whose finishDate is equal or before finishDate inserted - considers finishDate of contest

        List<dto.Contest> list = new ArrayList<>();
        long timestamp=Long.parseLong(finishDate);
        Date date = new Date(timestamp);
        List<entity.Contest> contestList = contestDao.findContestListWhoseFinishDateEqualOrBeforeGivenDate(date);
        if(contestList!=null){
            for (entity.Contest c : contestList){
                list.add(convertContestEntToDto(c));
            }
        }
        return list;
    }
}










