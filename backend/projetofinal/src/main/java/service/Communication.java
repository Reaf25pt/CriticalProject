package service;

import dto.Notification;
import dto.PersonalMessage;
import dto.ProjectChat;
import dto.UserInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.crypto.spec.PSource;
import java.util.List;

/**
 * Includes all endpoints that manage communication: system notifications and personal chat. Includes response to participate in project when asked by project manager, since invite is sent by notification
 */
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

        if (userBean.checkStringInfo(token)) {
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

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);


            Notification notif = comBean.markNotifAsRead(token, id);

            if (notif == null) {
                r = Response.status(404).entity("Not found").build();
            } else {
                List<Notification> list = comBean.getOwnNotificationList(token);
                r = Response.status(200).entity(list).build();
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

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            boolean res = comBean.deleteNotif(token, id);

            if (!res) {
                r = Response.status(404).entity("Not found").build();
            } else {
                List<Notification> list = comBean.getOwnNotificationList(token);

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }

    // RESPONDER A CONVITE FEITO POR GESTOR DE PROJECTO PARA PARTICIPAR EM PROJECTO: true== 1 ACCEPT INVITE/ false ==0 REFUSE INVITE
    @POST
    @Path("/invitation/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response answerInvitation(@HeaderParam("token") String token, @PathParam("id") int id, @HeaderParam("answer") int answer) {
        Response r = null;
        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            Notification notif = comBean.answerInvitation(token, id, answer);

            if (notif == null) {
                r = Response.status(404).entity("Not found").build();
            } else {
                List<Notification> list = comBean.getOwnNotificationList(token);

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }

    // MARK ALL MESSAGES EXCHANGED BETWEEN TOKEN AND CONTACT ID AS READ
    @PATCH
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markMessagesRead(@HeaderParam("token") String token, @HeaderParam("contactId") int contactId) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            boolean res = comBean.markMessagesRead(token, contactId);

            if (!res) {
                r = Response.status(404).entity("Not found").build();
            } else {
                List<PersonalMessage> list = comBean.getAllPersonalMessages(token);

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }


    // SEND NEW PERSONAL MESSAGE TO CONTACT
    @POST
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPersonalMessage(@HeaderParam("token") String token, PersonalMessage message) {
        Response r = null;

        if (userBean.checkStringInfo(token) || message == null) {
            r = Response.status(401).entity("Unauthorized!").build();
        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();
        } else {
            userBean.updateSessionTime(token);

            PersonalMessage newMessage = comBean.sendMessageToContact(message, token);

            if (newMessage == null) {
                r = Response.status(404).entity("Something went wrong!").build();
            } else {

                r = Response.status(200).entity(newMessage).build();
            }
        }

        return r;

    }

    // GET LIST OF CONTACTS LOGGED USER HAS PERSONAL CHAT MESSAGES WITH
    @GET
    @Path("/contacts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContactsList(@HeaderParam("token") String token, @QueryParam("idToChat") int idToChat) {


        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);


            List<UserInfo> list = comBean.getContactsList(token, idToChat);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }


    // GET ALL PERSONAL MESSAGES OF LOGGED USER : sent and received
    @GET
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagesList(@HeaderParam("token") String token) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);


            List<PersonalMessage> list = comBean.getAllPersonalMessages(token);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }


    // GET MESSAGES EXCHANGED BETWEEN TOKEN AND GIVEN CONTACT ID
    @GET
    @Path("/messages/{contactId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagesForSpecificContact(@HeaderParam("token") String token, @PathParam("contactId") int contactId) {
        Response r = null;

        if (userBean.checkStringInfo(token)) {
            r = Response.status(401).entity("Unauthorized!").build();

        } else if (!userBean.checkUserPermission(token)) {
            r = Response.status(403).entity("Forbidden!").build();

        } else {
            userBean.updateSessionTime(token);

            List<PersonalMessage> list = comBean.getMessagesForSpecificContact(token, contactId);

            if (list == null || list.size() == 0) {
                r = Response.status(404).entity(list).build();
            } else {

                r = Response.status(200).entity(list).build();
            }
        }
        return r;
    }


}
