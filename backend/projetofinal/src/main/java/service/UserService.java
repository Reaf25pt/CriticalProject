package service;

import bean.User;

import dto.Login;
import dto.EditProfile;
import dto.NewAccount;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// login, registo, alterar password, ver / editar perfil
@Path("/user")
public class UserService {

    @Inject
    User userBean;

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


    @POST
    @Path("/newAccount")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newAccount(NewAccount account, @HeaderParam("password") String password) {
        Response r = null;

        if (account == null || userBean.checkDataToRegister(account, password)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (userBean.checkEmailInDatabase(account.getEmail())==400) {

            r = Response.status(400).entity("Email is already registered!").build();
        } else if (userBean.checkEmailInDatabase(account.getEmail())==409) {

            r = Response.status(409).entity("Account validation is missing!").build();
        } else if (userBean.checkEmailInDatabase(account.getEmail())==401) {

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




}
