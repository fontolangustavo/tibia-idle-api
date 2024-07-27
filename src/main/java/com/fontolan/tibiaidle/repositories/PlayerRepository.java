package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, String> {
}
