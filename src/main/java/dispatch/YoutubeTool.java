package dispatch;


import dev.langchain4j.agent.tool.Tool;
import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeTool {
    @Tool("Opens the first matching YouTube video directly in the default browser.")
    public String openYoutubeVideo(String searchQuery) {
        try {
            String encoded = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String searchUrl = "https://www.youtube.com/results?search_query=" + encoded;

            // Fetch search results page to extract the first video ID
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Extract the first videoId from the page HTML
            Pattern pattern = Pattern.compile("\"videoId\":\"([a-zA-Z0-9_-]{11})\"");
            Matcher matcher = pattern.matcher(response.body());

            String videoUrl;
            if (matcher.find()) {
                String videoId = matcher.group(1);
                videoUrl = "https://www.youtube.com/watch?v=" + videoId;
            } else {
                // Fallback: open search page if no video ID found
                videoUrl = searchUrl;
                System.out.println("[WARN] Could not extract video ID, falling back to search page.");
            }

            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(videoUrl));
            }
            else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", videoUrl});            }
            return "Opened YouTube video for: " + searchQuery + " → " + videoUrl;

        }
        catch (Exception e) {
            return "Error opening URL: " + e.getMessage();
        }
    }
}
