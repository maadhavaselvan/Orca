import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Main {

  // STEP 1: Paste your unique Discord Webhook URL here
  private static final String DISCORD_WEBHOOK_URL = System.getenv("discord");

  /**
   * Sends the selected message to the specified Discord channel.
   * @param selectedMessage The text chosen by your Judge AI.
   */
  public static void sendMessage(String selectedMessage) {
    try {
      // STEP 2: Format the data into JSON.
      // Discord requires a JSON body like: {"content": "your text"}
      // We use replace() to handle basic double quotes within the message.
      String jsonBody = String.format("{\"content\": \"%s\"}",
              selectedMessage.replace("\"", "\\\""));

      // STEP 3: Build the HTTP Request
      HttpClient client = HttpClient.newBuilder()
              .connectTimeout(Duration.ofSeconds(10))
              .build();

      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(DISCORD_WEBHOOK_URL))
              .header("Content-Type", "application/json") // CRITICAL: Tells Discord it's JSON
              .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
              .build();

      // STEP 4: Send the request and check the response
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 204) {
        System.out.println("[SUCCESS] Message sent to Discord!");
      } else {
        System.out.println("[ERROR] Failed to send. HTTP Status: " + response.statusCode());
        System.out.println("Response body: " + response.body());
      }

    } catch (Exception e) {
      System.err.println("[EXCEPTION] Error dispatching webhook: " + e.getMessage());
    }
  }

  // This is how you would call it from your project
  public static void main(String[] args) {
    // Simulation of your Judge AI result
    String finalOutput = "Nee waste suriya.";

    System.out.println("Dispatching output to Discord...");
    sendMessage(finalOutput);
  }
}
