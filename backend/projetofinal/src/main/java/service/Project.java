package service;

import bean.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/project")
public class Project {

    @Inject
    bean.Project projBean;
    @Inject
    User userBean;


    // CRIAR NOVO PROJECTO
    @POST
    @Path("/newproject")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProject(dto.Project project, @HeaderParam("token") String token) {
        Response r = null;

        if (token == null || token.isBlank() || token.isEmpty() || project==null) {
            r = Response.status(401).entity("Unauthorized!").build();
        }  else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.createNewProject(project, token);

            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity("Success!").build();
            }
        }
        return r;}


    // ADICIONAR MEMBRO A PROJECTO
    @POST
    @Path("/newmember")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMember(@HeaderParam("projId") int projId, @HeaderParam("userId") int userId, @HeaderParam("token") String token) {
        // TODO send id de proj e user ou objecto com + info?? SE for ID, como verificar se a info vem nula?
        Response r = null;

        if (token == null || token.isBlank() || token.isEmpty() ) {
            r = Response.status(401).entity("Unauthorized!").build();
        }  else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.addMemberToProject(projId, userId, token);

            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity("Success!").build();
            }
        }
        return r;}

}


