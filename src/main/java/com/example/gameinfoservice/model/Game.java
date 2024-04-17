package com.example.gameinfoservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
//
@Entity
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String rawgId;
  private String description;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "game_genre",
      joinColumns = @JoinColumn(name = "game_id"),
      inverseJoinColumns = @JoinColumn(name = "genre_id")
  )
  @JsonManagedReference
  private List<Genre> genre;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Genre> getGenre() {
    return genre;
  }

  public void setGenre(List<Genre> genre) {
    this.genre = genre;
  }

  public String getRawgId() {
    return rawgId;
  }

  public void setRawgId(String rawgId) {
    this.rawgId = rawgId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
