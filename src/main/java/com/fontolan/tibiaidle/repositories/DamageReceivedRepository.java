package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.DamageReceived;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DamageReceivedRepository extends CrudRepository<DamageReceived, String> {
}
