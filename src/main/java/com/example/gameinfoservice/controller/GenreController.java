package com.example.gameinfoservice.controller;

import com.example.gameinfoservice.model.Genre;
import com.example.gameinfoservice.service.GenreService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/genre")
@Tag(name = "GenreController",
        description = "You can view and edit information about genre")
public class GenreController {

    private GenreService genreService;

    @GetMapping
    public ResponseEntity<List<Genre>> getAllMatches() {
        List<Genre> genres = genreService.getAllGenre();
        if (genres.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(genres, HttpStatus.OK);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveGenre(@RequestParam final String name) {
        genreService.addNewGenre(name);
        return new ResponseEntity<>("Genre was created", HttpStatus.OK);
    }

    @GetMapping("/getByName")
    public ResponseEntity<Genre> getGenreInfo(@RequestParam(value = "name") final String name) {
        return new ResponseEntity<>(genreService.findGenreByName(name), HttpStatus.OK);
    }

    @PutMapping("/changeName/genre/{oldName}/{newName}")
    public ResponseEntity<String> changeGenreName(@PathVariable final String oldName,
                                                  @PathVariable final String newName) {
        genreService.changeName(oldName,newName);
        return new ResponseEntity<>("Data has been updated", HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGenre(@RequestParam(value = "name") final String name) {
        genreService.deleteByName(name);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }
}
