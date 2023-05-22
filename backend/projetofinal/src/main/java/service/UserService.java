package service;

import bean.User;

import dto.Login;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

}
