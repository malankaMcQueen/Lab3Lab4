package com.example.gameinfoservice.controller;

import com.example.gameinfoservice.model.Game;
import com.example.gameinfoservice.model.Genre;
import com.example.gameinfoservice.service.GameService;
import com.example.gameinfoservice.service.RepositoriesService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GameController {
  private final GameService gameService;
  private final RepositoriesService repositoriesService;

  public GameController(GameService gameService, RepositoriesService repositoriesService) {
    this.gameService = gameService;
    this.repositoriesService = repositoriesService;
  }

  //Получение информации об игре из API
  @GetMapping("/game")
  public ResponseEntity<String> getGamesFromApi(
      @RequestParam(value = "rawgId", defaultValue = "null") String rawgId) {
    if (Objects.equals(rawgId, "null")) {
      return new ResponseEntity<>("Missed parameter", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(gameService.getGameById(rawgId), HttpStatus.OK);
  }

  //Запись игры(имя игры, id игры на сайте RAWG, и описание игры) в базу данных
  @PostMapping("/saveInDataBase/fromApi/game")
  public ResponseEntity<String> saveGameAndGenresFromApi(
      @RequestParam(value = "rawgId") String rawgId) {
    Game game = repositoriesService.findByRawgId(rawgId);
    if (game != null) {
      return new ResponseEntity<>("This game is already exists", HttpStatus.OK);
    }
    game = new Game();
    game.setName(gameService.getGameFieldValue(rawgId, "name"));
    game.setDescription(gameService.getGameFieldValue(rawgId, "description"));
    game.setRawgId(rawgId);
    repositoriesService.saveGame(game);
    return new ResponseEntity<>("Game saved!", HttpStatus.OK);
  }

  //Создание и запись собственного жанра игры
  @PostMapping("/saveInDataBase/genre")
  public ResponseEntity<String> saveGenre(@RequestParam(value = "name") String name) {
    Genre genre = repositoriesService.findGenreByName(name);
    if (genre != null) {
      return new ResponseEntity<>("This genre is already exists", HttpStatus.BAD_REQUEST);
    }
    genre = new Genre();
    genre.setName(name);
    repositoriesService.saveGenre(genre);
    return new ResponseEntity<>("Genre was created", HttpStatus.OK);
  }

  //Создание и запись собственной игры
  @PostMapping("/saveInDataBase/game")
  public ResponseEntity<String> saveGame(@RequestParam(value = "name") String name,
                                         @RequestParam(value = "description") String description) {
    Game game = repositoriesService.findGameByName(name);
    if (game != null) {
      return new ResponseEntity<>("This game is already exists", HttpStatus.BAD_REQUEST);
    }
    game = new Game();
    game.setName(name);
    game.setDescription(description);
    repositoriesService.saveGame(game);
    return new ResponseEntity<>("Game was created", HttpStatus.OK);
  }

  //Вывод информации про жанр игр и сами игры привязвнные к этому жанру
  @GetMapping("/getFromDataBase/genre")
  public ResponseEntity<String> getGenreInfo(@RequestParam(value = "name") String name) {
    Genre genre = repositoriesService.findGenreByName(name);
    if (genre == null) {
      return new ResponseEntity<>("Genre not found", HttpStatus.NOT_FOUND);
    }
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode jsonObject = objectMapper.createObjectNode();

    jsonObject.put("id", genre.getId());
    jsonObject.put("name", genre.getName());

    ArrayNode jsonArray = objectMapper.valueToTree(genre.getGames());

    jsonObject.set("games", jsonArray);

    return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
  }

  //Вывод информации про игру из API по rawgId и жанры, к которым она привязана
  @GetMapping("/getFromDataBase/game")
  public ResponseEntity<String> getGameInfo(@RequestParam(value = "rawgId") String rawgId) {
    Game game = repositoriesService.findByRawgId(rawgId);
    if (game == null) {
      return new ResponseEntity<>("This game not found", HttpStatus.NOT_FOUND);
    }
    List<Genre> genres = game.getGenre();
    for (Genre genre : genres) {
      genre.setGames(null);
    }
    game.setGenre(genres);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.valueToTree(game);

    ObjectNode jsonObject = (ObjectNode) jsonNode;

    return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
  }

  //Вывод всех игр
  @GetMapping("/getAllGames")
  public ResponseEntity<List<Game>> getAllGames() {
    return new ResponseEntity<>(repositoriesService.getAllGames(), HttpStatus.OK);
  }

  //Изаменение имени игры по id
  @PutMapping("/changeInfo/game/{id}/{newName}")
  public ResponseEntity<String> changeGameName(@PathVariable Long id,
                                               @PathVariable String newName) {
    Game game = repositoriesService.findGameById(id);
    if (game == null) {
      return new ResponseEntity<>("Game not found", HttpStatus.BAD_REQUEST);
    }
    game.setName(newName);
    repositoriesService.saveGame(game);
    return new ResponseEntity<>("Data has been updated", HttpStatus.OK);
  }

  //Изменение имени жанра по старому имени
  @PutMapping("/changeInfo/genre/{oldName}/{newName}")
  public ResponseEntity<String> changeGenreName(@PathVariable String oldName,
                                                @PathVariable String newName) {
    Genre genre = repositoriesService.findGenreByName(oldName);
    if (genre == null) {
      return new ResponseEntity<>("Game not found", HttpStatus.BAD_REQUEST);
    }
    genre.setName(newName);
    repositoriesService.saveGenre(genre);
    return new ResponseEntity<>("Data has been updated", HttpStatus.OK);
  }

  //Привязка какой-либо игры к жанру
  @PutMapping("/putGame/{id}/toGenre/{name}")
  public ResponseEntity<String> putGameToGenre(@PathVariable Long id,
                                               @PathVariable String name) {
    Genre genre = repositoriesService.findGenreByName(name);
    List<Game> games = genre.getGames();
    for (Game game : games) {
      if (Objects.equals(game.getId(), id)) {
        return new ResponseEntity<>("This game is already exists in this type of genre",
            HttpStatus.BAD_REQUEST);
      }
    }
    Game game = repositoriesService.findGameById(id);
    if (game == null) {
      return new ResponseEntity<>("There is no such game in database",
          HttpStatus.BAD_REQUEST);
    }
    games.add(game);
    genre.setGames(games);
    repositoriesService.saveGenre(genre);
    List<Genre> genres = game.getGenre();
    genres.add(genre);
    game.setGenre(genres);
    repositoriesService.saveGame(game);
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  //Удаление игры
  @DeleteMapping("/deleteInfo/game")
  public ResponseEntity<String> deleteGame(@RequestParam(value = "id") Long id) {
    Game game = repositoriesService.findGameById(id);
    if (game == null) {
      return new ResponseEntity<>("This game doesn't exist", HttpStatus.BAD_REQUEST);
    }
    List<Genre> genres = game.getGenre();
    for (Genre genre : genres) {
      List<Game> games = genre.getGames();
      games.remove(game);
      genre.setGames(games);
    }
    repositoriesService.deleteGame(game);
    return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
  }

  //Удаление жанра
  @DeleteMapping("/deleteInfo/genre")
  public ResponseEntity<String> deleteGenre(@RequestParam(value = "name") String name) {
    Genre genre = repositoriesService.findGenreByName(name);
    if (genre == null) {
      return new ResponseEntity<>("Genre doesn't exist", HttpStatus.BAD_REQUEST);
    }
    List<Game> games = genre.getGames();
    for (Game game : games) {
      List<Genre> genres = game.getGenre();
      genres.remove(genre);
      game.setGenre(genres);
    }
    repositoriesService.deleteGenre(genre);
    return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
  }
}
