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
    void buildModel()
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
                System.out.println("Sorry we are working on adding support to that Ai Model");
        }
    }
}
