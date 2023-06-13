package service;

import dto.Notification;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/communication")
public class Communication {

    @Inject
    bean.Communication comBean;
    @Inject
    bean.User userBean;

    // GET LIST OF NOTIFICATIONS OF LOGGED USER
    @GET
    @Path("/notifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnNotificationList(@HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);


            List<Notification> list = comBean.getOwnNotificationList(token);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }



}
