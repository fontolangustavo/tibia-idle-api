package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@RedisHash("monster")
public class Monster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private int maxHealth;
    private int health;
    private int experience;
    private int respawnIn;
    private Date diedAt;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private String targetId;

    private List<DamageReceived> damageReceived = new ArrayList<>();

    public void attack(Player player) {
        int damage = 15;

        log.info("{} deal {} damage to a {}.", this.name, player.getName(), damage);

        boolean playerIsAlive = player.receiveDamage(damage);

        if (!playerIsAlive) {
            this.setTargetId(null);
        }
    }

    public boolean receiveDamage(Player player, int damage) {
        if (this.health - damage <= 0) {
            log.info("{} - {} is dead.", this.id, this.name);
        }

        Optional<DamageReceived> optionalDamageReceived = this.damageReceived.stream().filter(damageReceived -> Objects.equals(damageReceived.getPlayerId(), player.getId())).findFirst();

        int index = -1;

        DamageReceived damageReceived = new DamageReceived();
        if (optionalDamageReceived.isPresent()) {
            damageReceived = optionalDamageReceived.get();

            damageReceived.setDamage(damageReceived.getDamage() + damage);

            index = this.damageReceived.indexOf(damageReceived);
        } else {
            damageReceived = DamageReceived.builder()
                    .playerId(player.getId())
                    .monsterId(this.id)
                    .damage(damage)
                    .build();
        }

        if (index == -1) {
            this.damageReceived.add(damageReceived);
        } else {
            this.damageReceived.set(index, damageReceived);
        }

        this.health = Math.max(this.health - damage, 0);

        return this.isAlive();
    }
    public boolean isAlive() {
        return this.health > 0;
    }
}
