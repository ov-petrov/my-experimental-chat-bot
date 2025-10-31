package org.jeka.demowebinar1no_react.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "prompt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type; // e.g., 'RAG' | 'Expansion'

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "text")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}


