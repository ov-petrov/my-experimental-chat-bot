package org.jeka.demowebinar1no_react.repository;

import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatRepository chatRepository;

    private ChatEntity testChat;

    @BeforeEach
    void setUp() {
        testChat = ChatEntity.builder()
                .title("Test Chat")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_ShouldPersistChat() {
        // When
        ChatEntity saved = chatRepository.save(testChat);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Chat");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_WhenChatExists_ShouldReturnChat() {
        // Given
        ChatEntity saved = entityManager.persistAndFlush(testChat);

        // When
        Optional<ChatEntity> found = chatRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Chat");
    }

    @Test
    void findById_WhenChatNotExists_ShouldReturnEmpty() {
        // When
        Optional<ChatEntity> found = chatRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllChats() {
        // Given
        ChatEntity chat1 = ChatEntity.builder()
                .title("Chat 1")
                .createdAt(LocalDateTime.now())
                .build();
        ChatEntity chat2 = ChatEntity.builder()
                .title("Chat 2")
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(chat1);
        entityManager.persistAndFlush(chat2);

        // When
        List<ChatEntity> allChats = chatRepository.findAll();

        // Then
        assertThat(allChats).hasSize(2);
        assertThat(allChats).extracting(ChatEntity::getTitle)
                .containsExactlyInAnyOrder("Chat 1", "Chat 2");
    }

    @Test
    void delete_ShouldRemoveChat() {
        // Given
        ChatEntity saved = entityManager.persistAndFlush(testChat);
        Long chatId = saved.getId();

        // When
        chatRepository.deleteById(chatId);
        entityManager.flush();

        // Then
        Optional<ChatEntity> found = chatRepository.findById(chatId);
        assertThat(found).isEmpty();
    }
}
