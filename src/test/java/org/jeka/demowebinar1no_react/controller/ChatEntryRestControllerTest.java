package org.jeka.demowebinar1no_react.controller;

import io.restassured.http.ContentType;
import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.Role;
import org.jeka.demowebinar1no_react.service.ChatEntryService;
import org.jeka.demowebinar1no_react.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.ai.ollama.enabled=false",
                "spring.ai.vectorstore.pgvector.enabled=false"
        })
class ChatEntryRestControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ChatEntryService chatEntryService;

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
    void listEntries_WhenChatExists_ShouldReturnEntries() {
        // Given
        List<ChatEntryEntity> entries = Arrays.asList(testEntry);
        when(chatService.findById(1L)).thenReturn(Optional.of(testChat));
        when(chatEntryService.findByChat(testChat)).thenReturn(entries);

        // When & Then
        given()
                .port(port)
                .when()
                .get("/api/chats/1/entries")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].content", is("Hello, world!"))
                .body("[0].role", is(Role.USER.name()));
    }

    @Test
    void listEntries_WhenChatNotExists_ShouldReturn404() {
        // Given
        when(chatService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        given()
                .port(port)
                .when()
                .get("/api/chats/999/entries")
                .then()
                .statusCode(404);
    }

    @Test
    void createEntry_WhenChatExists_ShouldCreateAndReturnEntry() {
        // Given
        ChatEntryEntity newEntry = ChatEntryEntity.builder()
                .role(Role.USER)
                .content("New message")
                .build();
        when(chatService.findById(1L)).thenReturn(Optional.of(testChat));
        when(chatEntryService.create(any(ChatEntryEntity.class))).thenReturn(testEntry);

        // When & Then
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(newEntry)
                .when()
                .post("/api/chats/1/entries")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("content", is("Hello, world!"))
                .body("role", is(Role.USER.name()))
                .header("Location", containsString("/api/chats/1/entries/1"));
    }

    @Test
    void createEntry_WhenChatNotExists_ShouldReturn500() {
        // Given
        ChatEntryEntity newEntry = ChatEntryEntity.builder()
                .role(Role.USER)
                .content("New message")
                .build();
        when(chatService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        // .orElseThrow() throws NoSuchElementException, resulting in 500 error
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(newEntry)
                .when()
                .post("/api/chats/999/entries")
                .then()
                .statusCode(500);
    }

    @Test
    void getEntry_WhenEntryExists_ShouldReturnEntry() {
        // Given
        when(chatEntryService.findById(1L)).thenReturn(Optional.of(testEntry));

        // When & Then
        given()
                .port(port)
                .when()
                .get("/api/chats/1/entries/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("content", is("Hello, world!"))
                .body("role", is(Role.USER.name()));
    }

    @Test
    void getEntry_WhenEntryNotExists_ShouldReturn404() {
        // Given
        when(chatEntryService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        given()
                .port(port)
                .when()
                .get("/api/chats/1/entries/999")
                .then()
                .statusCode(404);
    }

    @Test
    void updateEntry_WhenEntryExists_ShouldUpdateAndReturnEntry() {
        // Given
        ChatEntryEntity updatedEntry = ChatEntryEntity.builder()
                .role(Role.ASSISTANT)
                .content("Updated message")
                .build();
        when(chatEntryService.update(eq(1L), any(ChatEntryEntity.class))).thenReturn(updatedEntry);

        // When & Then
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(updatedEntry)
                .when()
                .put("/api/chats/1/entries/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", is("Updated message"))
                .body("role", is(Role.ASSISTANT.name()));
    }

    @Test
    void deleteEntry_ShouldDeleteEntry() {
        // When & Then
        given()
                .port(port)
                .when()
                .delete("/api/chats/1/entries/1")
                .then()
                .statusCode(204);
    }
}
