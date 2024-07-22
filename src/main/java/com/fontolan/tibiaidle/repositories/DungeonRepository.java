package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Dungeon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, Long> {
}

