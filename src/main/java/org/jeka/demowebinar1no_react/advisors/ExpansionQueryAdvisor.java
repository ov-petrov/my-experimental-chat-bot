package org.jeka.demowebinar1no_react.advisors;

import lombok.Builder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;

@Builder
public class ExpansionQueryAdvisor implements BaseAdvisor {
    private int order;
    private ChatClient chatClient;

    public static ExpansionQueryAdvisorBuilder builder(ChatModel chatModel) {
        var chatClient = ChatClient.builder(chatModel)
                .defaultOptions(OllamaOptions.builder().temperature(0.0).topK(1).topP(0.1).repeatPenalty(1.0).build())
                .build();
        return new ExpansionQueryAdvisorBuilder().chatClient(chatClient);
    }


    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        var userMessage = chatClientRequest.prompt().getUserMessage();


        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {

        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}