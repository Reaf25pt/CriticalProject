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


@Path("/user")
public class UserService {

    @Inject
    User userBean;

    // Login
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

    // Logout
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

    // New regist in the app
    @POST
    @Path("/newaccount")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newAccount(NewAccount account, @HeaderParam("password") String password) {
        Response r = null;

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


    // Validate account after new regist, through link
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


    // Ask to recover password
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


}
