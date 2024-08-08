package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, String> {
    Optional<Item> findByName(String name);
}
