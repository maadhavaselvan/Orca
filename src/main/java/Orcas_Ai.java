// LangChain4j Core
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;

// Static imports for message helpers
import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;

// Google Gemini
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
// OpenAI-compatible (used for Groq)
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.workersai.WorkersAiChatModel;
import java.util.Scanner;
public class Orcas_Ai
{
    public static void main(String[] args) {

        ChatModel gemini = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GOOGLE_API_KEY"))
                .modelName("gemini-2.5-flash")
                .build();
        ChatModel groq = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(System.getenv("GROQ_API_KEY"))
                .modelName("llama-3.1-8b-instant")
                .build();
        ChatModel cohere = OpenAiChatModel.builder()
                .baseUrl("https://api.cohere.com/compatibility/v1")
                .apiKey(System.getenv("COHERE_API_KEY"))
                .modelName("command-r7b-12-2024")
                .build();
        ChatModel mistral = MistralAiChatModel.builder()
                .apiKey(System.getenv("MISTRAL_API_KEY"))
                .modelName("open-mistral-nemo")
                .build();
        ChatModel cloudflare = WorkersAiChatModel.builder()
                .accountId(System.getenv("CLOUDFLARE_ACCOUNT_ID"))
                .apiToken(System.getenv("CLOUDFLARE_API_TOKEN"))
                .modelName("@cf/meta/llama-3.1-8b-instruct")
                .build();
        ChatModel huggingface = OpenAiChatModel.builder()
                .apiKey(System.getenv("HUGGINGFACEHUB_API_TOKEN"))
                .baseUrl("https://router.huggingface.co/v1") // <-- This is the new, correct endpoint
                .modelName("Qwen/Qwen2.5-7B-Instruct")
                .build();
        ChatMessage system = systemMessage("Be kind ");
        ChatMessage user = userMessage("who is Professor Dr H K Sardana in one line" );
        ChatResponse geminiResponse = gemini.chat(ChatRequest.builder()
                .messages(system,user)
                .build());
        ChatResponse groqResponse = groq.chat(ChatRequest.builder()
                .messages(system, user)
                .build());
        ChatResponse cohereResponse = cohere.chat(ChatRequest.builder()
                .messages(system, user)
                .build());
        ChatResponse mistralResponse = mistral.chat(ChatRequest.builder()
                .messages(system, user)
                .build());
        ChatResponse cloudflareResponse = cloudflare.chat(ChatRequest.builder()
                .messages(system, user)
                .build());
        ChatResponse huggingfaceResponse = huggingface.chat(ChatRequest.builder()
                .messages(system, user)
                .build());
        System.out.println("=================================================================Gemini================================================================");
        System.out.println(geminiResponse.aiMessage().text());
        System.out.println("=================================================================Groq=================================================================");
        System.out.println(groqResponse.aiMessage().text());
        System.out.println("================================================================Cohere================================================================");
        System.out.println(cohereResponse.aiMessage().text());
        System.out.println("================================================================Mistral================================================================");
        System.out.println(mistralResponse.aiMessage().text());
        System.out.println("================================================================Cloud Flare================================================================");
        System.out.println(cloudflareResponse.aiMessage().text());
        System.out.println("================================================================Hugging Face================================================================");
        System.out.println(huggingfaceResponse.aiMessage().text());
        System.out.println("=======================================================================================================================================");

    }
}