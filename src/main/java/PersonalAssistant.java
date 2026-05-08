import dev.langchain4j.service.SystemMessage;

public interface PersonalAssistant {
    @SystemMessage("You are a helpful personal assistant. You have tools to send emails and send Discord messages. If the user asks you to do either, use the tools provided.")
    String chat(String userMessage);
}