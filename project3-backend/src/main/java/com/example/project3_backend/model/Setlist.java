package com.example.project3_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "setlists")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Setlist extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false, unique = true)
    private Concert concert;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "setlist_songs", joinColumns = @JoinColumn(name = "setlist_id"))
    @Column(name = "song_title", nullable = false)
    private List<String> songs = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "setlist_favorites", joinColumns = @JoinColumn(name = "setlist_id"))
    @Column(name = "favorite_song_title")
    private Set<String> favoriteSongs = new HashSet<>();

    private String spotifyPlaylistUrl;
    private String youtubePlaylistUrl;
}