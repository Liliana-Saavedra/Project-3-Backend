package com.example.project3_backend.controllers;

import com.example.project3_backend.model.Concert;
import com.example.project3_backend.model.Setlist;
import com.example.project3_backend.model.User;
import com.example.project3_backend.repository.ConcertRepository;
import com.example.project3_backend.repository.SetlistRepository;
import com.example.project3_backend.repository.UserRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/setlists")
public class SetlistController {
    private final SetlistRepository setlistRepository;
    private final ConcertRepository concertRepository;

    public SetlistController(SetlistRepository setlistRepository, ConcertRepository concertRepository) {
        this.setlistRepository = setlistRepository;
        this.concertRepository = concertRepository;

    }

    @GetMapping
    public ResponseEntity<List<Setlist>> getAllSetlists() {
        return ResponseEntity.ok(setlistRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Setlist> getSetlistById(@PathVariable UUID id) {
        return setlistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/concert/{concertId}")
    public ResponseEntity<Setlist> getSetlistbyConcertId(@PathVariable UUID concertId) {
        List<Setlist> list = setlistRepository.findByConcertId(concertId);
        if (list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list.get(0));
    }

    @PostMapping
    public ResponseEntity<?> createSetlist(@RequestBody @NonNull SetlistReq request) {
        Concert concert = concertRepository.findById(request.getConcertId()).orElse(null);
        if (concert == null) {
            return ResponseEntity.notFound().build();
        }
        Setlist setlist = Setlist.builder()
                .user(concert.getUser())
                .concert(concert)
                .songs(request.getSongs() != null ? request.getSongs() : new ArrayList<>())
                .favoriteSongs(request.getFavoriteSongs() != null ? request.getFavoriteSongs() : new HashSet<>())
                .build();
        Setlist saved = setlistRepository.save(setlist);
        return ResponseEntity.ok(saved);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSetlist(@PathVariable @org.springframework.lang.NonNull UUID id,
            @RequestBody SetlistReq request) {
        return setlistRepository.findById(id)
                .map(setlist -> {

                    if (request.getSongs() != null) {
                        setlist.setSongs(request.getSongs());
                    }
                    if (request.getFavoriteSongs() != null) {
                        setlist.setFavoriteSongs(request.getFavoriteSongs());
                    }

                    setlistRepository.save(setlist);
                    return ResponseEntity.ok(setlist);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetlist(@PathVariable @org.springframework.lang.NonNull UUID id) {
        if (setlistRepository.existsById(id)) {
            setlistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Setter
    @Getter
    public static class SetlistReq {
        private UUID concertId;
        private List<String> songs;
        private Set<String> favoriteSongs;
    }

}