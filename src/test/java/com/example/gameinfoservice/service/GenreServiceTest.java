package com.example.gameinfoservice.service;

import com.example.gameinfoservice.cache.CacheManager;
import com.example.gameinfoservice.exception.BadRequestException;
import com.example.gameinfoservice.exception.ResourceNotFoundException;
import com.example.gameinfoservice.model.Game;
import com.example.gameinfoservice.model.Genre;
import com.example.gameinfoservice.repository.GameRepository;
import com.example.gameinfoservice.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceTest {
    private static final String GENRE = "GenreName_";

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllGenre() {
        List<Genre> genres = new ArrayList<>();
        Genre genreFirst = new Genre();
        Genre genreSecond = new Genre();
        genreFirst.setName("Action");
        genreSecond.setName("Adventure");
        genres.add(genreFirst);
        genres.add(genreSecond);

        when(genreRepository.findAll()).thenReturn(genres);
        when(cacheManager.get(anyString())).thenReturn(null);

        List<Genre> result = genreService.getAllGenre();

        assertEquals(2, result.size());
        verify(cacheManager, times(2)).put(anyString(), any());
    }

    @Test
    void testAddNewGenre() {
        String genreName = "RPG";
        when(genreRepository.findGenreByName(genreName)).thenReturn(null);

        genreService.addNewGenre(genreName);

        verify(genreRepository, times(1)).save(any());
        verify(cacheManager, times(1)).put(anyString(), any());
    }

    @Test
    void testAddNewGenreAlreadyExists() {
        String genreName = "Action";
        Genre genre = new Genre();
        genre.setName(genreName);
        when(genreRepository.findGenreByName(genreName)).thenReturn(genre);

        assertThrows(BadRequestException.class, () -> genreService.addNewGenre(genreName));

        verify(genreRepository, never()).save(any());
        verify(cacheManager, never()).put(anyString(), any());
    }

    @Test
    void testFindGenreByName() {
        String genreName = "RPG";
        Genre genre = new Genre();
        genre.setName(genreName);
        when(cacheManager.get(GENRE + genreName)).thenReturn(genre);
        when(genreRepository.findGenreByName(genreName)).thenReturn(genre);

        Genre result = genreService.findGenreByName(genreName);

        assertNotNull(result);
        assertEquals(genreName, result.getName());
        verify(cacheManager, times(1)).get(GENRE + genreName);
        verify(genreRepository, never()).save(any());
    }

    @Test
    void testFindGenreByNameNotFound() {
        String genreName = "NonExistingGenre";
        when(cacheManager.get(GENRE + genreName)).thenReturn(null);
        when(genreRepository.findGenreByName(genreName)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> genreService.findGenreByName(genreName));

        verify(cacheManager, times(1)).get(GENRE + genreName);
        verify(genreRepository, never()).save(any());
    }

    @Test
    void testChangeName() {
        String oldName = "RPG";
        String newName = "Role Playing";
        Genre genre = new Genre();
        genre.setName(oldName);
        when(genreRepository.findGenreByName(oldName)).thenReturn(genre);

        genreService.changeName(oldName, newName);

        assertEquals(newName, genre.getName());
        verify(genreRepository, times(1)).save(genre);
        verify(cacheManager, times(1)).clear();
    }

    @Test
    void testChangeNameNotFound() {
        String oldName = "NonExistingGenre";
        String newName = "Role Playing";
        when(genreRepository.findGenreByName(oldName)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> genreService.changeName(oldName, newName));

        verify(genreRepository, never()).save(any());
        verify(cacheManager, never()).clear();
    }

    @Test
    void testDeleteByName() {
        String genreName = "RPG";
        Genre genre = new Genre();
        genre.setName(genreName);
        Game game = new Game();
        List <Genre> genreList = new ArrayList<>();
        genreList.add(genre);
        game.setGenre(genreList);
        List<Game> games = new ArrayList<>();
        games.add(game);
        genre.setGames(games);
        when(genreRepository.findGenreByName(genreName)).thenReturn(genre);

        genreService.deleteByName(genreName);

        verify(genreRepository, times(1)).delete(genre);
        verify(cacheManager, times(1)).clear();
        for (Game gameTmp : games) {
            verify(gameRepository, times(1)).save(gameTmp);
        }
    }

    @Test
    void testDeleteByNameNotFound() {
        String genreName = "NonExistingGenre";
        when(genreRepository.findGenreByName(genreName)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> genreService.deleteByName(genreName));

        verify(genreRepository, never()).delete(any());
        verify(cacheManager, never()).clear();
        verify(gameRepository, never()).save(any());
    }
}
