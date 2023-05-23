package mail;



        import java.util.Properties;


        import jakarta.ejb.EJB;
        import jakarta.mail.Authenticator;
        import jakarta.mail.PasswordAuthentication;
        import jakarta.mail.Session;

public class ValidateNewAccount {

    /**
     * Outgoing Mail (SMTP) Server requires TLS or SSL: smtp.gmail.com (use
     * authentication) Use Authentication: Yes Port for TLS/STARTTLS: 587
     */
    public static void main(String recipientEmail, String tokenForActivaction) {

        final String fromEmail = "joanaramalho@student.dei.uc.pt"; // requires valid gmail id
        final String password = ""; // correct password for gmail id
        // String toEmail = "joanaramalho@student.dei.uc.pt"; // can be any email id
        String toEmail = recipientEmail; // can be any email id

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.dei.uc.pt"); // SMTP Host
        props.put("mail.smtp.port", "587"); // TLS Port
        props.put("mail.smtp.auth", "true"); // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

        // create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            // override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        String about = "Innovation Lab App: Activação da conta / Account activation";
        String subject = " Bem-vindo à aplicação Laboratório de Inovação.\n\n Para validar a sua conta tem de confirmar o email usado. Clique no url http://localhost:3000/activation/"
                + tokenForActivaction
                + " .\n\n Se não se registou na aplicação ignore este email.\n\n Obrigado pela preferência, \n Equipa Innovation Lab  \n\n\n Welcome to Innovation Lab App. To validate your account you must first confirm your email, by clicking in url  http://localhost:3000/activation/"
                + tokenForActivaction
                + " .\n\n In case you did not registered in the app, please ignore this email. \n\n Thank you for your preference, \n Innovation Lab App";

        EmailUtil.sendEmail(session, toEmail, about, subject);

    }

}

