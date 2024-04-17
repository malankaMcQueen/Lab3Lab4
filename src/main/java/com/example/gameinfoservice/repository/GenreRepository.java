package com.example.gameinfoservice.repository;

import com.example.gameinfoservice.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
  Genre findByName(String name);
}
