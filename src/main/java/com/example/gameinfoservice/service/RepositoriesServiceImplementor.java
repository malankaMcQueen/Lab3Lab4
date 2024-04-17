package com.example.gameinfoservice.service;

import com.example.gameinfoservice.model.Game;
import com.example.gameinfoservice.model.Genre;
import com.example.gameinfoservice.repository.GameRepository;
import com.example.gameinfoservice.repository.GenreRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RepositoriesServiceImplementor implements RepositoriesService {
  private final GameRepository gameRepository;
  private final GenreRepository genreRepository;

  public RepositoriesServiceImplementor(GameRepository gameRepository,
                                        GenreRepository genreRepository) {
    this.gameRepository = gameRepository;
    this.genreRepository = genreRepository;
  }

  @Override
  public void saveGame(Game game) {
    gameRepository.save(game);
  }

  @Override
  public void saveGenre(Genre genre) {
    genreRepository.save(genre);
  }

  @Override
  public Game findGameById(Long id) {
    return gameRepository.findGameById(id);
  }

  @Override
  public Game findByRawgId(String rawgId) {
    return gameRepository.findByRawgId(rawgId);
  }

  @Override
  public Game findGameByName(String name) {
    return gameRepository.findGameByName(name);
  }

  @Override
  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }

  @Override
  public Genre findGenreByName(String name) {
    return genreRepository.findByName(name);
  }

  @Override
  public void deleteGame(Game game) {
    gameRepository.delete(game);
  }

  @Override
  public void deleteGenre(Genre genre) {
    genreRepository.delete(genre);
  }


}
