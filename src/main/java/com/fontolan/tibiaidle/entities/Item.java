package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.Vocation;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("item")
public class Item implements Serializable {
    @Id
    private String id;
    @Indexed
    private String name;
    private String image;
    private boolean isGroupable;
    private int baseAttack;
    private int baseDefense;
    private boolean isTwoHands;
    private List<ElementalDamage> elementalDamage = new ArrayList<>();
    private List<ElementalProtection> elementalProtection = new ArrayList<>();
    private List<AttributeBuff> attributeBuff = new ArrayList<>();
    private int maxEnchantmentSlots;
    private int level;
    private ItemType type;
    private List<Vocation> vocations = new ArrayList<>();
    private int hitPercentageIncrease;
    private int manaCost;
}