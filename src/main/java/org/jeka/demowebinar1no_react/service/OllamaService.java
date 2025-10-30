package org.jeka.demowebinar1no_react.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeka.demowebinar1no_react.dto.ollama.OllamaModel;
import org.jeka.demowebinar1no_react.dto.ollama.OllamaModelsResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OllamaService {

    private final ChatClient chatClient;
    private final WebClient webClient;

    /**
     * Send a chat message to Ollama and get AI response using Spring AI ChatClient
     */
    public Mono<String> chat(String message) {
        log.info("Sending message to Ollama: {}", message);

        return Mono.fromCallable(() -> {
            try {
                String response = chatClient.prompt()
                        .user(message)
                        .call()
                        .content();

                log.debug("Ollama response: {}", response);
                return response;
            } catch (Exception e) {
                log.error("Error calling Ollama: {}", e.getMessage());
                return "Sorry, I'm having trouble connecting to the AI service right now.";
            }
        });
    }

    /**
     * Synchronous version of chat for non-reactive contexts
     */
    public String chatSync(String message) {
        long startTime = System.currentTimeMillis();
        log.info("Sending message to Ollama (sync), message length: {} chars", message.length());

        try {
            String response = chatClient.prompt()
                    .user(message)
                    .call()
                    .content();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            if (response != null) {
                log.info("Ollama response received in {} ms ({} seconds), response length: {} chars",
                        duration, String.format("%.2f", duration / 1000.0), response.length());
                log.debug("Ollama response: {}", response);
            } else {
                log.warn("Ollama returned null response in {} ms", duration);
            }

            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.error("Error calling Ollama after {} ms: {}", duration, e.getMessage());
            return "Sorry, I'm having trouble connecting to the AI service right now.";
        }
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
