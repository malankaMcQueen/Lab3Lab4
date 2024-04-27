package com.example.gameinfoservice.service;

import com.example.gameinfoservice.aspect.AspectAnnotation;
import com.example.gameinfoservice.cache.CacheManager;
import com.example.gameinfoservice.exception.BadRequestException;
import com.example.gameinfoservice.exception.ResourceNotFoundException;
import com.example.gameinfoservice.model.Game;
import com.example.gameinfoservice.model.Genre;
import com.example.gameinfoservice.repository.GameRepository;
import com.example.gameinfoservice.repository.GenreRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GameService {

    private GameRepository gameRepository;
    private GenreRepository genreRepository;
    private CacheManager cacheManager;
    private static final String GAME = "gameName_";

    @AspectAnnotation
    public List<Game> getAllGames() {
        List<Game> gameList = gameRepository.findAll();
        for (Game game: gameList) {
            cacheManager.put(GAME + game.getName(), game);
        }
        return gameList;
    }

    @AspectAnnotation
    public boolean saveGame(final Game game) {
        if (gameRepository.findGameByName(game.getName()) != null) {
            throw new BadRequestException("Game with name " + game.getName() + "already exist");
        }
        cacheManager.put(GAME + game.getName(), game);
        gameRepository.save(game);
        return true;
    }

    @AspectAnnotation
    public boolean changeName(final Long id, final String newName) {
        Game game = gameRepository.findGameById(id);
        if (game == null) {
            throw new ResourceNotFoundException("Game with id " + id.toString() + "not found");
        }
        game.setName(newName);
        cacheManager.clear();
        gameRepository.save(game);
        return true;
    }

    @AspectAnnotation
    public Game putGameToGenre(final Long id, final String name) {
        Genre genre = genreRepository.findGenreByName(name);
        if (genre == null) {
            throw new ResourceNotFoundException("Genre with name " + name + "not found ");
        }
        List<Game> games = genre.getGames();
        for (Game game : games) {
            if (Objects.equals(game.getId(), id)) {
                throw new BadRequestException("This game is already exists in this type of genre");
            }
        }
        Game game = gameRepository.findGameById(id);
        if (game == null) {
            throw new ResourceNotFoundException("There is no such game in database");
        }
        games.add(game);
        genre.setGames(games);
        genreRepository.save(genre);
        List<Genre> genres = game.getGenre();
        genres.add(genre);
        game.setGenre(genres);
        gameRepository.save(game);
        cacheManager.clear();
        return game;
    }

    @AspectAnnotation
    public void deleteGameById(final Long id) {
        Game game = gameRepository.findGameById(id);
        if (game == null) {
            throw new ResourceNotFoundException("This game doesn't exist");
        }
        List<Genre> genres = game.getGenre();
        for (Genre genre : genres) {
            List<Game> games = genre.getGames();
            games.remove(game);
            genre.setGames(games);
            genreRepository.save(genre);
        }
        cacheManager.clear();
        gameRepository.deleteById(id);
    }
    @AspectAnnotation
    public Game getByName(final String name) {
        Object gameObj = cacheManager.get(GAME + name);
        if (gameObj != null) {
            return (Game) gameObj;
        } else {
            Game game = gameRepository.findGameByName(name);
            if (game == null) {
                throw new ResourceNotFoundException("Game with name: " + name + "doesnt exist");
            }
            cacheManager.put(GAME + game.getName(), game);
            return game;
        }
    }
}
