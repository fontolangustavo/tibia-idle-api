package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @OneToMany(mappedBy = "monster", fetch = FetchType.EAGER)
    private List<DamageReceived> damageReceiveds;

    public void attack(Player player) {
        int damage = 25;

        log.info("{} deal {} damage to a {}.", this.name, player.getName(), damage);

        boolean playerIsAlive = player.receiveDamage(damage);

        if (!playerIsAlive) {
            this.setTargetPlayer(null);
        }
    }

    public boolean receiveDamage(Player player, int damage) {
        if (this.health - damage <= 0) {
            log.info("{} - {} is dead.", this.id, this.name);
        }

        Optional<DamageReceived> optionalDamageReceived = this.damageReceiveds.stream().filter(damageReceived -> Objects.equals(damageReceived.getPlayer().getId(), player.getId())).findFirst();

        DamageReceived damageReceived = new DamageReceived();
        if (optionalDamageReceived.isPresent()) {
            damageReceived.setDamage(damageReceived.getDamage() + damage);
        } else {
            damageReceived = DamageReceived.builder()
                    .player(player)
                    .monster(this)
                    .damage(damage)
                    .build();
        }

        this.damageReceiveds.add(damageReceived);

        this.health = Math.max(this.health - damage, 0);

        return this.isAlive();
    }
    public boolean isAlive() {
        return this.health > 0;
    }
}
