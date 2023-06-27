package service;
import dto.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


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

        if (userBean.checkStringInfo(token) || contest==null) {
            r = Response.status(401).entity("Unauthorized!").build();
        }  else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token)) {
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
        return r;}


    // EDIT CONTEST INFO
    @PATCH
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editContest(@HeaderParam("token") String token, dto.Contest editContest) {

        Response r = null;

        if (userBean.checkStringInfo(token) || editContest==null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) || !contestBean.verifyPermissionToModifyContest(editContest.getId())) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {

            userBean.updateSessionTime(token);

            dto.Contest contest = contestBean.editContestInfo(token, editContest);

            if (contest==null) {
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

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) ) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            dto.Contest contest = contestBean.editContestStatus(token, contestId, status);

            if (contest==null) {
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

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();
        }  else if (!userBean.checkUserPermission(token) || !contestBean.verifyPermissionToApply( contestId)|| !projBean.verifyProjectCanApply(token, contestId)) {
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
        return r;}


    // RESPONSE TO PROJECT APPLICATION : accept - 1 ; refuse - 0
    @PATCH
    @Path("/application")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyToApplication(@HeaderParam("token") String token, @HeaderParam("status") int status, @HeaderParam("applicationId") int applicationId, @HeaderParam("contestId") int contestId) {

        Response r = null;

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !contestBean.verifyUserProfile(token) || !contestBean.verifyPermissionToApply( contestId)|| contestBean.checkApplicationsLimit(contestId) ) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);
            System.out.println("endpoint");
            boolean res = contestBean.replyToApplication(token, applicationId, status);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                r = Response.status(200).entity("Success").build();
            }
        }
        return r;

    }

    // SELECÇÃO DO PROJECTO VENCEDOR
    @PUT
    @Path("/application")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response chooseWinner(@HeaderParam("contestId") int contestId,@HeaderParam("projId") int projId, @HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();
        }  else if (!userBean.checkUserPermission(token) || !contestBean.verifyPermissionToApply( contestId)|| !projBean.verifyProjectCanApply(token, contestId)) {
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
        return r;}

    // GET LIST OF ALL CONTESTS IN DB
    @GET
    @Path("/allcontests")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllContests(@HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.Contest> list = contestBean.getAllContests(token);

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
    public Response getContest(@HeaderParam("token") String token,  @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            dto.Contest contest = contestBean.getContest(token, id);

            if (contest == null ) {
                r = Response.status(404).entity("Not found").build();
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

}
