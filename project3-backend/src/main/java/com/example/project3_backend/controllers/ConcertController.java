package com.example.project3_backend.controllers;

import com.example.project3_backend.model.Concert;
import com.example.project3_backend.model.Photo;
import com.example.project3_backend.model.User;
import com.example.project3_backend.repository.ConcertRepository;
import com.example.project3_backend.repository.UserRepository;
import com.example.project3_backend.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;

    public ConcertController(ConcertRepository concertRepository, UserRepository userRepository) {
        this.concertRepository = concertRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Concert>> getConcerts() {
        List<Concert> concerts = concertRepository.findAll();
        return ResponseEntity.ok(concerts);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Concert> getConcertById(@PathVariable UUID id) {
        return ResponseEntity.ok(concertRepository.findById(id).orElse(null));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Concert>> getConcertsByUserId(@PathVariable UUID userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(concertRepository.findByUserId(userId));
    }
    @PostMapping
    public ResponseEntity<?> createConcert(@RequestBody @NonNull ConcertController.ConcertReq request) {
        if (!userRepository.existsById(request.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with id " + request.getUserId() + " not found");
        }
        User user = userRepository.findById(request.getUserId()).orElse(null);

        Concert concert = Concert.builder()
                .user(user)
                .artist(request.getArtist())
                .tourName(request.getTourName())
                .venue(request.getVenue())
                .city(request.getCity())
                .country(request.getCountry())
                .genre(request.getGenre())
                .dateTime(request.getDateTime())
                .build();
        Concert savedConcert = concertRepository.save(concert);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedConcert);
    }

        @PutMapping("/{id}")
        public ResponseEntity<?> updateConcert (@PathVariable @NonNull UUID id, @RequestBody ConcertReq request){
        return concertRepository.findById(id)
                .map(concert ->{
                    if(request.getArtist() != null) {
                        concert.setArtist(request.getArtist());
                    }
                    if (request.getTourName() != null) {
                        concert.setTourName(request.getTourName());
                    }
                    if (request.getVenue() != null) {
                        concert.setVenue(request.getVenue());
                    }
                    if (request.getCity() != null) {
                        concert.setCity(request.getCity());
                    }
                    if (request.getCountry() != null) {
                        concert.setCountry(request.getCountry());
                    }
                    if (request.getGenre() != null) {
                        concert.setGenre(request.getGenre());
                    }
                    if (request.getDateTime() != null) {
                        concert.setDateTime(request.getDateTime());
                    }
                    if(request.getUserId() != null){
                        User user = userRepository.findById(request.getUserId()).orElse(null);
                        if(user == null){
                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("User not found");

                        }
                        concert.setUser(user);
                    }
                    return ResponseEntity.ok(concertRepository.save(concert));

                }).orElse(ResponseEntity.notFound().build());

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert (@PathVariable UUID id) {
        if (concertRepository.existsById(id)) {
        concertRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    } else {
        return ResponseEntity.notFound().build();
    }
}
@GetMapping("/{concertId}/photos")
public ResponseEntity<List<Photo>> getPhotos(@PathVariable UUID concertId) {
        return concertRepository.findById(concertId)
                .map(concert -> ResponseEntity.ok(concert.getPhotos()))
                .orElse(ResponseEntity.notFound().build());
}
@PostMapping("/{concertId}/photos")
public ResponseEntity<?> addPhototoConcert(@PathVariable UUID concertId, @RequestBody PhotoReq request) {
        return concertRepository.findById(concertId)
                .map(concert ->{
                    Photo photo = Photo.builder()
                            .concert(concert)
                            .url(request.getUrl())
                            .caption(request.getCaption())
                            .build();
                    concert.getPhotos().add(photo);
                    concertRepository.save(concert);
                    return ResponseEntity.ok(photo);
                        })
                .orElse(ResponseEntity.notFound().build());

}
@DeleteMapping("/{concertId}/photos/{photoId}")
public ResponseEntity<?> deletePhoto(@PathVariable UUID concertId, @PathVariable UUID photoId) {
        return concertRepository.findById(concertId)
                .map(concert -> {
                    concert.getPhotos().removeIf(photo -> photo.getId().equals(photoId));
                    concertRepository.save(concert);
                    return ResponseEntity.noContent().build();
                }).orElse(ResponseEntity.notFound().build());
}

    @Getter
    @Setter
    public static class ConcertReq{
        public UUID userId;
        public String city;
        public String country;
        public String artist;
        public LocalDateTime dateTime;
        public String venue;
        public String tourName;
        public String genre;
    }
    @Getter
    @Setter
    public static class PhotoReq{
        public String url;
        public String caption;

    }

}
