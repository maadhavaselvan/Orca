package model;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.util.List;

public abstract class Ai {
    protected ChatModel Chatbot;
    private final String name;
    abstract  void buildModel();
    public ChatResponse Respond(List<ChatMessage> user)
    {
        return Chatbot.chat(ChatRequest.builder()
                .messages(user)
                .build());
    }
    Ai(String name)
    {
        this.name=name;
    }
    public String getName()
    {
        return name;
    }
    public ChatModel getChatModel() {
        return this.Chatbot;
    }
}