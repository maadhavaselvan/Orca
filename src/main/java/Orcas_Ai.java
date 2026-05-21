import dev.langchain4j.data.message.ChatMessage;
import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;
import java.util.Scanner;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import java.util.List;
import java.util.ArrayList;
import static dev.langchain4j.data.message.AiMessage.aiMessage;
import dev.langchain4j.service.AiServices;

import model.*;
import dispatch.*;

public class Orcas_Ai
{
    private static final Scanner sc=new Scanner(System.in);
    private static final ChatMemory memory = TokenWindowChatMemory.builder()
            .maxTokens(4000, new OpenAiTokenCountEstimator("gpt-3.5-turbo"))
            .build();
    private static String UserPrompt = "";
    private static String allUserPrompt="";
    private static int count=0;
    private static String Ai_Decision(model.Ai[] AllAi,ChatMessage system)
    {
        count++;
        System.out.print("Enter the prompt:");
        UserPrompt = sc.nextLine();
        ChatMessage user = userMessage(UserPrompt);
        String UMessage = "Question asked by user: " + UserPrompt;
        allUserPrompt+=""+count+"."+UserPrompt+"\n";
        memory.add(user);
        List<ChatMessage> history = new ArrayList<>();
        history.add(system);
        history.addAll(memory.messages());
        for (int i=0;i<AllAi.length-1;i++)
        {
            try {
                UMessage += "Answer " + (i + 1) + ": " + (AllAi[i].Respond(history).aiMessage().text());
            }
            catch(Exception e)
            {
                System.out.println("The server at "+AllAi[i].getName()+" is Overloaded, so this output is being skipped sorry for the inconvenience");
            }
        }
        List<ChatMessage> judgeContext = new ArrayList<>();//Sir might ask why you didnt use normal array
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
                        "- If it exceeds 1900 characters rewrite it so it doesnt exceed 1900 characters" +
                        "- If the user's request is purely to find a YouTube video, return the search queries related to the specific video such that video should come as the first video in the algorithm");


        judgeContext.add(system);
        judgeContext.add(userMessage(UMessage));
        String finalOutput="All Ai's are busy so no output";
        for(int i=AllAi.length-1;i>=0;i--) {
            try {
                finalOutput = AllAi[i].Respond(judgeContext).aiMessage().text();
                break;
            } catch (Exception e) {
                if(i!=0)
                    System.out.println(AllAi[i].getName()+" busy right now so judge Ai is being replaced by "+AllAi[i-1].getName());
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
        OpenAiChat groq=new OpenAiChat("groq","https://api.groq.com/openai/v1","llama-3.3-70b-versatile");
        OpenAiChat cohere=new OpenAiChat("cohere","https://api.cohere.com/compatibility/v1","command-r7b-12-2024");
        dedicatedAiChat mistral=new dedicatedAiChat("mistral","open-mistral-nemo");
        OpenAiChat openrouter = new OpenAiChat("openrouter", "https://openrouter.ai/api/v1", "meta-llama/llama-3.1-8b-instruct:free");
        OpenAiChat huggingface=new OpenAiChat("huggingface","https://router.huggingface.co/v1","Qwen/Qwen2.5-7B-Instruct");
        System.out.print("Enter the behaviour you want the Ai to have:");
        ChatMessage system = systemMessage(sc.nextLine());
        Ai[] AllAi = {huggingface, cohere, mistral, openrouter, groq, gemini};
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


        System.out.println("\n🤖 Handing over to Dispatch Agent...");
        String deliveryResult = "Failed: All APIs are out of tokens.";
        String context = "User's original requests (in order):\n" + allUserPrompt +
                "\n\nFinal Approved Content: \"" + finalOutput + "\"\n\n"+"For Multiple Youtube video pick any one of the youtube video";
        for (int i = AllAi.length - 1; i >= 0; i--) {
            try {
                DispatchAgent agent = AiServices.builder(DispatchAgent.class)
                        .chatModel(AllAi[i].getChatModel())
                        .tools(new DiscordTool(),new EmailTool(),new TelegramTool(),new YoutubeTool())
                        .build();

                deliveryResult = agent.dispatch(context);

                System.out.println("-> Successfully used [" + AllAi[i].getName() + "] for dispatch.");
                break;

            } catch (Exception e) {
                System.out.println("-> [" + AllAi[i].getName() + "] is out of tokens or busy. Switching to next AI...");
            }
        }

        System.out.println("Agent Report: " + deliveryResult);
        sc.close();
    }
}


