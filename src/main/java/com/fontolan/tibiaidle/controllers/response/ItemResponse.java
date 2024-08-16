package com.fontolan.tibiaidle.controllers.response;

import com.fontolan.tibiaidle.entities.AttributeBuff;
import com.fontolan.tibiaidle.entities.ElementalDamage;
import com.fontolan.tibiaidle.entities.ElementalProtection;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.Vocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemResponse {
    private String id;
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
