package com.example.project3_backend.model;

import com.example.project3_backend.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "ix_notifications_user_read", columnList = "user_id,isRead")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 1000)
    private String message;

    private boolean isRead;
    private Instant sentAt;

    @Column(length = 4000)
    private String metadata; // optional JSON-ish blob
}