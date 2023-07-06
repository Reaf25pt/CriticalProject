package websocket;


import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.websocket.Session;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
/**
 * Defines websocket to send messages to project Chat in real time if and when appropriate for open sessions
 * OnOpen 'creates' the socket
 * sendNotification acts as OnMessage, sending the object to session identified as recipientToken
 * OnClose closes the socket
 */
@ServerEndpoint("/websocket/projectchat/{token}")
public class ProjectChat {

    static HashMap<String, Session> sessions = new HashMap<String, Session>();

    public static void send(String message, String token) {
        Session session = sessions.get(token);

        if (session != null) {
            try {
                System.out.println("sending...." + message);
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                System.out.println("Something went wrong");
            }
        }
    }

    @OnOpen
    public void notifierOnOpen(Session session, @PathParam("token") String token) throws IOException {
        System.out.println("Websocket opened: " + token);
        sessions.put(token, session);
        // session.getBasicRemote().sendText("9");

    }

    // reason -> explicação / motivo pelo qual a ligação foi perdida. Pode-se
    // remover
    @OnClose
    public void notifierOnClose(Session session, CloseReason reason) {
        System.out.println("Websocket connection closed with CloseCode: " + reason.getCloseCode());
        for (String key : sessions.keySet()) {
            if (sessions.get(key) == session)
                sessions.remove(key);

        }
    }

    /*
     * @OnMessage public String newMessage(String msg) {
     * System.out.println("A new message is received: " + msg + "'"); return
     * ("Message Received"); }
     */

    public static void sendNotification(Object obj, String recipientToken) {
        System.out.println("metodo web socket send notification " + recipientToken);
        Session session = null;

        try {
            session = sessions.get(recipientToken);
        } catch (Exception e) {
            System.out.println("No session found for token");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(obj);
            System.out.println("metodo web socket send notification object " + json);

            session.getBasicRemote().sendText(json);
        } catch (Exception e) {
            System.out.println("Something went wrong " + e);
        }
    }

}

