package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Item;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.entities.PlayerItem;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class FightService {
    private static final double LEVEL_FACTOR = 0.3;
    private static final double MASTERY_FACTOR = 0.1;
    private final ItemRepository itemRepository;

    public FightService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public int damageCalculate(Player player) {
        PlayerItem playerItem = player.getItemBySlotType(SlotType.RIGHT_HAND);

        Optional<Item> optionalItem = itemRepository.findById(playerItem.getItemId());

        if (optionalItem.isEmpty()) {
            throw new RuntimeException();
        }

        Item item = optionalItem.get();

        int baseDamage = item.getBaseAttack();
        int playerLevel = player.getLevel();

        ItemType weaponType = item.getType();

        int weaponMastery = player.getWeaponMastery(weaponType);

        return (int) ((baseDamage + playerLevel * LEVEL_FACTOR) * (1 + weaponMastery * MASTERY_FACTOR));
    }
}
