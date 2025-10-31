package org.jeka.demowebinar1no_react.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * Calculates and logs AI response time
     */
    public String chatSync(String message) {
        return chatSync(message, null);
    }

    /**
     * Synchronous version of chat with system prompt support
     * Calculates and logs AI response time
     */
    public String chatSync(String message, String systemPrompt) {
        long startTime = System.currentTimeMillis();
        log.info("Requesting AI response from Ollama, message length: {} chars", message.length());
        if (StringUtils.isNotBlank(systemPrompt)) {
            log.debug("Using system prompt, length: {} chars", systemPrompt.length());
        }

        try {
            var promptBuilder = chatClient.prompt();

            // Set system prompt if provided
            if (StringUtils.isNotBlank(systemPrompt)) {
                promptBuilder = promptBuilder.system(systemPrompt);
            }

            String response = promptBuilder
                    .user(message)
                    .call()
                    .content();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            double durationSeconds = duration / 1000.0;

            if (StringUtils.isNotBlank(response)) {
                log.info("AI response received successfully. Total AI processing time: {} seconds, response length: {} chars",
                        String.format("%.2f", durationSeconds), StringUtils.length(response));
                log.debug("AI response content: {}", response);
            } else {
                log.warn("Ollama returned empty response after {} seconds",
                        String.format("%.2f", durationSeconds));
            }

            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            double durationSeconds = duration / 1000.0;
            log.error("Error getting AI response after {} seconds: {}",
                    String.format("%.2f", durationSeconds), e.getMessage(), e);
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
