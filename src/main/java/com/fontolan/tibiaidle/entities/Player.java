package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level;
    private int maxHealth;
    private int health;
    private int maxMana;
    private int mana;
    private int experience;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    private List<PlayerItem> playerItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "target_monster_id")
    private Monster targetMonster;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated(EnumType.STRING)
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
        log.info("{} deal {} damage to a {}.", this.name, monster.getName(), damage);

        monster.receiveDamage(damage);
    }

    public void receiveDamage(int damage) {
        if (this.health - damage <= 0) {
            log.info("{} is dead.", this.name);
        }

        this.health = Math.max(this.health - damage, 0);
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public int getWeaponMastery(ItemType type) {
        return weaponMastery.getOrDefault(type, 0);
    }

    public List<Item> getBackpack() {
        return playerItems.stream()
                .filter(playerItem -> playerItem.getSlotType() == SlotType.BACKPACK)
                .map(PlayerItem::getItem)
                .toList();
    }

    public Item getItemBySlotType(SlotType slotType) {
        return playerItems.stream()
                .filter(playerItem -> playerItem.getSlotType() == slotType)
                .map(PlayerItem::getItem)
                .findFirst()
                .orElse(null);
    }

    public PlayerItem equipItem(Item item, SlotType slotType) {
        return PlayerItem.builder()
                .player(this)
                .item(item)
                .slotType(slotType)
                .build();
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
