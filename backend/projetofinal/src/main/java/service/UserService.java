package service;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// login, registo, alterar password, ver / editar perfil
@Path("/user")
public class UserService {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("email") String email, @HeaderParam("password") String password) {
        Response r = null;


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
