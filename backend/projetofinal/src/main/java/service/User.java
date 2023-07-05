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


@Path("/user")
public class User {

    @Inject
    bean.User userBean;

    // LOGIN
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("email") String email, @HeaderParam("password") String password) {
        Response r = null;

        if (userBean.validateLoginInfo(email, password)) {
            Profile userLogged = userBean.validateLogin(email, password);
            if (userLogged == null) {
                r = Response.status(404).entity("Not found").build();
            } else {
                r = Response.status(200).entity(userLogged).build();
            }
        } else {
            r = Response.status(401).entity("Unauthorized").build();
        }

        return r;

    }

    // LOGOUT
    @Context
    private HttpServletRequest request;

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("token") String token) {

        Response r = null;
        HttpSession session = request.getSession();

        if (token == null || token.isBlank() || token.isEmpty()) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else {
            int value = userBean.validateLogout(token);

            if (value == 200) {
                r = Response.status(200).entity("Success!").build();
                session.invalidate();
            } else if (value == 400) {
                r = Response.status(400).entity("Failed!").build();
            } else if (value == 403)
                r = Response.status(403).entity("Forbidden").build();
        }
        return r;

    }

    // NEW REGIST IN THE APP
    @POST
    @Path("/newaccount")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newAccount(@HeaderParam("email") String email, @HeaderParam("password") String password) {
        Response r = null;


        if (userBean.checkStringInfo(email) || userBean.checkStringInfo(password)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {
            int statusCode = userBean.checkEmailInDatabase(email);

            if (statusCode == 400) {
                r = Response.status(400).entity("Email is already registered!").build();
            } else if (statusCode == 409) {
                r = Response.status(409).entity("Account validation is missing!").build();
            } else if (statusCode == 401) {
                r = Response.status(401).entity("Email is undefined").build();
            } else {
                boolean newAccount = userBean.createNewAccount(email, password);
                if (!newAccount) {
                    r = Response.status(404).entity("Not found!").build();
                } else {
                    r = Response.status(200).entity("Success!").build();

                }
            }
        }
        return r;
    }


    // VALIDATE ACCOUNT AFTER NEW REGIST, THROUGH LINK
    @POST
    @Path("/accountvalidation")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validation(@HeaderParam("tokenForValidation") String tokenForValidation) {
        Response r = null;

        if (userBean.checkStringInfo(tokenForValidation)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else {
            int validationCode = userBean.validateNewAccount(tokenForValidation);
            if (validationCode == 404) {
                r = Response.status(404).entity("Not found!").build();
            } else if (validationCode == 200) {

                r = Response.status(200).entity("Success!").build();
            } else if (validationCode == 400) {
                r = Response.status(400).entity("link is not valid ").build();
            }
        }
        return r;

    }


    // ASK TO RECOVER PASSWORD
    @POST
    @Path("/recoverpassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response recoverPassword(@HeaderParam("email") String email) {
        Response r = null;

        if (userBean.checkStringInfo(email)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {
            boolean res = userBean.askToRecoverPassword(email);
            if (!res) {
                r = Response.status(404).entity("Email not found in database!").build();
            } else {

                r = Response.status(200).entity("Success!").build();
            }
        }

        return r;

    }

    // RECOVER PASSWORD THROUGH LINK
    @POST
    @Path("/newpasswordvialink")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@HeaderParam("tokenRecoverPassword") String tokenRecoverPassword,
                                   @HeaderParam("password") String password) {
        Response r = null;

        if (userBean.checkStringInfo(tokenRecoverPassword) || userBean.checkStringInfo(password)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {

            int res = userBean.newPasswordViaLink(tokenRecoverPassword, password);
            if (res == 404) {
                r = Response.status(404).entity("Not found!").build();
            } else if (res == 200) {

                r = Response.status(200).entity("Success!").build();
            } else if (res == 400) {
                r = Response.status(400).entity("link is not valid ").build();
            }
        }

        return r;

    }

    // FILL MANDATORY PROFILE INFO OF USER IN ITS 1ST LOGIN
    @PATCH
    @Path("/ownprofile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMandatoryInfo(@HeaderParam("token") String token, Profile newInfo) {

        Response r = null;

        if (userBean.checkStringInfo(token) || userBean.checkStringInfo(newInfo.getFirstName()) || userBean.checkStringInfo(newInfo.getLastName())) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            // neste ponto o user tem autorização para fazer update da sua info e não é necessário validar se info vem preenchida ou existe na DB pq único campo que tem de ser único não é actualizado (email)

            Profile userUpdated = userBean.addMandatoryInfo(token, newInfo);
            if (userUpdated == null) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                r = Response.status(200).entity(userUpdated).build();

            }
        }
        return r;

    }


    // EDIT OWN PROFILE INFORMATION, EXCEPT PASSWORD
    @POST
    @Path("/ownprofile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@HeaderParam("token") String token, Profile newInfo) {

        Response r = null;

        if (userBean.checkStringInfo(token) || newInfo == null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            Profile userUpdated = userBean.updateProfile(token, newInfo);
            if (userUpdated == null) {
                r = Response.status(404).entity("Not found!").build();

            } else {
                r = Response.status(200).entity(userUpdated).build();

            }
        }
        return r;

    }

    // CHANGE PASSWORD ONCE LOGGED IN
    @POST
    @Path("/newpassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeOwnPassword(@HeaderParam("token") String token,
                                      @HeaderParam("oldPassword") String oldPassword, @HeaderParam("newPassword") String newPassword) {
        Response r = null;

        if (userBean.checkStringInfo(token) || userBean.checkStringInfo(oldPassword) || userBean.checkStringInfo(newPassword)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {
            userBean.updateSessionTime(token);

            int a = userBean.changeOwnPassword(token, oldPassword, newPassword);
            if (a == 404) {
                r = Response.status(404).entity("Not found!").build();
            } else if (a == 200) {

                r = Response.status(200).entity("Success!").build();
            } else if (a == 400) {
                r = Response.status(400).entity("old password is not correct ").build();
            } else {
                r = Response.status(409).entity("Conflict!").build();

            }
        }

        return r;

    }


    // ADICIONAR HOBBY À PP LISTA DE HOBBIES
    @POST
    @Path("/newhobby")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHobby(@HeaderParam("token") String token, String title) {

        Response r = null;

        if (userBean.checkStringInfo(token) || userBean.checkStringInfo(title)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            Hobby hobby = userBean.addHobby(token, title);
            if (hobby == null) {
                r = Response.status(404).entity("Not found!").build();
                //TODO melhorar texto de resposta? aqui o not found na verdade significa que a relação já existe ...
            } else {
                r = Response.status(200).entity(hobby).build();
                // permite apresentar logo no frontend com id do hobby para poder eventualmente apagar
            }
        }
        return r;

    }


    // ADICIONAR SKILL À PP LISTA DE SKILLS
    @POST
    @Path("/skill")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSkill(@HeaderParam("token") String token, Skill skill) {

        Response r = null;

        if (userBean.checkStringInfo(token) || userBean.checkSkillInfo(skill)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            Skill newSkill = userBean.addSkillToOwnProfile(token, skill);
            if (newSkill == null) {
                r = Response.status(404).entity("Not found!").build();
            } else {
                r = Response.status(200).entity(newSkill).build();
                // permite apresentar logo no frontend com id da skill para poder eventualmente apagar
            }
        }
        return r;

    }


    // DELETE HOBBY
    @DELETE
    @Path("/hobby")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHobbyOfUser(@HeaderParam("token") String token, int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            boolean res = userBean.deleteHobby(token, id);
            if (!res) {
                r = Response.status(404).entity("Not found!").build();
            } else {
                r = Response.status(200).entity("Success").build();

            }
        }
        return r;

    }

    // DELETE SKILL
    @DELETE
    @Path("/skill")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSkillOfUser(@HeaderParam("token") String token, int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

            boolean res = userBean.deleteSkill(token, id);
            if (!res) {
                r = Response.status(404).entity("Not found!").build();
            } else {
                r = Response.status(200).entity("Success").build();

            }
        }
        return r;

    }

    // GET LIST OF ALL USERS
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam("token") String token) {


        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<UserInfo> users = userBean.getAllUsers(token);

            if (users == null || users.size() == 0) {
                r = Response.status(404).entity(users).build();
            } else {

                r = Response.status(200).entity(users).build();
            }
        }

        return r;
    }

    // GET LIST OF USERS TO SUGGEST
    @GET
    @Path("/suggestion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersToSuggest(@QueryParam("name") String name, @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<UserInfo> list = userBean.getUsersToSuggest(name, token);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }


    // GET INFO ON ACTIVE PROJECT THAT LOGGED USER HAS
    @GET
    @Path("/activeproject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveProject(@HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {

            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            Project project = userBean.getActiveProjectInfo(token);
            //  ActiveProjectToken project = userBean.getActiveProjectInfo(token);

            if (project == null) {
                r = Response.status(404).entity(project).build();
            } else {

                r = Response.status(200).entity(project).build();
            }
        }

        return r;
    }


    // GET USER PROFILE BY USER ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@HeaderParam("token") String token, @PathParam("id") int id) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token) || !userBean.checkUserHasOpenProfile(id)) {

            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            AnotherProfile profile = userBean.getAnotherProfile(token, id);

            if (profile == null) {
                r = Response.status(404).entity(profile).build();
            } else {

                r = Response.status(200).entity(profile).build();
            }
        }

        return r;
    }


    // GET LIST OF PROJECTS OF LOGGED USER
    @GET
    @Path("/ownprojects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnProjects(@HeaderParam("token") String token) {

        // ir buscar lista de projectos que user id do token logado seja membro (accepted and not removed)
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Project> projects = userBean.getOwnProjectsList(token);

            if (projects == null || projects.size() == 0) {
                r = Response.status(404).entity(projects).build();
            } else {

                r = Response.status(200).entity(projects).build();
            }
        }

        return r;
    }


    // GET LIST OF OWN HOBBIES
    @GET
    @Path("/ownhobbies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnHobbies(@HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Hobby> hobbies = userBean.getOwnHobbiesList(token);

            if (hobbies == null || hobbies.size() == 0) {
                r = Response.status(404).entity(hobbies).build();
            } else {

                r = Response.status(200).entity(hobbies).build();
            }
        }

        return r;
    }

    // GET LIST OF OWN SKILLS
    @GET
    @Path("/ownskills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnSkills(@HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Skill> skills = userBean.getOwnSkillsList(token);

            if (skills == null || skills.size() == 0) {
                r = Response.status(404).entity(skills).build();
            } else {

                r = Response.status(200).entity(skills).build();
            }
        }

        return r;
    }


    // GET LIST OF SKILLS TO SUGGEST TO USER
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

            List<Skill> skills = userBean.getSkillsList(title, token);

            if (skills == null || skills.size() == 0) {
                r = Response.status(404).entity(skills).build();
            } else {

                r = Response.status(200).entity(skills).build();
            }
        }

        return r;
    }

    // GET LIST OF HOBBIES TO SUGGEST
    @GET
    @Path("/hobby")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHobbies(@QueryParam("title") String title, @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Hobby> hobbies = userBean.getHobbiesList(title, token);

            if (hobbies == null || hobbies.size() == 0) {
                r = Response.status(404).entity(hobbies).build();
            } else {

                r = Response.status(200).entity(hobbies).build();
            }
        }

        return r;
    }

    // GET LIST OF OFFICES ENUM
    @GET
    @Path("/offices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOffices(@HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            HashMap<Integer, String> list = userBean.getOfficeList();

            if (list == null) {
                r = Response.status(404).entity(list).build();
            } else {
                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }

    // GET LIST OF SKILL TYPES ENUM
    @GET
    @Path("/skilltypes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListOfSkillTypes(@HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            HashMap<Integer, String> list = userBean.getSkillTypesList();

            if (list == null) {
                r = Response.status(404).entity(list).build();
            } else {
                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }


    // ALTERA PAPEL DE USER: GESTOR DE CONCURSO OU USER NORMAL
    // Apenas para usar no postman - método administrativo mas que permite garantir que user passa a PERFIL A tratanda de "dependências"
    @PATCH
    @Path("/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeUserProfile(@HeaderParam("role") int role, @HeaderParam("userId") int userId) {

        Response r = null;

        boolean res = userBean.modifyProfileType(role, userId);
        if (!res) {
            r = Response.status(404).entity("Forbidden").build();
        } else {
            r = Response.status(200).entity("Success").build();

        }

        return r;

    }


}
