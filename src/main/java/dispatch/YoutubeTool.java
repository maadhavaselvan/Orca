package dispatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class YoutubeTool {
    @Tool("Sends a text message to our fixed Telegram group chat.")
    public String sendTelegram(String messageText) {
        System.out.println("\n--> [AGENT] Sending message to Telegram group...");
        try {
            String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
            // Hardcode it to only use the environment variable
            String targetChatId = System.getenv("TELEGRAM_CHAT_ID");
            String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of(
                    "chat_id", targetChatId,
                    "text", messageText
            ));

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var root = mapper.readTree(response.body());
            boolean ok = root.path("ok").asBoolean(false);
            return ok
                    ? "Successfully sent Telegram message to chat " + targetChatId + "."
                    : "Failed to send Telegram message. Response: " + response.body();
        }
        catch (Exception e) {
            return "Failed to send Telegram message. Error: " + e.getMessage();
        }
    }
}
