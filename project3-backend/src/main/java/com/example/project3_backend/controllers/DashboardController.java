package com.example.project3_backend.controllers;

import com.example.project3_backend.model.Concert;
import com.example.project3_backend.repository.ConcertRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final ConcertRepository concertRepository;
    public DashboardController(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<DashboardReq> getUser(@PathVariable UUID userId) {
        List<Concert> concerts = concertRepository.findByUserId(userId);
        DashboardReq resp = new DashboardReq();
        if(concerts.isEmpty()) {
            resp.setTotalConcerts(0);
            resp.setTopArtists(new HashMap<>());
            resp.setLocations(new HashMap<>());
            resp.setGenres(new HashMap<>());
            resp.setTimeline(new HashMap<>());
            resp.setFirstConcert(null);
            resp.setLastConcert(null);
            return ResponseEntity.ok(resp);
        }
        resp.setTotalConcerts(concerts.size());

        Map<String, Integer> artists = new HashMap<>();
        for(Concert concert : concerts) {
            String artist = concert.getArtist();
            if(artist != null) {
                artists.put(artist, artists.getOrDefault(artist, 0) + 1);


            }
        }
        resp.setTopArtists(artists);

    Map<String, Integer> genres = new HashMap<>();
    for(Concert concert : concerts) {
        String genreName = concert.getGenre();
        if(genreName != null) {
            genres.put(genreName, genres.getOrDefault(genreName, 0) + 1);

        }
    }
    resp.setGenres(genres);

    Map<String, Integer> timeline = new HashMap<>();
    for(Concert concert : concerts) {
        LocalDateTime dateTime = concert.getDateTime();
        if(dateTime != null) {
            String month = dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String monthyear = month + " " + dateTime.getYear();
            timeline.put(monthyear, timeline.getOrDefault(monthyear, 0) + 1);

        }
    }
    resp.setTimeline(timeline);

    Map<String, Integer> location = new HashMap<>();
    for(Concert concert : concerts) {
        String city = concert.getCity();
        if(city != null) {
            location.put(city, location.getOrDefault(city, 0) + 1);

        }
    }
    resp.setLocations(location);

    concerts.sort(Comparator.comparing(Concert::getDateTime));
    resp.setFirstConcert(concerts.get(0));
    resp.setLastConcert(concerts.get(concerts.size() - 1));
    return ResponseEntity.ok(resp);
    }

    @Getter
    @Setter
    public static class DashboardReq{
        private int totalConcerts;
        private Map<String, Integer> topArtists;
        private Map<String, Integer> genres;
        private Map<String, Integer> timeline;
        private Map<String, Integer> locations;
        private Concert firstConcert;
        private Concert lastConcert;

    }
}
