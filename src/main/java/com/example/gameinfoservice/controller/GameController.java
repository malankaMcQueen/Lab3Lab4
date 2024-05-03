package com.example.gameinfoservice.controller;

import com.example.gameinfoservice.model.Game;
import com.example.gameinfoservice.service.GameService;


import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/game")
@AllArgsConstructor
@RestController
@Tag(name = "GameController",
        description = "You can view and edit information about game")
public class GameController {
    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> gameList = gameService.getAllGames();
        if (gameList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(gameList, HttpStatus.OK);
        }
    }

    @GetMapping("/getByName")
    public ResponseEntity<Game> getByName(@RequestParam final String name) {
        return new ResponseEntity<>(gameService.getByName(name), HttpStatus.OK);
    }
    @PostMapping("/saveGame")
    public ResponseEntity<String> addMatch(
            @RequestBody final Game game) {
        gameService.saveGame(game);
        return new ResponseEntity<>("Game was created", HttpStatus.OK);
    }

    //Изаменение имени игры по id
    @PutMapping("/changeInfo/game/{id}/{newName}")
    public ResponseEntity<String> changeGameName(@PathVariable final Long id,
                                                 @PathVariable final String newName) {
        if (gameService.changeName(id, newName)) {
            return new ResponseEntity<>("Data has been updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Game not found", HttpStatus.BAD_REQUEST);
        }
    }

    //Привязка какой-либо игры к жанру
    @PutMapping("/putGame/{id}/toGenre/{name}")
    public ResponseEntity<Game> putGameToGenre(@PathVariable final Long id,
                                               @PathVariable final String name) {
        return new ResponseEntity<>(gameService.putGameToGenre(id, name), HttpStatus.OK);
    }

    //Удаление игры
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGame(@RequestParam(value = "id") final Long id) {
        gameService.deleteGameById(id);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/addListGames")
    public ResponseEntity<List<Game>> addListGame(@RequestBody final List<Game> gameList){
        return new ResponseEntity<>(gameService.addMultipleGames(gameList), HttpStatus.OK);
    }
}


