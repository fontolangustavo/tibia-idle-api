package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.enums.Vocation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.*;
import java.util.stream.StreamSupport;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@RedisHash("player")
public class Player implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private int level;
    private int maxHealth;
    private int health;
    private int maxMana;
    private int mana;
    private int experience;
    private Vocation vocation;
    private List<PlayerItem> playerItems = new ArrayList<>();
    private String targetId;
    private String roomId;

    @Indexed
    private Map<ItemType, Integer> weaponMastery = new HashMap<>();

    public int getLevel(){
        return this.getLevelFromXP(this.experience);
    }

    public void setExperience(int experience) {
        int oldLevel = getLevel();

        this.experience = experience;

        if (oldLevel != getLevel()) {
            this.level = getLevel();
        }
    }

    public void attack(Monster monster, int damage) {
        int effectiveDamage = Math.min(damage, monster.getHealth());

        log.info("{} deal {} damage to a {} - {}.", this.name, effectiveDamage, monster.getId(), monster.getName());

        boolean monsterIsAlive = monster.receiveDamage(this, effectiveDamage);

        if(!monsterIsAlive) {
            this.setTargetId(null);
        }
    }

    public boolean receiveDamage(int damage) {
        if (this.health - damage <= 0) {
            log.info("{} is dead.", this.name);
        }

        this.health = Math.max(this.health - damage, 0);

        return this.isAlive();
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public int getWeaponMastery(ItemType type) {
        return weaponMastery.getOrDefault(type, 0);
    }

    public PlayerItem getItemBySlotType(SlotType slotType) {
        return StreamSupport.stream(playerItems.spliterator(), false)
                .filter(item -> item.getSlotType().equals(slotType))
                .findFirst()
                .orElse(null);
    }

    public boolean equipItem(Item item, SlotType slotType) {
        Optional<PlayerItem> hasItem = this.checkSlotType(slotType);

        if(slotType.equals(SlotType.BACKPACK)) {
            return false;
        }

        if(hasItem.isEmpty()) {
            this.unequipItem(slotType);
        }

        playerItems.add(
                PlayerItem.builder()
                        .itemId(item.getId())
                        .slotType(slotType)
                        .build()
        );

        return true;
    }

    public Optional<PlayerItem> checkSlotType(SlotType slotType) {
        return this.playerItems.stream()
                .filter(playerItem ->
                        Objects.equals(playerItem.getPlayerId(), this.id)
                        && playerItem.getSlotType() == slotType
                )
                .findFirst();
    }

    public void unequipItem(SlotType slotType) {
        playerItems.stream()
                .filter(playerItem -> playerItem.getSlotType() == slotType)
                .forEach(pi -> pi.setSlotType(SlotType.BACKPACK));
    }

    public int calculateTotalXP(int level) {
        return (50 * (level * level * level - level)) / 3;
    }

    public int getLevelFromXP(int currentXP) {
        int level = 1;
        while (calculateTotalXP(level) <= currentXP) {
            level++;
        }
        return level - 1;
    }
}
