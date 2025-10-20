package org.jeka.demowebinar1no_react.controller;

import io.restassured.http.ContentType;
import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.jeka.demowebinar1no_react.service.PromptService;
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
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PromptRestControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private PromptService promptService;

    private PromptEntity testPrompt;

    @BeforeEach
    void setUp() {
        testPrompt = PromptEntity.builder()
                .id(1L)
                .type("RAG")
                .name("Test Prompt")
                .content("You are a helpful assistant")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void listPrompts_ShouldReturnAllPrompts() {
        // Given
        List<PromptEntity> prompts = Arrays.asList(testPrompt);
        when(promptService.findAll()).thenReturn(prompts);

        // When & Then
        given()
                .port(port)
                .when()
                .get("/api/prompts")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].name", is("Test Prompt"))
                .body("[0].type", is("RAG"));
    }

    @Test
    void getPrompt_WhenPromptExists_ShouldReturnPrompt() {
        // Given
        when(promptService.findById(1L)).thenReturn(Optional.of(testPrompt));

        // When & Then
        given()
                .port(port)
                .when()
                .get("/api/prompts/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("name", is("Test Prompt"))
                .body("type", is("RAG"))
                .body("content", is("You are a helpful assistant"));
    }

    @Test
    void getPrompt_WhenPromptNotExists_ShouldReturn404() {
        // Given
        when(promptService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        given()
                .port(port)
                .when()
                .get("/api/prompts/999")
                .then()
                .statusCode(404);
    }

    @Test
    void createPrompt_ShouldCreateAndReturnPrompt() {
        // Given
        PromptEntity newPrompt = PromptEntity.builder()
                .type("RAG")
                .name("New Prompt")
                .content("New content")
                .build();
        when(promptService.create(any(PromptEntity.class))).thenReturn(testPrompt);

        // When & Then
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(newPrompt)
                .when()
                .post("/api/prompts")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("name", is("Test Prompt"))
                .header("Location", containsString("/api/prompts/1"));
    }

    @Test
    void updatePrompt_WhenPromptExists_ShouldUpdateAndReturnPrompt() {
        // Given
        PromptEntity updatedPrompt = PromptEntity.builder()
                .type("Expansion")
                .name("Updated Prompt")
                .content("Updated content")
                .build();
        when(promptService.update(eq(1L), any(PromptEntity.class))).thenReturn(updatedPrompt);

        // When & Then
        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(updatedPrompt)
                .when()
                .put("/api/prompts/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", is("Updated Prompt"))
                .body("type", is("Expansion"));
    }

    @Test
    void deletePrompt_ShouldDeletePrompt() {
        // When & Then
        given()
                .port(port)
                .when()
                .delete("/api/prompts/1")
                .then()
                .statusCode(204);
    }
}
