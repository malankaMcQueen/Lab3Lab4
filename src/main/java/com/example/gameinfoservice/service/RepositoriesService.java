package com.example.gameinfoservice.service;

import com.example.gameinfoservice.model.Game;
import com.example.gameinfoservice.model.Genre;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface RepositoriesService {
  void saveGame(Game game);
  void saveGenre(Genre genre);
  Game findGameById(Long id);
  Game findByRawgId(String rawgId);
  Game findGameByName(String name);
  List<Game> getAllGames();
  Genre findGenreByName(String name);
  void deleteGame(Game game);
  void deleteGenre(Genre genre);
}