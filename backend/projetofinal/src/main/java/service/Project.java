package service;

import bean.User;
import dto.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Includes all endpoints that manage project data, including its tasks: add, edit and delete when appropriate projects, tasks and project members and manage project skills and keywords. Includes project chat
 */
@Path("/project")
public class Project {

    @Inject
    bean.Project projBean;
    @Inject
    User userBean;

    // GET ALL PROJECTS OR SOME BASED ON FILTER INPUT INSERTED IN FRONTEND
    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getALl(/*@DefaultValue("ola")*/ @QueryParam("queryWinner") boolean queryWinner, @QueryParam("global") String global,  @HeaderParam("token") String token) {


        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);
            List<dto.Project> projects = new ArrayList<>();
            if (queryWinner) {
                projects = projBean.filterWinnerProjects();
            } else if (!userBean.checkStringInfo(global)) {
                projects = projBean.filterProjectsByNameSkillsAndKeywords( global);
            }  else {

                projects = projBean.getAllProjectsList();
            }
            if (projects == null || projects.size() == 0) {
                r = Response.status(404).entity(projects).build();
            } else {

                r = Response.status(200).entity(projects).build();
            }
        }

        return r;
    }


    // CRIAR NOVO PROJECTO
    @POST
    @Path("/newproject")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProject(dto.Project project, @HeaderParam("token") String token) {
        Response r = null;
        if (userBean.checkStringInfo(token) || project == null) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.verifyIfUserHasActiveProject(token)) {
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
        return r;
    }

    // EDIT PROJECT INFO - refers to details and not execution plan
    @PATCH
    @Path("/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProject(@HeaderParam("token") String token, dto.Project editProj) {

        Response r = null;

        if (userBean.checkStringInfo(token) || editProj == null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, editProj.getId()) || projBean.verifyPermisionToEditProjectInfo(editProj.getId())) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {

            userBean.updateSessionTime(token);

            boolean res = projBean.editProjectInfo(token, editProj);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                dto.Project project = projBean.getProject(token, editProj.getId());
                r = Response.status(200).entity(project).build();
            }
        }
        return r;

    }


    // ADICIONAR MEMBRO A PROJECTO (convite por gestor do projecto ou self-invite)
    @POST
    @Path("/newmember")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMember(@HeaderParam("projId") int projId, @HeaderParam("userId") int userId, @HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || projBean.verifyProjectStatusToModifyTask(projId) || !projBean.verifyIfProjectHasAvailableSpots(projId) || !projBean.verifyPermissionToAddMember(token, projId, userId)) {

            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.addMemberToProject(projId, userId, token);

            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {
                List<dto.PotentialProjMember> list = projBean.getPotentialProjectMembers(projId);
                r = Response.status(200).entity(list).build();
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

        if (userBean.checkStringInfo(token) || task == null || projBean.checkTaskInfo(task)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId) || projBean.verifyProjectStatusToModifyTask(projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.addTaskDependingOnProjectStatus(projId, task, token);

            if (!res) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {
                List<Task> tasks = projBean.getTasksList(projId);
                r = Response.status(200).entity(tasks).build();
            }
        }
        return r;
    }


    // ADICIONAR PROJECT CHAT MESSAGE
    @POST
    @Path("/chat/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProjectChatMessage(@PathParam("id") int projId, ProjectChat message, @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token) || message == null) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjMember(projId, token) || projBean.verifyPermissionToChat(projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            ProjectChat newMessage = projBean.addMessageToProjectChat(projId, message, token);

            if (newMessage == null) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity(newMessage).build();
            }
        }
        return r;
    }

    // ADICIONAR REGISTO HISTÓRICO MANUAL
    @POST
    @Path("/{projId}/record")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addManualRecord(@PathParam("projId") int projId, ProjectHistory record, @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token) || record == null) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjMember(projId, token) || !projBean.verifyPermissionToAddManualRecord(projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            ProjectHistory newRecord = projBean.addManualRecord(projId, record, token);

            if (newRecord == null) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity(newRecord).build();
            }
        }
        return r;
    }


    // GESTOR DE PROJECTO RESPONDE A PEDIDO PARA PARTICIPAR NO PROJECTO - actualiza info da relação do projMember
    @PATCH
    @Path("/selfinvitation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyToSelfInvitation(@HeaderParam("token") String token, @HeaderParam("projMemberId") int projMemberId, @HeaderParam("projId") int projId, @HeaderParam("answer") int answer) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || projBean.verifyProjectStatusToModifyTask(projId) || !projBean.isProjManager(token, projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.replyToSelfInvitation(projMemberId, projId, token, answer);

            if (!res) {
                r = Response.status(404).entity("Not found").build();
            } else {
                List<dto.PotentialProjMember> list = projBean.getPotentialProjectMembers(projId);

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }


    // DELETE MEMBER FROM PROJECT - na verdade não remove da DB, apenas actualiza info do atributo removed do projMember que define a relação
    @PATCH
    @Path("/member")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMember(@HeaderParam("token") String token, @HeaderParam("userId") int userId, @HeaderParam("projId") int projId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || projBean.verifyProjectStatusToModifyTask(projId) || !projBean.verifyPermissionToDeleteUser(token, projId, userId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.deleteProjMember(userId, projId, token);

            if (!res) {
                r = Response.status(404).entity("Not found").build();
            } else {
                List<dto.ProjectMember> projMembers = projBean.getProjectMembers(projId);
                r = Response.status(200).entity(projMembers).build();
            }
        }

        return r;
    }

    // ALTERA PAPEL DE MEMBRO DE UM PROJECTO: GESTOR OU PARTICIPANTE
    @PUT
    @Path("/member")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeMemberRole(@HeaderParam("token") String token, @HeaderParam("userId") int userId, @HeaderParam("projId") int projId, @HeaderParam("role") int role) {

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
                List<dto.ProjectMember> projMembers = projBean.getProjectMembers(projId);
                r = Response.status(200).entity(projMembers).build();
            }
        }

        return r;
    }

    // EDIT PROJECT STATUS
    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editProjectStatus(@HeaderParam("token") String token, @HeaderParam("status") int status, @HeaderParam("projId") int projId, Task finalTask) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.editProjectStatus(token, projId, status, finalTask);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                dto.Project project = projBean.getProject(token, projId);
                r = Response.status(200).entity(project).build();
            }
        }
        return r;

    }


    //DELETE TASK FROM PROJECT only if task is not precedent of another task or has no prerequired tasks
    @DELETE
    @Path("/task")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTaskOfProject(@HeaderParam("token") String token, @HeaderParam("projId") int projId, @HeaderParam("taskId") int taskId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId) || !projBean.verifyIfTaskBelongsToProject(taskId, projId) || !projBean.verifyProjectIsPlanning(projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {

            userBean.updateSessionTime(token);

            boolean res = projBean.deleteTask(token, taskId);
            if (!res) {
                r = Response.status(404).entity("Not found!").build();
            } else {
                List<Task> tasks = projBean.getTasksList(projId);

                r = Response.status(200).entity(tasks).build();

            }
        }
        return r;

    }


    // EDIT TASK INFO
    @PATCH
    @Path("/{projId}/task")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editTask(@HeaderParam("token") String token, @PathParam("projId") int projId, dto.Task editTask) {

        Response r = null;

        if (userBean.checkStringInfo(token) || editTask == null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId) || !projBean.verifyIfTaskBelongsToProject(editTask.getId(), projId) || projBean.verifyProjectStatusToModifyTask(projId) || projBean.verifyTaskStatusToEditTask(editTask.getId())) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            boolean res = projBean.editTask(token, editTask);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                List<Task> tasks = projBean.getTasksList(projId);

                r = Response.status(200).entity(tasks).build();
            }
        }
        return r;

    }


    // EDIT TASK STATUS
    @PUT
    @Path("/{projId}/task")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editTaskStatus(@HeaderParam("token") String token, @PathParam("projId") int projId, Task editTask) {

        Response r = null;


        if (userBean.checkStringInfo(token) || editTask == null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token) || !projBean.verifyIfTaskBelongsToProject(editTask.getId(), projId) || !projBean.verifyPermissionToEditTaskStatus(token, editTask.getId()) || projBean.verifyProjectStatusToEditTaskStatus(projId)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            boolean res = projBean.validateEditTaskStatus(token, editTask);

            if (!res) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                List<Task> tasks = projBean.getTasksList(projId);
                r = Response.status(200).entity(tasks).build();
            }
        }
        return r;

    }


    // GET LIST OF TASKS OF GIVEN PROJECT
    @GET
    @Path("/tasks/{projId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasksofProject(@HeaderParam("token") String token, @PathParam("projId") int projId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Task> tasks = projBean.getTasksList(projId);

            if (tasks == null || tasks.size() == 0) {
                r = Response.status(404).entity(tasks).build();
            } else {

                r = Response.status(200).entity(tasks).build();
            }
        }

        return r;
    }


    // GET PROJECT BY PROJECT ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@HeaderParam("token") String token, @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            dto.Project project = projBean.getProject(token, id);

            if (project == null) {
                r = Response.status(404).entity(project).build();
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
    public Response getProjectMembers(@HeaderParam("token") String token, @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {

            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.ProjectMember> projMembers = projBean.getProjectMembers(id);

            if (projMembers == null || projMembers.size() == 0) {
                r = Response.status(404).entity(projMembers).build();
            } else {

                r = Response.status(200).entity(projMembers).build();
            }
        }

        return r;
    }

    // GET LISTA DE MEMBROS COM CONVITE PENDENTE DE UM PROJECTO PELO PROJECT ID
    @GET
    @Path("/{id}/potentialmembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPotentialProjectMembers(@HeaderParam("token") String token, @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.PotentialProjMember> list = projBean.getPotentialProjectMembers(id);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }


    // GET LISTA DE REGISTO DE ACTIVIDADES DE PROJECTO PELO SEU ID
    @GET
    @Path("/{id}/record")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectRecords(@HeaderParam("token") String token, @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjMember(id, token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.ProjectHistory> list = projBean.getProjectRecords(id);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }

    // GET LIST OF CHAT MESSAGES FROM GIVEN PROJECT ID
    @GET
    @Path("/chat/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectChatMessages(@HeaderParam("token") String token, @PathParam("id") int projId) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjMember(projId, token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<dto.ProjectChat> list = projBean.getProjectChatList(token, projId);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {
                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }


    // GET LIST OF SKILLS TO SUGGEST TO PROJECT
    @GET
    @Path("/skills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills(@QueryParam("title") String title, @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Skill> skills = projBean.getSkillsList(title);

            if (skills == null || skills.size() == 0) {
                r = Response.status(404).entity(skills).build();
            } else {

                r = Response.status(200).entity(skills).build();
            }
        }

        return r;
    }

    // GET LIST OF KEYWORDS TO SUGGEST
    @GET
    @Path("/keywords")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeywords(@QueryParam("title") String title, @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Keyword> keywords = projBean.getKeywordsList(title);

            if (keywords == null || keywords.size() == 0) {
                r = Response.status(404).entity(keywords).build();
            } else {

                r = Response.status(200).entity(keywords).build();
            }
        }

        return r;
    }


    // GET LIST OF USERS TO SUGGEST TO PROJECT
    @GET
    @Path("/possiblemembers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPossibleMembers(@QueryParam("name") String name, @HeaderParam("token") String token, @HeaderParam("projId") int projId) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !projBean.isProjManager(token, projId)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<UserInfo> list = projBean.getPossibleMembers(name);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }

}


