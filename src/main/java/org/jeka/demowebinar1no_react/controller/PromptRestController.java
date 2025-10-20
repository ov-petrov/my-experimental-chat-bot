package org.jeka.demowebinar1no_react.controller;

import lombok.RequiredArgsConstructor;
import org.jeka.demowebinar1no_react.model.PromptEntity;
import org.jeka.demowebinar1no_react.service.PromptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class PromptRestController {

    private final PromptService promptService;

    @GetMapping
    public List<PromptEntity> list() {
        return promptService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromptEntity> get(@PathVariable Long id) {
        return promptService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PromptEntity> create(@RequestBody PromptEntity prompt) {
        PromptEntity created = promptService.create(prompt);
        return ResponseEntity.created(URI.create("/api/prompts/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromptEntity> update(@PathVariable Long id, @RequestBody PromptEntity prompt) {
        return ResponseEntity.ok(promptService.update(id, prompt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


