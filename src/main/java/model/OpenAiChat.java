package model;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class OpenAiChat extends Ai
{
    private final String baseUrl;
    private final String modelName;
    public OpenAiChat(String name,String baseUrl,String modelName)
    {
        super(name);
        this.baseUrl=baseUrl;
        this.modelName=modelName;
        buildModel();
    }
    void buildModel()
    {
        this.Chatbot= OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(System.getenv(getName().toUpperCase()+"_API_KEY"))
                .modelName(modelName)
                .build();
    }
}
