package mail;

        import java.util.Properties;

        import jakarta.ejb.EJB;
        import jakarta.mail.Authenticator;
        import jakarta.mail.PasswordAuthentication;
        import jakarta.mail.Session;

public class AskRecoverPassword {

    /**
     * Outgoing Mail (SMTP) Server requires TLS or SSL: smtp.gmail.com (use
     * authentication) Use Authentication: Yes Port for TLS/STARTTLS: 587
     */
    public static void main(String recipientEmail, String tokenRecoverPassword) {

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

        String about = "Innovation Lab App: Recuperação da palavra-chave / Recover password ";
        String subject = "Lamentamos que esteja a ter problemas em iniciar sessão na aplicação Innovation Lab. \n\n Para repor a password e poder voltar à sua conta clique no url http://localhost:3000/changepassword/"
                + tokenRecoverPassword
                + ". \n\n Se não pediu para alterar a password ignore este email. \n\n Equipa Innovation Lab \n\n\n We are sorry that you are not able to login in our app. \n\n You can recover your password by clicking in url http://localhost:3000/changepassword/"
                + tokenRecoverPassword
                + ". \n\n If you have not asked to recover password, please ignore this email. \n\n Innovation Lab App";

        EmailUtil.sendEmail(session, toEmail, about, subject);

    }

}
