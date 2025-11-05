package com.example.project3_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "concerts", indexes = {
        @Index(name = "ix_concerts_artist_date", columnList = "artist,dateTime")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Concert extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String artist;

    private String tourName;
    private String venue;
    private String city;
    private String region;
    private String country;
    private String genre;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(length = 4000)
    private String notes;

    private String ticketUrl;

    @Builder.Default
    @OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @OneToOne(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Setlist setlist;
}