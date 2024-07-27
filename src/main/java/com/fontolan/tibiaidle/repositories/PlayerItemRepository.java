package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.PlayerItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerItemRepository extends CrudRepository<PlayerItem, String> {
    Optional<PlayerItem> findByPlayerId(String playerId);
}
