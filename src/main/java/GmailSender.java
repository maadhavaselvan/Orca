import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Scanner;
public class GmailSender {

    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        final String fromEmail = System.getenv("gmail");
        final String appPassword = System.getenv("password"); // App Password (no spaces required)

        // Gmail SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Authenticate
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        // Build and send the message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body); // Plain text — use setContent() for HTML

        Transport.send(message);
        System.out.println("Email sent successfully!");
    }

    public static void main(String[] args) throws MessagingException {
        Scanner sc=new Scanner(System.in);
        String email=sc.nextLine();
        sendEmail(email, "Hello!", "This is my string from Java.");
    }
}