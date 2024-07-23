package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Entity
public class Monster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int maxHealth;
    private int health;
    private int experience;
    private int respawnIn;
    private Date diedAt;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "target_player_id")
    private Player targetPlayer;

    public void attack(Player player) {
        int damage = 10;

        log.info("{} deal {} damage to a {}.", this.name, player.getName(), damage);

        player.receiveDamage(damage);
    }

    public void receiveDamage(int damage) {
        if (this.health - damage <= 0) {
            this.diedAt = new Date();

            log.info("{} - {} is dead.", this.id, this.name);
        }

        this.health = Math.max(this.health - damage, 0);
    }
    public boolean isAlive() {
        return this.health > 0;
    }
}
