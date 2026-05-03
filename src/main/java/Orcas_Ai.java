// LangChain4j Core
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.data.message.ChatMessage;

// Static imports for message helpers
import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;

// Google Gemini
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
// OpenAI-compatible (used for Groq)
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.workersai.WorkersAiChatModel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Scanner;
class Ai {
    ChatModel Ai;
    ChatResponse Respond(ChatMessage system, ChatMessage user)
    {
        return Ai.chat(ChatRequest.builder()
                .messages(system, user)
                .build());
    }
}
class OpenAiChat extends Ai
{
    OpenAiChat(String name,String baseUrl,String modelName)
    {
            this.Ai=OpenAiChatModel.builder()
                    .baseUrl(baseUrl)
                    .apiKey(System.getenv(name.toUpperCase()+"_API_KEY"))
                    .modelName(modelName)
                    .build();
    }
}
class dedicatedAiChat extends Ai
{
    dedicatedAiChat(String name,String modelName)
    {
        switch (name.toLowerCase()) {
            case "gemini":
            case "google":
                this.Ai = GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GOOGLE_API_KEY"))
                        .modelName(modelName)
                        .build();
                break;
            case "mistral":
                this.Ai = MistralAiChatModel.builder()
                        .apiKey(System.getenv("MISTRAL_API_KEY"))
                        .modelName(modelName)
                        .build();
                break;
            case "cloudflare":
                this.Ai= WorkersAiChatModel.builder()
                        .accountId(System.getenv("CLOUDFLARE_ACCOUNT_ID"))
                        .apiToken(System.getenv("CLOUDFLARE_API_TOKEN"))
                        .modelName(modelName)
                        .build();
                break;
            default:
                System.out.println("Sorry we are working on adding support to that Ai Model");
        }
    }

}

public class Orcas_Ai
{
    public static void main(String[] args) {
        dedicatedAiChat gemini=new dedicatedAiChat("gemini","gemini-2.5-flash");
        OpenAiChat groq=new OpenAiChat("groq","https://api.groq.com/openai/v1","llama-3.1-8b-instant");
        OpenAiChat cohere=new OpenAiChat("cohere","https://api.cohere.com/compatibility/v1","command-r7b-12-2024");
        dedicatedAiChat mistral=new dedicatedAiChat("mistral","open-mistral-nemo");
        dedicatedAiChat cloudflare=new dedicatedAiChat("cloudflare","@cf/meta/llama-3.1-8b-instruct");
        OpenAiChat huggingface=new OpenAiChat("huggingface","https://router.huggingface.co/v1","Qwen/Qwen2.5-7B-Instruct");
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the behaviour you want the Ai to have:");
        ChatMessage system = systemMessage(sc.nextLine());
        System.out.print("Enter the prompt:");
        ChatMessage user = userMessage(sc.nextLine());
        Ai[] AllAi={groq,cohere,mistral,cloudflare,huggingface,gemini};
        String UMessage="Question asked by user:"+user;
        for (int i=0;i<AllAi.length-1;i++)
        {
           UMessage+=(AllAi[i].Respond(system,user).aiMessage().text());
        }
        system = systemMessage("I will you give you the responses of 5 ai to a question asked by one user select the best one out of that and dont tell the reason just repeat the best one ");
        user = userMessage(UMessage);
        String finalOutput=AllAi[AllAi.length-1].Respond(system,user).aiMessage().text();
        System.out.println(finalOutput);
        if (finalOutput.length() > 1800) {
            finalOutput = finalOutput.substring(0, 1800);
        }
        System.out.println("Dispatching output to Discord...");
        Main.sendMessage(finalOutput);
    }
}
class Main {

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
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of("content", selectedMessage));


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
    public static void test(String[] args) {
        // Simulation of your Judge AI result
        String finalOutput = "Nee waste suriya.";

        System.out.println("Dispatching output to Discord...");
        sendMessage(finalOutput);
    }
}
