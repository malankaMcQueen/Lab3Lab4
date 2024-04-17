package com.example.gameinfoservice.repository;

import com.example.gameinfoservice.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
  Game findByRawgId(String rawgId);
  Game findGameById(Long id);
  Game findGameByName(String name);
}
