import dev.langchain4j.agent.tool.Tool;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class TaskTools {

    @Tool("Sends an email to a specific email address with a given subject and body text.")
    public String sendEmail(String toEmail, String subject, String body) {
        System.out.println("🤖 AI is executing Tool: Sending email to " + toEmail + "...");
        try {
            final String fromEmail = System.getenv("gmail");
            final String appPassword = System.getenv("password");

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, appPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            return "Success: Email was sent to " + toEmail;
        } catch (Exception e) {
            return "Failed to send email. Error: " + e.getMessage();
        }
    }

    @Tool("Sends a message to the public Discord server/channel.")
    public String sendDiscordMessage(String messageText) {
        System.out.println("🤖 AI is executing Tool: Sending Discord message...");
        try {
            String DISCORD_WEBHOOK_URL = System.getenv("discord");
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of("content", messageText));

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(DISCORD_WEBHOOK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204) {
                return "Success: Message sent to Discord.";
            } else {
                return "Failed: HTTP Status " + response.statusCode();
            }
        } catch (Exception e) {
            return "Failed to send Discord message. Error: " + e.getMessage();
        }
    }
}