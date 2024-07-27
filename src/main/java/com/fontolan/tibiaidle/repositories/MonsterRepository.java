package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Monster;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonsterRepository extends CrudRepository<Monster, String> {
}
