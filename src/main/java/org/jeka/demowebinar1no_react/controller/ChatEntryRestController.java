package org.jeka.demowebinar1no_react.controller;

import lombok.RequiredArgsConstructor;
import org.jeka.demowebinar1no_react.model.ChatEntryEntity;
import org.jeka.demowebinar1no_react.model.ChatEntity;
import org.jeka.demowebinar1no_react.service.ChatEntryService;
import org.jeka.demowebinar1no_react.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/chats/{chatId}/entries")
@RequiredArgsConstructor
public class ChatEntryRestController {

    private final ChatService chatService;
    private final ChatEntryService chatEntryService;

    @GetMapping
    public ResponseEntity<List<ChatEntryEntity>> list(@PathVariable Long chatId) {
        return chatService.findById(chatId)
                .map(chat -> ResponseEntity.ok(chatEntryService.findByChat(chat)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChatEntryEntity> create(@PathVariable Long chatId, @RequestBody ChatEntryEntity entry) {
        ChatEntity chat = chatService.findById(chatId).orElseThrow();
        entry.setId(null);
        entry.setChat(chat);
        ChatEntryEntity created = chatEntryService.create(entry);
        return ResponseEntity.created(URI.create("/api/chats/" + chatId + "/entries/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatEntryEntity> get(@PathVariable Long chatId, @PathVariable Long id) {
        return chatEntryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChatEntryEntity> update(@PathVariable Long chatId, @PathVariable Long id, @RequestBody ChatEntryEntity entry) {
        return ResponseEntity.ok(chatEntryService.update(id, entry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long chatId, @PathVariable Long id) {
        chatEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


