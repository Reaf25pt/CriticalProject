package service;

import bean.User;
import dto.*;
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
        }  else if (!userBean.checkUserPermission(token) || !projBean.verifyIfUserHasActiveProject(token)) {
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
        }  else if (!userBean.checkUserPermission(token) || !projBean.verifyIfProjectHasAvailableSpots(projId) || !projBean.verifyPermissionToAddMember(token, projId, userId)) {
            //TODO falta testar verify permission
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

    // GET LIST OF KEYWORDS TO SUGGEST
    @GET
    @Path("/keywords")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeywords(@QueryParam("title") String title,  @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Keyword> keywords = projBean.getKeywordsList(title);

            if (keywords == null || keywords.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(keywords).build();
            }
        }

        return r;
    }


    // ADICIONAR TAREFA A PROJECTO
    @POST
    @Path("/{projId}/task")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTask(@PathParam("projId") int projId, Task task, @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token) || task== null || projBean.checkTaskInfo(task)) {
            r = Response.status(401).entity("Unauthorized!").build();
        }  else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId) || projBean.verifyProjectStatusToChangeTask(projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.addTaskToProject(projId, task, token);

            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity("Success!").build();
            }
        }
        return r;}

    // GET LIST OF TASKS OF GIVEN PROJECT
    @GET
    @Path("/tasks/{projId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasksofProject(@HeaderParam("token") String token, @PathParam("projId") int projId) {

        Response r = null;

        // TODO verificar permissão. se user tem acesso a tarefas?! só acede a tab no frontend se for membro do projecto

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Task> tasks = projBean.getTasksList(projId);

            if (tasks == null || tasks.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(tasks).build();
            }
        }

        return r;
    }

    // GET LIST OF SKILLS TO SUGGEST TO PROJECT
    @GET
    @Path("/skills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills(@QueryParam("title") String title,   @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Skill> skills = projBean.getSkillsList(title);

            if (skills == null || skills.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(skills).build();
            }
        }

        return r;
    }



    // EDIT PROJECT INFO
    @PATCH
    @Path("/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProject(@HeaderParam("token") String token, dto.Project editProj) {

        Response r = null;

        if (userBean.checkStringInfo(token) || editProj==null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, editProj.getId())) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            boolean res = projBean.editProjectInfo(token, editProj);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                r = Response.status(200).entity("Success").build();
            }
        }
        return r;

    }

    // GET LIST OF USERS TO SUGGEST TO PROJECT
    @GET
    @Path("/possiblemembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPossibleMembers(@QueryParam("name") String name,   @HeaderParam("token") String token,   @HeaderParam("projId") int projId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<UserInfo> list = projBean.getPossibleMembers(name);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }

    // DELETE MEMBER FROM PROJECT - na verdade não remove da DB, apenas actualiza info do atributo removed do projMember
    @PATCH
    @Path("/member")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMember(@HeaderParam("token") String token, @HeaderParam("userId") int userId,@HeaderParam("projId") int projId ) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.verifyPermissionToDeleteUser(token, projId, userId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.deleteProjMember(userId, projId, token);

            if (!res) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity("Success").build();
            }
        }

        return r;
    }

     // ALTERA PAPEL DE MEMBRO DE UM PROJECTO: GESTOR OU PARTICIPANTE
    @PUT
    @Path("/member")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeMemberRole(@HeaderParam("token") String token, @HeaderParam("userId") int userId,@HeaderParam("projId") int projId, @HeaderParam("role") int role ) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.changeMemberRole(userId, projId, token, role);

            if (!res) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity("Success").build();
            }
        }

        return r;
    }

    // EDIT PROJECT STATUS
    @PUT
    @Path("/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProjectStatus(@HeaderParam("token") String token, @HeaderParam("status") int status, @HeaderParam("projId") int projId) {
// TODO pode ser necessário alterar info que vem do frontend
        Response r = null;

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            // TODO terminar de implementar
            boolean res = projBean.editProjectStatus(token, projId, status);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                r = Response.status(200).entity("Success").build();
            }
        }
        return r;

    }


}


