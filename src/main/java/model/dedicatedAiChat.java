package model;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;

public class dedicatedAiChat extends Ai
{
    private final String modelName;
    public dedicatedAiChat(String name,String modelName)
    {
        super(name);
        this.modelName=modelName;
        buildModel();
    }
    @Override
    void buildModel() throws InvalidAiException
    {
        switch (getName().toLowerCase()) {
            case "gemini":
            case "google":
                this.Chatbot = GoogleAiGeminiChatModel.builder()
                        .apiKey(System.getenv("GOOGLE_API_KEY"))
                        .modelName(modelName)
                        .build();
                break;
            case "mistral":
                this.Chatbot = MistralAiChatModel.builder()
                        .apiKey(System.getenv("MISTRAL_API_KEY"))
                        .modelName(modelName)
                        .build();
                break;
            default:
                throw new InvalidAiException("Sorry we have not added support to that Ai");
        }
    }
}
