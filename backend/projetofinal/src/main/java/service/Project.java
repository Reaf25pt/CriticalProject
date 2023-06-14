package service;

import bean.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/project")
public class Project {

    @Inject
    bean.Project projBean;
    @Inject
    User userBean;

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getALl(@DefaultValue("ola") @QueryParam("statecode") String statecode) {
        System.out.println(statecode);
        Response r = null;

        // TODO validar token na mesma? validar que queries estão preenchidos no caso de haver multiplos queries?


return r;
    }


    // CRIAR NOVO PROJECTO
    @POST
    @Path("/newproject")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProject(dto.Project project, @HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token) || project==null) {
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

        if (userBean.checkStringInfo(token) ) {
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

    // GET LIST OF ALL PROJECTS IN DB
    @GET
    @Path("/allprojects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects(@HeaderParam("token") String token) {

        // verificar se token tem sessão iniciada e válida, se sim actualizar session time
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.Project> projects = projBean.getAllProjectsList(token);

            if (projects == null || projects.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(projects).build();
            }
        }

        return r;
    }

    // GET PROJECT BY PROJECT ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects(@HeaderParam("token") String token,  @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            dto.Project project = projBean.getProject(token, id);

            if (project == null ) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(project).build();
            }
        }

        return r;
    }

// GET LISTA DE MEMBROS ACTIVOS DE UM PROJECTO PELO PROJECT ID
    @GET
    @Path("/{id}/members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectMembers(@HeaderParam("token") String token,  @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            // TODO impedir que user n é membro do projecto ?!
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.ProjectMember> projMembers = projBean.getProjectMembers(id);

            if (projMembers == null || projMembers.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(projMembers).build();
            }
        }

        return r;
    }



}


