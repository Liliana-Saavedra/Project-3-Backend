package com.example.project3_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "photos", indexes = {
        @Index(name = "ix_photos_concert", columnList = "concert_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Photo extends BaseEntity {

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Concert concert;

    @JsonIgnore
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "setlist_id")
    private Setlist setlist;

    @Column(nullable = false)
    private String url; // e.g., Supabase/Cloudinary URL

    private String caption;
    private Instant takenAt;
}
