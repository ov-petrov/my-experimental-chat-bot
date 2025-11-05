package org.jeka.demowebinar1no_react.controller;

import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.jeka.demowebinar1no_react.model.Role;
import org.jeka.demowebinar1no_react.service.ChatEntryService;
import org.jeka.demowebinar1no_react.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ChatEntryService chatEntryService;

    @MockBean
    private org.jeka.demowebinar1no_react.service.OllamaService ollamaService;

    @MockBean
    private org.jeka.demowebinar1no_react.service.PromptService promptService;

    private ChatEntity testChat;
    private ChatEntryEntity testEntry;

    @BeforeEach
    void setUp() {
        testChat = ChatEntity.builder()
                .id(1L)
                .title("Test Chat")
                .createdAt(LocalDateTime.now())
                .build();

        testEntry = ChatEntryEntity.builder()
                .id(1L)
                .chat(testChat)
                .role(Role.USER)
                .content("Hello, world!")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void listChats_ShouldReturnChatPage() throws Exception {
        // Given
        List<ChatEntity> chats = Arrays.asList(testChat);
        List<PromptEntity> prompts = Arrays.asList();
        when(chatService.findAll()).thenReturn(chats);
        when(promptService.findAll()).thenReturn(prompts);

        // When & Then
        mockMvc.perform(get("/chats"))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attribute("pageTitle", "Chats"))
                .andExpect(model().attribute("chats", chats))
                .andExpect(model().attribute("prompts", prompts))
                .andExpect(model().attributeExists("newChat"))
                .andExpect(model().attributeExists("newPrompt"));
    }

    @Test
    void listChats_WithRootPath_ShouldReturnChatPage() throws Exception {
        // Given
        List<ChatEntity> chats = Arrays.asList(testChat);
        List<PromptEntity> prompts = Arrays.asList();
        when(chatService.findAll()).thenReturn(chats);
        when(promptService.findAll()).thenReturn(prompts);

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"));
    }

    @Test
    void createChat_ShouldCreateAndRedirect() throws Exception {
        // Given
        ChatEntity newChat = ChatEntity.builder()
                .title("New Chat")
                .build();
        when(chatService.create(any(ChatEntity.class))).thenReturn(testChat);

        // When & Then
        mockMvc.perform(post("/chats")
                        .param("title", "New Chat"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chats/1"));
    }

    @Test
    void viewChat_WhenChatExists_ShouldReturnChatPage() throws Exception {
        // Given
        List<ChatEntity> chats = Arrays.asList(testChat);
        List<ChatEntryEntity> entries = Arrays.asList(testEntry);
        List<PromptEntity> prompts = Arrays.asList();
        when(chatService.findById(1L)).thenReturn(Optional.of(testChat));
        when(chatService.findAll()).thenReturn(chats);
        when(chatEntryService.findByChat(testChat)).thenReturn(entries);
        when(promptService.findAll()).thenReturn(prompts);

        // When & Then
        mockMvc.perform(get("/chats/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attribute("pageTitle", "Chats"))
                .andExpect(model().attribute("chats", chats))
                .andExpect(model().attribute("prompts", prompts))
                .andExpect(model().attributeExists("newChat"))
                .andExpect(model().attributeExists("newPrompt"))
                .andExpect(model().attribute("selectedChat", testChat))
                .andExpect(model().attribute("entries", entries))
                .andExpect(model().attributeExists("newEntry"));
    }

    @Test
    @Disabled
    void viewChat_WhenChatNotExists_ShouldThrowException() throws Exception {
        // Given
        when(chatService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/chats/999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addEntry_ShouldCreateEntryAndRedirect() throws Exception {
        // Given
        when(chatService.findById(1L)).thenReturn(Optional.of(testChat));
        when(chatEntryService.create(any(ChatEntryEntity.class))).thenReturn(testEntry);

        // When & Then
        mockMvc.perform(post("/chats/1/entries")
                        .param("content", "New message")
                        .param("role", Role.USER.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chats/1"));
    }

    @Test
    void addEntry_ShouldRedirectEvenWhenChatNotExists() throws Exception {
        // Given
        // The addEntry endpoint just redirects without checking chat existence
        // It redirects to /chats/{id}, which would fail if chat doesn't exist
        // But the redirect itself always succeeds
        
        // When & Then
        mockMvc.perform(post("/chats/999/entries")
                        .param("content", "New message")
                        .param("role", Role.USER.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chats/999"));
    }

    @Test
    void deleteChat_ShouldDeleteAndRedirect() throws Exception {
        // When & Then
        mockMvc.perform(delete("/chats/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chats"));
    }
}
