package service;

import ENUM.Office;
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
            Login userLogged = userBean.validateLogin(email, password);
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
            // TODO incluir hipotese de res vir sem info? algo pode correr mal. nesse caso qual o erro apropriado?
        }
        return r;

    }

    // NEW REGIST IN THE APP
    @POST
    @Path("/newaccount")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newAccount(@HeaderParam("email") String email, @HeaderParam("password") String password) {
        Response r = null;

        // TODO  haverá melhor forma de colocar as validações para não repetir o código  checkEmailInDatabase ?!?!
        // TODO  faz sentido chamar método de createNewAccount dentro do checkEmailInDatabase ???

        if (userBean.checkStringInfo(email) || userBean.checkStringInfo(password)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {
            int statusCode = userBean.checkEmailInDatabase(email);

            if (statusCode== 400){
                r = Response.status(400).entity("Email is already registered!").build();
            } else if (statusCode==409){
                r = Response.status(409).entity("Account validation is missing!").build();
            } else if(statusCode==401){
                r = Response.status(401).entity("Email is undefined").build();
            } else {
                boolean newAccount = userBean.createNewAccount(email, password);
                if (!newAccount) {
                    r = Response.status(404).entity("Not found!").build();
                } else {
                    r = Response.status(200).entity("Success!").build();
                    //TODO ask if better to use 200 ou 201 - created
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

        if ( userBean.checkStringInfo(tokenRecoverPassword) || userBean.checkStringInfo(password)) {
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
    public Response addMandatoryInfo(@HeaderParam("token") String token, EditProfile newInfo) {

        Response r = null;

        if (userBean.checkStringInfo(token) || userBean.checkStringInfo(newInfo.getFirstName()) || userBean.checkStringInfo(newInfo.getLastName())) {
            r = Response.status(401).entity("Unauthorized!").build();
            // TODO decidir como verificar se office vem preenchido do frontend e validar em conformidade

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
            // TODO permitir apenas se fillInfo for false?

        } else {

            userBean.updateSessionTime(token);

            // neste ponto o user tem autorização para fazer update da sua info e não é necessário validar se info vem preenchida ou existe na DB pq único campo que tem de ser único não é updated (email)

            EditProfile userUpdated = userBean.addMandatoryInfo(token, newInfo);
            if (userUpdated == null) {
                r = Response.status(404).entity("Not found!").build();
                //TODO erro 404 not found  ou  400 bad request?

            } else {
                r = Response.status(200).entity(userUpdated).build();
                //TODO update info na userStore ou enviar LoginDto em x do EditProfile
            }
        }
        return r;

    }





    // EDIT OWN PROFILE INFORMATION, EXCEPT PASSWORD
    @POST
    @Path("/ownprofile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@HeaderParam("token") String token, EditProfile newInfo) {

        Response r = null;

        if (userBean.checkStringInfo(token)|| newInfo == null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            System.out.println(newInfo.isOpenProfile());
            userBean.updateSessionTime(token);

            // neste ponto o user tem autorização para fazer update da sua info e não é necessário validar se info vem preenchida ou existe na DB pq único campo que tem de ser único não é updated (email)

            EditProfile userUpdated = userBean.updateProfile(token, newInfo);
            if (userUpdated == null) {
                r = Response.status(404).entity("Not found!").build();
                //TODO erro 404 not found  ou  400 bad request?

            } else {
                r = Response.status(200).entity(userUpdated).build();
                //TODO update info na userStore ou enviar LoginDto em x do EditProfile
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

        if (userBean.checkStringInfo(token)|| userBean.checkStringInfo(oldPassword) || userBean.checkStringInfo(newPassword)) {
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

                // TODO manter ou simplesmente apagar / alterar erro?
            }
        }

        return r;

    }



    // GET LIST OF PROJECTS OF LOGGED USER
    @GET
    @Path("/ownprojects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnProjects(@HeaderParam("token") String token) {

        // verificar se token tem sessão iniciada e válida, se sim actualizar session time
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
                r = Response.status(404).entity("Not found").build();
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

        // verificar se token tem sessão iniciada e válida, se sim actualizar session time
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Hobby> hobbies = userBean.getOwnHobbiesList(token);

            if (hobbies == null || hobbies.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(hobbies).build();
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

        if (userBean.checkStringInfo(token) ||userBean.checkStringInfo(title)) {
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

    // GET LIST OF OFFICES
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

            if (list == null ) {
                r = Response.status(404).entity("Not found").build();
            } else {
                r = Response.status(200).entity(list).build();
            }
        }

        return r;
    }

    // GET LIST OF SKILL TYPES
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

            if (list == null ) {
                r = Response.status(404).entity("Not found").build();
            } else {
                r = Response.status(200).entity(list).build();
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

    // GET LIST OF OWN SKILLS
    @GET
    @Path("/ownskills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnSkills(@HeaderParam("token") String token) {

        // verificar se token tem sessão iniciada e válida, se sim actualizar session time
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Skill> skills = userBean.getOwnSkillsList(token);

            if (skills == null || skills.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(skills).build();
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

        if (userBean.checkStringInfo(token) ) {
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

        if (userBean.checkStringInfo(token) ) {
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

    // GET LIST OF SKILLS
    @GET
    @Path("/skills")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills(@QueryParam("title") String title,  @HeaderParam("token") String token) {

        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            List<Skill> skills = userBean.getSkillsList(title);

            if (skills == null || skills.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(skills).build();
            }
        }

        return r;
    }


}
