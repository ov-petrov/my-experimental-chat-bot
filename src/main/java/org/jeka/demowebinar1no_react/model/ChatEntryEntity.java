package org.jeka.demowebinar1no_react.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.ai.chat.messages.Message;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "text")
    private String content;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public static ChatEntryEntity fromMessage(Message message) {
        var role = message.getMessageType().getValue();
        var text = message.getText();
        return ChatEntryEntity.builder()
                .role(Role.getRole(role))
                .content(text)
                .build();
    }

    public Message toMessage() {
        return this.role.getMessage(this.content);
    }
}


