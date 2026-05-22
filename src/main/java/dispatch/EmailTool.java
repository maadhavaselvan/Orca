package dispatch;

import dev.langchain4j.agent.tool.Tool;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
class EmailAuth extends  Authenticator{
    private final String email;
    private final String password;
    EmailAuth(String email,String password)
    {
        this.email=email;
        this.password=password;
    }
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(email,password);
    }
}
public class EmailTool {
    @Tool("Sends an email to a specific email address with a subject and body.")
    public String sendEmail(String toEmail, String subject, String body) {
        System.out.println("\n--> [AGENT] Sending Email to " + toEmail + "...");
        try {
            final String fromEmail = System.getenv("gmail");
            final String appPassword = System.getenv("password");

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.ssl.protocols","TLSv1.2");

            Session session = Session.getInstance(props, new EmailAuth(fromEmail,appPassword));
            toEmail = toEmail.replace(" ", ",");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            return "Success: Email sent to " + toEmail;
        }
        catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}
