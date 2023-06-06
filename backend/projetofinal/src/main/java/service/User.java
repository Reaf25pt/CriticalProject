package service;

import dto.Login;
import dto.EditProfile;
import dto.NewAccount;
import dto.Project;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

        /*
         * HttpServletRequest request = (HttpServletRequest)
         * FacesContext.getCurrentInstance().getExternalContext().getRequest(); String
         * ipAddress = request.getHeader("X-FORWARDED-FOR"); if (ipAddress == null) {
         * ipAddress = request.getRemoteAddr(); logger.info("IP of request is " +
         * ipAddress); }
         */

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
    public Response newAccount(NewAccount account, @HeaderParam("password") String password) {
        Response r = null;

        // TODO  haverá melhor forma de colocar as validações para não repetir o código  checkEmailInDatabase ?!?!
        // TODO  faz sentido chamar método de createNewAccount dentro do checkEmailInDatabase ???

        if (account == null || userBean.checkDataToRegister(account, password)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (userBean.checkEmailInDatabase(account.getEmail()) == 400) {

            r = Response.status(400).entity("Email is already registered!").build();
        } else if (userBean.checkEmailInDatabase(account.getEmail()) == 409) {

            r = Response.status(409).entity("Account validation is missing!").build();
        } else if (userBean.checkEmailInDatabase(account.getEmail()) == 401) {

            r = Response.status(401).entity("Email is undefined").build();
        } else {

            //logger.info("New regist attempt: email used is " + u.getEmail());

            boolean newAccount = userBean.createNewAccount(account, password);
            if (!newAccount) {
                r = Response.status(404).entity("Not found!").build();
            } else {
                // logger.info("A new regist is created for email " + u.getEmail() + "An email was sent to validate account");

                r = Response.status(200).entity("Success!").build();
                //TODO ask if better to use 200 ou 201 - created
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

        if (tokenForValidation == null || tokenForValidation.isBlank()) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {

            // logger.info("Attempt to validate account through url sent to email");

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

        if (email == null || email.isBlank()) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {
            // logger.info("Attempt to recover password for email " + email);

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

        if (tokenRecoverPassword == null || tokenRecoverPassword.isBlank() || password == null || password.isBlank()) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {

            // logger.info("Attempt to modify password through url sent to email ");

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

    // EDIT OWN PROFILE INFORMATION, EXCEPT PASSWORD
    @POST
    @Path("/ownprofile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@HeaderParam("token") String token, EditProfile newInfo) {

        Response r = null;

        if (token == null || token.isBlank() || token.isEmpty() || newInfo == null) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {

            userBean.updateSessionTime(token);

           // UserDto uDto = userBean.sendDtoFromToken(token);

           // logger.info("User whose userId is " + uDto.getUserId() + " attempts to update its profile ");

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

        if (token == null || token.isBlank() || oldPassword == null || oldPassword.isBlank() || newPassword == null
                || newPassword.isBlank()) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else {
            userBean.updateSessionTime(token);
           // TokenEntity tEnt = tokenDao.findTokenEntByToken(token);
           // logger.info("User whose userId is " + tEnt.getTokenOwner().getUserId() + " attempts to modify its password");

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

        if (token == null || token.isBlank() || token.isEmpty()) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            // TokenEntity tEnt = tokenDao.findTokenEntByToken(token);
            //logger.info("User whose userId is " + tEnt.getTokenOwner().getUserId() + " attempts to see list of all its activities, including those shared with its account");

            List<Project> projects = userBean.getOwnProjectsList(token);

            if (projects == null || projects.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {
               // logger.info("Request from userId " + tEnt.getTokenOwner().getUserId() + " - List of all its activities is retrieved from database ");

                r = Response.status(200).entity(projects).build();
            }
        }

        return r;
    }


}
