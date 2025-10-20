package org.jeka.demowebinar1no_react.service;

import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    private ChatEntity testChat;

    @BeforeEach
    void setUp() {
        testChat = ChatEntity.builder()
                .id(1L)
                .title("Test Chat")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_ShouldReturnAllChats() {
        // Given
        List<ChatEntity> expectedChats = Arrays.asList(testChat);
        when(chatRepository.findAll()).thenReturn(expectedChats);

        // When
        List<ChatEntity> result = chatService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Chat");
        verify(chatRepository).findAll();
    }

    @Test
    void findById_WhenChatExists_ShouldReturnChat() {
        // Given
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));

        // When
        Optional<ChatEntity> result = chatService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Chat");
        verify(chatRepository).findById(1L);
    }

    @Test
    void findById_WhenChatNotExists_ShouldReturnEmpty() {
        // Given
        when(chatRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<ChatEntity> result = chatService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(chatRepository).findById(999L);
    }

    @Test
    void create_ShouldSaveAndReturnChat() {
        // Given
        ChatEntity newChat = ChatEntity.builder()
                .title("New Chat")
                .build();
        when(chatRepository.save(any(ChatEntity.class))).thenReturn(testChat);

        // When
        ChatEntity result = chatService.create(newChat);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Chat");
        verify(chatRepository).save(any(ChatEntity.class));
    }

    @Test
    void update_WhenChatExists_ShouldUpdateAndReturnChat() {
        // Given
        ChatEntity updatedChat = ChatEntity.builder()
                .title("Updated Chat")
                .build();
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(ChatEntity.class))).thenReturn(updatedChat);

        // When
        ChatEntity result = chatService.update(1L, updatedChat);

        // Then
        assertThat(result.getTitle()).isEqualTo("Updated Chat");
        verify(chatRepository).findById(1L);
        verify(chatRepository).save(any(ChatEntity.class));
    }

    @Test
    void update_WhenChatNotExists_ShouldThrowException() {
        // Given
        ChatEntity updatedChat = ChatEntity.builder()
                .title("Updated Chat")
                .build();
        when(chatRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatService.update(999L, updatedChat))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Chat not found: 999");
        verify(chatRepository).findById(999L);
        verify(chatRepository, never()).save(any());
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        // When
        chatService.delete(1L);

        // Then
        verify(chatRepository).deleteById(1L);
    }
}
