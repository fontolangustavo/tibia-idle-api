package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<Player, String> {
    List<Player> findAllUserId(String playerId);
}
