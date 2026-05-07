<img width="700" height="350" alt="image" src="https://github.com/user-attachments/assets/bbf14033-91f3-435e-9552-68fd0b7125e0" />


🐋 Orca AI Arena
Orca is a multi-model AI pipeline that generates, synthesizes, and dispatches content across various platforms. It uses an Arena Strategy where five different AI models compete to generate the best answer, followed by a "Judge AI" that combines the results into a single, high-quality response.

🚀 Key Features
Multi-Model Synthesis: Leverages Gemini, Mistral, Llama 3 (via Groq/OpenRouter), and Qwen (via HuggingFace) simultaneously.

Intelligent Dispatch Agent: An automated agent analyzes the final output and decides whether to send it as an Email or a Discord message based on your original request.

Self-Healing Fallback: If one AI model hits a rate limit or is busy, the system automatically cascades to the next available model to prevent crashes.

Robust Data Handling: Built-in logic to fix common AI mistakes, such as malformed email lists or character limits.

🛠️ Setup
Clone the Repository:

Bash
git clone https://github.com/maadhavaselvan/Orca.git
cd Orca
Environment Variables:
To run this project, you must set up the following keys in your environment or VS Code launch.json:

GOOGLE_API_KEY, MISTRAL_API_KEY, GROQ_API_KEY, COHERE_API_KEY, HUGGINGFACE_API_KEY

gmail (Your sender address)

password (Your App Password)

discord (Your Webhook URL)

Build and Run:
This project uses Maven. You can run it directly through VS Code or via terminal:

Bash
mvn compile exec:java -Dexec.mainClass="Orcas_Ai"
📖 How to Use
Define Behavior: Tell the AI how you want it to act (e.g., "You are a helpful study assistant").

Enter Prompt: Ask your question (e.g., "Explain Quantum Physics and email it to my friend").

Review & Approve: View the synthesized response. If you're happy, type yes.

Automatic Dispatch: The Agent will detect the email request and handle the delivery automatically!
