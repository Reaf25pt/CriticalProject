package service;
import dto.*;
import dto.Project;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
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

}
