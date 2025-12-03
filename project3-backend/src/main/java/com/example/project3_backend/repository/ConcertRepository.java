package com.example.project3_backend.repository;

import com.example.project3_backend.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConcertRepository extends JpaRepository<Concert, UUID> {
    List<Concert> findByUserId(UUID userId);
}

