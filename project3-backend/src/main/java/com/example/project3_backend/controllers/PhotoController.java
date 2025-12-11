package com.example.project3_backend.controllers;

import com.example.project3_backend.model.Concert;
import com.example.project3_backend.model.Photo;
import com.example.project3_backend.model.User;
import com.example.project3_backend.repository.ConcertRepository;
import com.example.project3_backend.repository.PhotoRepository;
import com.example.project3_backend.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;

    public PhotoController(PhotoRepository photoRepository,
            UserRepository userRepository,
            ConcertRepository concertRepository) {
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.concertRepository = concertRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable UUID id) {
        return photoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Photo>> getPhotosByUserId(@PathVariable UUID userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(photoRepository.findByUserId(userId));
    }

    @GetMapping("/concert/{concertId}")
    public ResponseEntity<List<Photo>> getPhotosByConcertId(@PathVariable UUID concertId) {
        if (!concertRepository.existsById(concertId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(photoRepository.findByConcertId(concertId));
    }

    @PostMapping
    public ResponseEntity<?> createPhoto(@RequestBody @NonNull PhotoReq request) {
        if (request.getUserId() == null) {
            return ResponseEntity.badRequest().body("userId is required");
        }
        if (request.getConcertId() == null) {
            return ResponseEntity.badRequest().body("concertId is required");
        }
        if (request.getUrl() == null || request.getUrl().isBlank()) {
            return ResponseEntity.badRequest().body("url is required");
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with id " + request.getUserId() + " not found");
        }

        Concert concert = concertRepository.findById(request.getConcertId()).orElse(null);
        if (concert == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Concert with id " + request.getConcertId() + " not found");
        }

        Photo photo = Photo.builder()
                .user(user)
                .concert(concert)
                .url(request.getUrl())
                .caption(request.getCaption())
                .takenAt(request.getTakenAt())
                .build();

        Photo savedPhoto = photoRepository.save(photo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPhoto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePhoto(@PathVariable @NonNull UUID id, @RequestBody PhotoReq request) {
        return photoRepository.findById(id)
                .map(photo -> {
                    if (request.getUrl() != null && !request.getUrl().isBlank()) {
                        photo.setUrl(request.getUrl());
                    }
                    if (request.getCaption() != null) {
                        photo.setCaption(request.getCaption());
                    }
                    if (request.getTakenAt() != null) {
                        photo.setTakenAt(request.getTakenAt());
                    }
                    if (request.getConcertId() != null) {
                        Concert concert = concertRepository.findById(request.getConcertId()).orElse(null);
                        if (concert == null) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("Concert not found");
                        }
                        photo.setConcert(concert);
                    }
                    return ResponseEntity.ok(photoRepository.save(photo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable UUID id) {
        if (photoRepository.existsById(id)) {
            photoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @Setter
    public static class PhotoReq {
        private UUID userId;
        private UUID concertId;
        private String url;
        private String caption;
        private Instant takenAt;
    }
}