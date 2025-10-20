package org.jeka.demowebinar1no_react.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeka.demowebinar1no_react.dto.ollama.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {

    private final WebClient webClient;


    @Value("${spring.ai.ollama.chat.model:gemma3:4b-it-q4_K_M}")
    private String chatModel;

    /**
     * Send a chat message to Ollama and get AI response
     */
    public Mono<String> chat(String message) {
        log.info("Sending message to Ollama: {}", message);
        
        OllamaMessage userMessage = OllamaMessage.builder()
                .role("user")
                .content(message)
                .build();

        OllamaChatRequest request = OllamaChatRequest.builder()
                .model(chatModel)
                .messages(List.of(userMessage))
                .stream(false)
                .options(OllamaOptions.builder()
                        .temperature(0.7)
                        .topP(0.9)
                        .maxTokens(1000)
                        .build())
                .build();

        return webClient.post()
                .uri("/api/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaChatResponse.class)
                .map(response -> {
                    log.debug("Ollama response: {}", response);
                    return response.getMessage().getContent();
                })
                .timeout(Duration.ofSeconds(30))
                .doOnError(error -> log.error("Error calling Ollama: {}", error.getMessage()))
                .onErrorReturn("Sorry, I'm having trouble connecting to the AI service right now.");
    }

    /**
     * Check if Ollama service is available
     */
    public Mono<Boolean> isAvailable() {
        return webClient.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(OllamaModelsResponse.class)
                .map(response -> true)
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(false);
    }

    /**
     * Get list of available models from Ollama
     */
    public Mono<OllamaModelsResponse> getModels() {
        return webClient.get()
                .uri("/api/tags")
                .retrieve()
                .bodyToMono(OllamaModelsResponse.class)
                .timeout(Duration.ofSeconds(10));
    }

    /**
     * Get models as a simple list of model names
     */
    public Mono<List<String>> getModelNames() {
        return getModels()
                .map(response -> response.getModels().stream()
                        .map(OllamaModel::getName)
                        .toList());
    }
}
