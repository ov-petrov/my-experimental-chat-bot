package org.jeka.demowebinar1no_react.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeka.demowebinar1no_react.repository.ChatRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.ollama.chat.options.top-p:0.9}")
    private Double topP;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(ollamaBaseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatRepository chatRepository) {
        OllamaOptions options = OllamaOptions.builder()
                .temperature(temperature)
                .topP(topP)
                .build();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(conversationAdvisor(chatRepository))
                .defaultOptions(options)
                .build();
    }

    private Advisor conversationAdvisor(ChatRepository chatRepository) {
        return MessageChatMemoryAdvisor.builder(getChatMemory(chatRepository)).build();
    }

    private ChatMemory getChatMemory(ChatRepository chatRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(2)
                .chatMemoryRepository(chatRepository)
                .build();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
