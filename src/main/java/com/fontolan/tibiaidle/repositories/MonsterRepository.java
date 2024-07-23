package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Monster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonsterRepository extends JpaRepository<Monster, Long> {
}
