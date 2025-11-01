package org.jeka.demowebinar1no_react.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_prompt_id")
    private PromptEntity systemPrompt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    private List<ChatEntryEntity> entries = new ArrayList<>();

    public void addEntry(ChatEntryEntity chatEntry) {
        chatEntry.setChat(this);
        this.entries.add(chatEntry);
    }

}
