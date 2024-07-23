package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Item;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import org.springframework.stereotype.Service;

@Service
public class FightService {
    private static final double LEVEL_FACTOR = 0.5;
    private static final double MASTERY_FACTOR = 0.2;

    public int damageCalculate(Player player) {
        Item item = player.getItemBySlotType(SlotType.RIGHT_HAND);

        if (item == null) {
            throw new RuntimeException();
        }

        int baseDamage = item.getBaseDamage();
        int playerLevel = player.getLevel();

        ItemType weaponType = item.getType();

        int weaponMastery = player.getWeaponMastery(weaponType);

        return (int) ((baseDamage + playerLevel * LEVEL_FACTOR) * (1 + weaponMastery * MASTERY_FACTOR));
    }
}
