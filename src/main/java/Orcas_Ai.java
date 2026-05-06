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
    protected ChatModel Ai;
    protected String name;
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
        this.name=name;
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
        this.name=name;
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
            default:
                System.out.println("Sorry we are working on adding support to that Ai Model");
        }
    }

}

public class Orcas_Ai
{
    static Scanner sc=new Scanner(System.in);
    static ChatMemory memory = TokenWindowChatMemory.builder()
            .maxTokens(4000, new OpenAiTokenCountEstimator("gpt-3.5-turbo"))
            .build();    static String Ai_Decision(Ai[] AllAi,ChatMessage system)
    {
        System.out.print("Enter the prompt:");
        ChatMessage user = userMessage(sc.nextLine());
        String UMessage = "Question asked by user: " + ((dev.langchain4j.data.message.UserMessage) user).singleText();
        memory.add(user);
        List<ChatMessage> history = memory.messages();
        for (int i=0;i<AllAi.length-1;i++)
        {
            try {
                UMessage += "Answer " + (i + 1) + ": " + (AllAi[i].Respond(history).aiMessage().text());
            }
            catch(Exception e)
            {
                System.out.println("The server at "+AllAi[i].name+"is Overloaded, so this output is being skipped sorry for the inconvenience");
            }
        }
        List<ChatMessage> judgeContext = new ArrayList<>();
        system = systemMessage(
                "You are an expert response evaluator and synthesizer. You will receive a user prompt followed by 5 AI-generated responses.\n" +
                        "\n" +
                        "## Step 1: Classify the Task\n" +
                        "First, determine the nature of the task:\n" +
                        "\n" +
                        "- ATOMIC: The response is a single cohesive unit that cannot be split or recombined without losing its meaning or effect.\n" +
                        "  Examples: jokes, poems, riddles, creative one-liners, haikus, puns, stories.\n" +
                        "\n" +
                        "- COMPOSITIONAL: The response is made of separable parts where different sources may each contribute stronger sections.\n" +
                        "  Examples: explanations, factual answers, essays, code, step-by-step guides, comparisons, summaries.\n" +
                        "\n" +
                        "## Step 2: Apply the Right Strategy\n" +
                        "\n" +
                        "### If ATOMIC → SELECT\n" +
                        "- Evaluate all 5 responses using these criteria:\n" +
                        "  - Impact: Is it funny, moving, or effective at what it is trying to do?\n" +
                        "  - Originality: Is it creative and non-generic?\n" +
                        "  - Correctness: Does it fully land (e.g. does the joke make sense, does the poem flow)?\n" +
                        "- Return ONLY the single best response, word for word, with zero modifications.\n" +
                        "\n" +
                        "### If COMPOSITIONAL → SYNTHESIZE\n" +
                        "- Evaluate all 5 responses using these criteria:\n" +
                        "  - Accuracy: Is the information factually correct?\n" +
                        "  - Completeness: Does it fully address what the user asked?\n" +
                        "  - Clarity: Is it easy to understand?\n" +
                        "  - Conciseness: Does it avoid unnecessary filler or repetition?\n" +
                        "- Extract the strongest elements from each response.\n" +
                        "- Write a single unified response that combines the best structure, depth, accuracy, and clarity from all 5.\n" +
                        "- The result should be better than any individual response alone.\n" +
                        "\n" +
                        "## Rules you must follow in ALL cases:\n" +
                        "- Do NOT start with any introduction like 'Here is the best answer' or 'Based on my evaluation'\n" +
                        "- Do NOT explain your classification or your reasoning\n" +
                        "- Do NOT mention which responses you used or preferred\n" +
                        "- Return ONLY the final answer and absolutely nothing else\n"+
                        "- If it exceeds 1900 characters rewrite it so it doesnt exceed 1900 characters");

        judgeContext.add(system);
        judgeContext.add(userMessage(UMessage));
        String finalOutput="All Ai's are busy so no output";
        for(int i=AllAi.length-1;i>=0;i--) {
            try {
                finalOutput = AllAi[i].Respond(judgeContext).aiMessage().text();
                break;
            } catch (Exception e) {
                if(i!=0)
                    System.out.println(AllAi[i].name+" busy right now so judge Ai is being replaced by"+AllAi[i-1].name);
                else {
                    System.out.println("All 6 Ai's are busy, so this program will now close");
                    System.exit(0);
                }
            }
        }
        memory.add(aiMessage(finalOutput));
        System.out.println(finalOutput);
        return finalOutput;
    }
    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.log.dev.langchain4j", "error");
        dedicatedAiChat gemini=new dedicatedAiChat("gemini","gemini-2.5-flash");
        OpenAiChat groq=new OpenAiChat("groq","https://api.groq.com/openai/v1","llama-3.1-8b-instant");
        OpenAiChat cohere=new OpenAiChat("cohere","https://api.cohere.com/compatibility/v1","command-r7b-12-2024");
        dedicatedAiChat mistral=new dedicatedAiChat("mistral","open-mistral-nemo");
        OpenAiChat openrouter = new OpenAiChat("openrouter", "https://openrouter.ai/api/v1", "meta-llama/llama-3.1-8b-instruct:free");
        OpenAiChat huggingface=new OpenAiChat("huggingface","https://router.huggingface.co/v1","Qwen/Qwen2.5-7B-Instruct");
        System.out.print("Enter the behaviour you want the Ai to have:");
        ChatMessage system = systemMessage(sc.nextLine());
        Ai[] AllAi={groq,cohere,mistral,openrouter,huggingface,gemini};
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
            System.out.println("In the below prompt, write the changes you want in the output");
        }
        if (finalOutput.length() > 1900) {
            List<ChatMessage> trimContext = new ArrayList<>();
            trimContext.add(systemMessage("Rewrite the following response so it is under 1900 characters. Preserve the core meaning. Return only the rewritten text, nothing else."));
            trimContext.add(userMessage(finalOutput));

            for (int i = AllAi.length - 1; i >= 0; i--) {
                try {
                    finalOutput = AllAi[i].Respond(trimContext).aiMessage().text();
                    break;
                } catch (Exception e) {
                    //Skip this
                }
            }
        }

        if (finalOutput.length() > 1900) {
            finalOutput = finalOutput.substring(0, 1900);
        }
        System.out.println("Dispatching output to Discord...");
        Main.sendMessage(finalOutput);
        sc.close();
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
