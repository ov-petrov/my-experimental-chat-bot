package org.jeka.demowebinar1no_react.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.jeka.demowebinar1no_react.model.Role;
import org.jeka.demowebinar1no_react.service.ChatEntryService;
import org.jeka.demowebinar1no_react.service.ChatService;
import org.jeka.demowebinar1no_react.service.OllamaService;
import org.jeka.demowebinar1no_react.service.PromptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final ChatEntryService chatEntryService;
    private final OllamaService ollamaService;
    private final PromptService promptService;

    @GetMapping({"/", "/chats"})
    public String listChats(Model model) {
        List<ChatEntity> chats = chatService.findAll();
        model.addAttribute("pageTitle", "Chats");
        model.addAttribute("chats", chats);
        model.addAttribute("newChat", new ChatEntity());
        model.addAttribute("selectedChat", null);
        model.addAttribute("entries", null);
        model.addAttribute("newEntry", new ChatEntryEntity());
        return "chat";
    }

    @PostMapping("/chats")
    public String createChat(@ModelAttribute("newChat") ChatEntity newChat) {
        ChatEntity created = chatService.create(newChat);
        return "redirect:/chats/" + created.getId();
    }

    @GetMapping("/chats/{id}")
    public String viewChat(@PathVariable Long id, Model model) {
        ChatEntity chat = chatService.findById(id).orElseThrow();
        model.addAttribute("pageTitle", "Chats");
        model.addAttribute("chats", chatService.findAll());
        model.addAttribute("newChat", new ChatEntity());
        model.addAttribute("selectedChat", chat);
        model.addAttribute("entries", chatEntryService.findByChat(chat));
        model.addAttribute("newEntry", ChatEntryEntity.builder().role(Role.USER).chat(chat).build());
        return "chat";
    }

    @PostMapping("/chats/{id}/entries")
    public String addEntry(@PathVariable Long id,
                           @ModelAttribute("newEntry") ChatEntryEntity newEntry) {
        long requestStartTime = System.currentTimeMillis();
        log.info("Processing new chat entry for chat ID: {}", id);
        
        ChatEntity chat = chatService.findById(id).orElseThrow();
        newEntry.setChat(chat);
        
        // Save user message
        chatEntryService.create(newEntry);
        log.debug("User message saved");
        
        // Get AI response if it's a user message
        if (Role.USER.equals(newEntry.getRole())) {
            try {
                log.info("Requesting AI response for user message");
                long aiStartTime = System.currentTimeMillis();

                String aiResponse = ollamaService.chatSync(newEntry.getContent());

                long aiEndTime = System.currentTimeMillis();
                long aiDuration = aiEndTime - aiStartTime;
                
                if (aiResponse != null && !aiResponse.trim().isEmpty()) {
                    ChatEntryEntity aiEntry = ChatEntryEntity.builder()
                            .chat(chat)
                            .role(Role.ASSISTANT)
                            .content(aiResponse)
                            .build();
                    chatEntryService.create(aiEntry);
                    log.info("AI response saved. Total AI processing time: {} ms ({} seconds)",
                            aiDuration, String.format("%.2f", aiDuration / 1000.0));
                }
            } catch (Exception e) {
                log.error("Error getting AI response: {}", e.getMessage());
                // Continue without AI response
            }
        }

        long requestEndTime = System.currentTimeMillis();
        long totalDuration = requestEndTime - requestStartTime;
        log.info("Request completed in {} ms ({} seconds)",
                totalDuration, String.format("%.2f", totalDuration / 1000.0));
        
        return "redirect:/chats/" + id;
    }

    @DeleteMapping("/chats/{id}")
    public String deleteChat(@PathVariable Long id) {
        chatService.delete(id);
        return "redirect:/chats";
    }

    @GetMapping("/api/ollama/status")
    @ResponseBody
    public String checkOllamaStatus() {
        try {
            Boolean isAvailable = ollamaService.isAvailable().block();
            return isAvailable ? "Ollama is available" : "Ollama is not available";
        } catch (Exception e) {
            return "Error checking Ollama status: " + e.getMessage();
        }
    }

    @GetMapping("/api/prompts/system")
    @ResponseBody
    public String getSystemPrompt() {
        try {
            Optional<PromptEntity> systemPrompt = promptService.findFirstByType("System");
            return systemPrompt.map(PromptEntity::getContent)
                    .orElse("No system prompt configured");
        } catch (Exception e) {
            return "Error getting system prompt: " + e.getMessage();
        }
    }

    @PostMapping("/api/prompts/system")
    @ResponseBody
    public String setSystemPrompt(@RequestParam String content) {
        try {
            // Find existing system prompt or create new one
            Optional<PromptEntity> existing = promptService.findFirstByType("System");
            
            PromptEntity systemPrompt = existing.orElse(PromptEntity.builder()
                    .type("System")
                    .name("System Prompt")
                    .build());
            
            systemPrompt.setContent(content);
            
            if (existing.isPresent()) {
                promptService.update(systemPrompt.getId(), systemPrompt);
            } else {
                promptService.create(systemPrompt);
            }
            
            return "System prompt updated successfully";
        } catch (Exception e) {
            return "Error setting system prompt: " + e.getMessage();
        }
    }

    /**
     * Build enhanced prompt with system prompt and conversation history
     */
    private String buildEnhancedPrompt(ChatEntity chat, String userMessage) {
        StringBuilder promptBuilder = new StringBuilder();
        
        // Add system prompt if available
        Optional<PromptEntity> systemPrompt = promptService.findFirstByType("System");
        if (systemPrompt.isPresent()) {
            promptBuilder.append("System: ").append(systemPrompt.get().getContent()).append("\n\n");
        }
        
        // Add conversation history (last 10 messages for context)
        List<ChatEntryEntity> recentEntries = chatEntryService.findByChat(chat);
        int startIndex = Math.max(0, recentEntries.size() - 10);
        
        for (int i = startIndex; i < recentEntries.size(); i++) {
            ChatEntryEntity entry = recentEntries.get(i);
            promptBuilder.append(entry.getRole().getRole().toUpperCase())
                    .append(": ")
                    .append(entry.getContent())
                    .append("\n");
        }
        
        // Add current user message
        promptBuilder.append("USER: ").append(userMessage);
        
        return promptBuilder.toString();
    }
}


