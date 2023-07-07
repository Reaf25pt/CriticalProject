package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dto.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Includes all endpoints that manage contest data: add and edit contests, manage applications, choose project winner and obtain contest statistics
 */
@Path("/contest")
public class Contest {
    @Inject
    bean.Project projBean;
    @Inject
    bean.User userBean;
    @Inject
    bean.Contest contestBean;


    // CRIAR NOVO CONCURSO
    @POST
    @Path("/newcontest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addContest(dto.Contest contest, @HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token) || contest == null) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) || !contestBean.verifyPermissionToAddNewContest()) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = contestBean.createNewContest(contest, token);

            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity("Success!").build();
            }
        }
        return r;
    }


    // EDIT CONTEST INFO
    @PATCH
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editContest(@HeaderParam("token") String token, dto.Contest editContest) {

        Response r = null;

        if (userBean.checkStringInfo(token) || editContest == null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) || !contestBean.verifyPermissionToModifyContest(editContest.getId())) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {

            userBean.updateSessionTime(token);

            dto.Contest contest = contestBean.editContestInfo(token, editContest);

            if (contest == null) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                r = Response.status(200).entity(contest).build();
            }
        }
        return r;

    }

    // EDIT CONTEST STATUS
    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editContestStatus(@HeaderParam("token") String token, @HeaderParam("status") int status, @HeaderParam("contestId") int contestId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            dto.Contest contest = contestBean.editContestStatus(token, contestId, status);

            if (contest == null) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                r = Response.status(200).entity(contest).build();
            }
        }
        return r;

    }


    // PROJECTO CONCORRE A CONCURSO - projecto activo do utilizador logado
    @POST
    @Path("/application")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyToContest(@HeaderParam("contestId") int contestId, @HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyPermissionToApply(contestId) || !projBean.verifyProjectCanApply(token, contestId)) {
            // TODO verificar se nenhuma tarefa do plano de execução tem datas q saiam do timing do concurso
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = contestBean.applyToContest(contestId, token);

            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity("Success!").build();
            }
        }
        return r;
    }


    // RESPONSE TO PROJECT APPLICATION : accept - 1 ; refuse - 0
    @PATCH
    @Path("/application")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyToApplication(@HeaderParam("token") String token, @HeaderParam("answer") int answer, @HeaderParam("applicationId") int applicationId, @HeaderParam("contestId") int contestId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) || !contestBean.verifyPermissionToApply(contestId) || contestBean.checkApplicationsLimit(contestId)) {
            r = Response.status(403).entity("Forbidden!").build();
// TODO verificar se projecto está cancelado? entretanto pode ter mudado de status
        } else {

            userBean.updateSessionTime(token);
            boolean res = contestBean.replyToApplication(token, applicationId, answer);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                List<Application> list = contestBean.getAllApplications(token, contestId);
                r = Response.status(200).entity(list).build();
            }
        }
        return r;

    }

    // SELECÇÃO DO PROJECTO VENCEDOR
    @PUT
    @Path("/application")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response chooseWinner(@HeaderParam("contestId") int contestId, @HeaderParam("projId") int projId, @HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) || !contestBean.verifyPermissionToChooseWinner(contestId) || !contestBean.verifyProjectIsFinished(projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);
            boolean res = contestBean.chooseContestWinner(contestId, projId, token);
            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {
                dto.Contest contest = contestBean.editContestStatus(token, contestId, 3); // para terminar o concurso
                r = Response.status(200).entity(contest).build();
            }
        }
        return r;
    }

    // GET LIST OF ALL CONTESTS IN DB
    @GET
    @Path("/allcontests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllContests(@HeaderParam("token") String token, @QueryParam("title") String title, @QueryParam("startDate") String startDate, @QueryParam("finishDate") String finishDate) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);
            List<dto.Contest> list = new ArrayList<>();
            if(!userBean.checkStringInfo(title)){
                list = contestBean.filterContestsByName(token, title);
            } else if(!userBean.checkStringInfo(startDate)) {
                list=contestBean.filterContestsByStartDate(startDate);
            } else if(!userBean.checkStringInfo(finishDate)) {
                list=contestBean.filterContestsByFinishDate(finishDate);
            }else{
               list =  contestBean.getAllContests(token);
            }

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }

    // GET LIST OF ACTIVE CONTESTS IN DB
    @GET
    @Path("/activecontests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveContests(@HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.Contest> list = contestBean.getActiveContests(token);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }

    // GET CONTEST BY CONTEST ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContest(@HeaderParam("token") String token, @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            dto.Contest contest = contestBean.getContest(token, id);

            if (contest == null) {
                r = Response.status(404).entity(contest).build();
            } else {

                r = Response.status(200).entity(contest).build();
            }
        }

        return r;
    }

    // GET LIST OF ALL PROJECTS ASSOCIATED WITH GIVEN CONTEST
    @GET
    @Path("/projects/{contestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects(@HeaderParam("token") String token, @PathParam("contestId") int contestId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Application> list = contestBean.getAllApplications(token, contestId);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }


    @GET
    @Path("stats/{contestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response statsContests(@PathParam("contestId") int contestId, @HeaderParam("token") String token) throws JsonProcessingException {
        Response r = null;

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) ) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            String list = contestBean.statsContenst(contestId);
            if (userBean.checkStringInfo(list)) {
                r = Response.status(404).entity("Something went wrong").build();
            } else {
            r = Response.status(200).entity(list).build();

        }}
        return r;
    }
}
