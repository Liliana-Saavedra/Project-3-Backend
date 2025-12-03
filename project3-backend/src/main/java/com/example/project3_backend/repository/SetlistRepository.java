package com.example.project3_backend.repository;

import com.example.project3_backend.model.Concert;
import com.example.project3_backend.model.Setlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SetlistRepository extends JpaRepository<Setlist, Long> {
    List<Setlist> findByConcertId(UUID concertId);
}

