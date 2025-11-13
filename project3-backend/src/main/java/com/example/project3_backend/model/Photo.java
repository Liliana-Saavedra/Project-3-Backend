package com.example.project3_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "photos", indexes = {
        @Index(name = "ix_photos_concert", columnList = "concert_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Photo extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Concert concert;

    @Column(nullable = false)
    private String url;      // e.g., Supabase/Cloudinary URL

    private String caption;
    private Instant takenAt;
}