package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}