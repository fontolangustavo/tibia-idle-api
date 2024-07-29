package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.Vocation;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("item")
public class Item implements Serializable {
    @Id
    private String id;
    private String name;
    private String image;
    private int baseAttack;
    private int baseDefense;
    private boolean isTwoHands;
    private List<ElementalDamage> elementalDamage;
    private List<ElementalProtection> elementalProtection;
    private List<AttributeBuff> attributeBuff;
    private int maxEnchantmentSlots;
    private int level;
    private ItemType type;
    private List<Vocation> vocations;
    private int hitPercentageIncrease;
    private int manaCost;
}