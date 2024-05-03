package com.example.gameinfoservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.gameinfoservice.cache.CacheManager;

import com.example.gameinfoservice.exception.BadRequestException;
import com.example.gameinfoservice.exception.ResourceNotFoundException;
import com.example.gameinfoservice.model.Game;
import com.example.gameinfoservice.model.Genre;
import com.example.gameinfoservice.repository.GameRepository;
import com.example.gameinfoservice.repository.GenreRepository;
import com.example.gameinfoservice.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllGames() {
        List<Game> games = new ArrayList<>();
        Game game1 = new Game();
        Game game2 = new Game();
        game1.setName("Game1");
        game2.setName("Game2");
        games.add(game1);
        games.add(game2);

        when(gameRepository.findAll()).thenReturn(games);
        when(cacheManager.get(anyString())).thenReturn(null);

        List<Game> result = gameService.getAllGames();

        assertEquals(2, result.size());
        verify(cacheManager, times(2)).put(anyString(), any());
    }

    @Test
    void testSaveGame() {
        Game game = new Game();
        game.setName("NewGame");

        when(gameRepository.findGameByName("NewGame")).thenReturn(null);

        Game result = gameService.saveGame(game);

        assertNotNull(result);
        assertEquals("NewGame", result.getName());
        verify(gameRepository, times(1)).save(game);
        verify(cacheManager, times(1)).put(anyString(), any());

        when(gameRepository.findGameByName("NewGame")).thenReturn(game);
        Assertions.assertThrows(BadRequestException.class, () -> {
            gameService.saveGame(game);
        });

    }

    @Test
    void testAddMultipleGames() {
        Game game1 = new Game();
        Game game2 = new Game();
        game1.setName("Game1");
        game2.setName("Game2");
        List<Game> gameList = new ArrayList<>();
        gameList.add(game1);
        gameList.add(game2);

        when(gameRepository.findGameByName(anyString())).thenReturn(null);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Game> result = gameService.addMultipleGames(gameList);

        assertEquals(2, result.size());
        verify(gameRepository, times(2)).save(any(Game.class));
        verify(cacheManager, times(2)).put(anyString(), any());
    }

    @Test
    void testPutGameToGenre() {
        Genre genre = new Genre();
        genre.setName("Action");
        Game existingGame = new Game();
        existingGame.setName("ExistingGame");
        existingGame.setId(1L);
        genre.setGames(Collections.singletonList(existingGame));
        when(genreRepository.findGenreByName("Action")).thenReturn(genre);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> gameService.putGameToGenre(1L, "Action"));
        assertEquals("This game is already exists in this type of genre", exception.getMessage());

        verify(genreRepository, times(1)).findGenreByName("Action");
        verify(genreRepository, never()).save(any());
        verify(gameRepository, never()).save(any());

    }
    @Test
    void testPutGameToGenreExcept() {
        Genre genre = new Genre();
        genre.setName("Action");
        Game existingGame = new Game();
        existingGame.setName("ExistingGame");
        existingGame.setId(1L);
        when(genreRepository.findGenreByName("Action")).thenReturn(genre);

        genre.setGames(new ArrayList<>());
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            gameService.putGameToGenre(1L, "Action");
        });
    }

    @Test
    void testPutGameToGenreSuccess() {
        Long gameId = 1L;
        String genreName = "Action";
        Genre genre = new Genre();
        Game game = new Game();
        genre.setName(genreName);
        game.setName("GameName");
        game.setGenre(new ArrayList<>());
        genre.setGames(new ArrayList<>());
        when(genreRepository.findGenreByName(genreName)).thenReturn(genre);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        Game result = gameService.putGameToGenre(gameId, genreName);

        assertEquals(game, result);
        assertTrue(genre.getGames().contains(game));
        assertTrue(game.getGenre().contains(genre));
        verify(genreRepository, times(1)).findGenreByName(genreName);
        verify(gameRepository, times(1)).findById(gameId);
        verify(genreRepository, times(1)).save(genre);
        verify(gameRepository, times(1)).save(game);
        verify(cacheManager, times(1)).clear();
    }

    @Test
    void testPutGameToGenreNoGenre() {
        when(genreRepository.findGenreByName("Action")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> gameService.putGameToGenre(1L, "Action"));
        assertEquals("Genre with name Action not found   ", exception.getMessage());

        verify(genreRepository, times(1)).findGenreByName("Action");
        verify(gameRepository, never()).findById(any());
        verify(genreRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    void testDeleteGameById() {
        Game game = new Game();
        game.setName("Game");
        game.setId(1L);
        Genre genre = new Genre();
        genre.setName("Action");

        List<Game> gameList = new ArrayList<>();
        gameList.add(game);
        genre.setGames(gameList);
        List<Genre> genres = new ArrayList<>();
        genres.add(genre);
        game.setGenre(genres);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        gameService.deleteGameById(1L);

        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).deleteById(1L);
        verify(cacheManager, times(1)).clear();
        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    void testDeleteGameByIdNotFound() {
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> gameService.deleteGameById(1L));
        assertEquals("Game with id: 1 not found", exception.getMessage());

        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, never()).deleteById(any());
        verify(cacheManager, never()).clear();
        verify(genreRepository, never()).save(any());
    }

    @Test
    void testGetByName() {
        Game game = new Game();
        game.setName("Game");
        game.setId(1L);
        when(cacheManager.get("gameName_Game")).thenReturn(game);
        when(gameRepository.findGameByName("Game")).thenReturn(game);

        Game result = gameService.getByName("Game");

        assertNotNull(result);
        assertEquals("Game", result.getName());
        verify(cacheManager, times(1)).get("gameName_Game");
        verify(gameRepository, never()).save(any());

        when(cacheManager.get("gameName_Game")).thenReturn(null);

        result = gameService.getByName("Game");

        verify(cacheManager, times(1)).put("gameName_Game", game);


    }

    @Test
    void testGetByNameNotFound() {
        when(cacheManager.get("gameName_NonExistingGame")).thenReturn(null);
        when(gameRepository.findGameByName("NonExistingGame")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> gameService.getByName("NonExistingGame"));
        assertEquals("Game with name: NonExistingGame doesnt exist", exception.getMessage());

        verify(cacheManager, times(1)).get("gameName_NonExistingGame");
        verify(gameRepository, never()).save(any());
    }

    @Test
    void testChangeName() {
        Long gameId = 1L;
        String newName = "NewGameName";
        Game game = new Game();
        game.setName("OldGameName");
        game.setId(gameId);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        assertTrue(gameService.changeName(gameId, newName));

        assertEquals(newName, game.getName());
        verify(cacheManager, times(1)).clear();
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    void testChangeNameNotFound() {
        Long gameId = 1L;
        String newName = "NewGameName";
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> gameService.changeName(gameId, newName));
        assertEquals("Game with id 1 not found ", exception.getMessage());

        verify(cacheManager, never()).clear();
        verify(gameRepository, never()).save(any());
    }
}
