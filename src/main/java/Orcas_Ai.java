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
import java.net.http.HttpClient;// Don't forget to add multithreading
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Scanner;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import java.util.List;
import java.util.ArrayList;
import static dev.langchain4j.data.message.AiMessage.aiMessage;
class Ai {
    ChatModel Ai;
    ChatResponse Respond(ChatMessage system, ChatMessage user)
    {
        return Ai.chat(ChatRequest.builder()
                .messages(system, user)
                .build());
    }
    ChatResponse Respond(List<ChatMessage> user)
    {
        return Ai.chat(ChatRequest.builder()
                .messages(user)
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
    static ChatMemory memory = TokenWindowChatMemory.builder()
            .maxTokens(4000, new OpenAiTokenCountEstimator("gpt-3.5-turbo"))
            .build();    static String Ai_Decision(Ai[] AllAi,ChatMessage system)
    {
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter the prompt:");
        ChatMessage user = userMessage(sc.nextLine());
        String UMessage = "Question asked by user: " + ((dev.langchain4j.data.message.UserMessage) user).singleText();
        memory.add(user);
        List<ChatMessage> history = memory.messages();
        List<ChatMessage> safeHistory = new ArrayList<>();
        for (ChatMessage msg : history) {
            if (msg instanceof dev.langchain4j.data.message.AiMessage) {
                String previousAiText = ((dev.langchain4j.data.message.AiMessage) msg).text();
                safeHistory.add(userMessage("Assistant previously said: " + previousAiText));
            }
            else {
                safeHistory.add(msg);
            }
        }
        safeHistory.add(system);
        for (int i=0;i<AllAi.length-1;i++)
        {

            UMessage+="Answer "+(i+1)+": "+(AllAi[i].Respond(safeHistory).aiMessage().text());
        }
        List<ChatMessage> judgeContext = new ArrayList<>();
        system = systemMessage("You are a response evaluator. You will receive a user question followed by 5 AI-generated answers.\n" +
                "\n" +
                "Your job is to return ONLY the single best answer as-is, word for word.\n" +
                "\n" +
                "Use these criteria to judge:\n" +
                "- Accuracy: Is the answer factually correct?\n" +
                "- Completeness: Does it fully address what the user asked?\n" +
                "- Clarity: Is it easy to understand?\n" +
                "- Conciseness: Does it avoid unnecessary filler or repetition?\n" +
                "\n" +
                "Rules you must follow:\n" +
                "- Do NOT add any introduction like \"Here is the best answer\"\n" +
                "- Do NOT add any explanation of why you chose it\n" +
                "- Do NOT modify, summarize or improve the chosen answer\n" +
                "- Do NOT combine multiple answers together\n" +
                "- Return ONLY the chosen answer and nothing else ");
        judgeContext.add(system);
        judgeContext.add(userMessage(UMessage));
        String finalOutput=AllAi[AllAi.length-1].Respond(judgeContext).aiMessage().text();
        memory.add(aiMessage(finalOutput));
        System.out.println(finalOutput);
        sc.close();
        return finalOutput;
    }
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
        Ai[] AllAi={groq,cohere,mistral,cloudflare,huggingface,gemini};
        String finalOutput;
        while (true)
        {
            finalOutput=Orcas_Ai.Ai_Decision(AllAi,system);
            String a;
            while (true) {
                System.out.print("Is the above output satisfactory(enter yes/no):");
                a= sc.nextLine();
                if (a.equalsIgnoreCase("yes")) {
                    break;
                }
                else if (a.equalsIgnoreCase("no")) {
                    break;
                }
                else{
                    System.out.println("Please Enter yes or no only");
                }
            }
            if(a.equalsIgnoreCase("yes"))
                break;
            System.out.println("Please enter the changes you want in the below prompt");
        }
        if (finalOutput.length() > 1800) {
            finalOutput = finalOutput.substring(0, 1800);
        }
        System.out.println("Dispatching output to Discord...");
        sc.close();
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
}
