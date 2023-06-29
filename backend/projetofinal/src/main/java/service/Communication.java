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
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }


    // MARK NOTIFICATION AS READ
    @POST
    @Path("/notification/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markNotifAsRead(@HeaderParam("token") String token, @PathParam("id") int id) {
        Response r = null;

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);


            Notification notif = comBean.markNotifAsRead(token, id);

            if (notif == null ) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(notif).build();
            }
        }
        return r;
    }

    // DELETE NOTIFICATION
    @DELETE
    @Path("/notification/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNotif(@HeaderParam("token") String token, @PathParam("id") int id) {
        Response r = null;

        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

boolean res = comBean.deleteNotif(token, id);

            if (!res ) {
                r = Response.status(404).entity("Not found").build();
            } else {
List<Notification> list = comBean.getOwnNotificationList(token);
                // TODO estará correcto?
                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }

    // RESPONDER A CONVITE PARA PARTICIPAR EM PROJECTO: true== 1 ACCEPT INVITE/ false ==0 REFUSE INVITE
    @POST
    @Path("/invitation/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response answerInvitation(@HeaderParam("token") String token, @PathParam("id") int id, @HeaderParam("answer") int answer ) {
        Response r = null;
        System.out.println("answer endpoint"+answer);
        if (userBean.checkStringInfo(token) ) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            Notification notif = comBean.answerInvitation(token, id, answer);

            if (notif == null ) {
                r = Response.status(404).entity("Not found").build();
            } else {

                r = Response.status(200).entity(notif).build();
            }
        }
        return r;
    }


}
