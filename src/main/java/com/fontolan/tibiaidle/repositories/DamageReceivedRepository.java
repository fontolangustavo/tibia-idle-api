package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.DamageReceived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DamageReceivedRepository extends JpaRepository<DamageReceived, Long> {
}
