package dispatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class DiscordTool {
    @Tool("Sends a text message to the Discord webhook.")
    public String sendDiscord(String messageText) {
        System.out.println("\n--> [AGENT] Sending message to Discord...");
        try {
            String DISCORD_WEBHOOK_URL = System.getenv("discord2");
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of("content", messageText));

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(DISCORD_WEBHOOK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204 ? "Successfully posted to Discord." : "Failed to post to Discord. Status: " + response.statusCode();
        }
        catch (Exception e) {
            return "Failed to send Discord message. Error: " + e.getMessage();
        }
    }
}
