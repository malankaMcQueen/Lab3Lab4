package com.example.gameinfoservice.repository;

import com.example.gameinfoservice.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Game findGameById(Long id);

    @Query("SELECT g FROM Game g JOIN FETCH g.genre WHERE g.name = :name")
    Game findGameByName(@Param("name") String name);
}
