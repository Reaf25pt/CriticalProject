package bean;

import ENUM.StatusContest;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import org.jboss.logging.Logger;

import java.util.ArrayList;
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
            // TODO CONFIRMAR Q n actualiza status nem vencedor porque não será  por aqui

            contestDao.merge(contestEnt);
            contestDto = convertContestEntToDto(contestEnt);
        }


        return contestDto;
    }
}
