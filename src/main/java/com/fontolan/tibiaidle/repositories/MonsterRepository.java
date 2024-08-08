package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Monster;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonsterRepository extends CrudRepository<Monster, String> {
    Optional<Monster> findByName(String name);
}
