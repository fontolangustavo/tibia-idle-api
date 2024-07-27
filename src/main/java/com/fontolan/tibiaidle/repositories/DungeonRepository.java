package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Dungeon;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DungeonRepository extends CrudRepository<Dungeon, String> {
    Dungeon getById(String id);
    Dungeon getByTitle(String title);
}

