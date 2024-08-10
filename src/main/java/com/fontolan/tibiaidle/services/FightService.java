package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.*;
import com.fontolan.tibiaidle.enums.DamageType;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class FightService {

    private static final double LEVEL_FACTOR = 0.05;
    private static final double MASTERY_FACTOR = 0.03;
    public enum HitType {
        MELEE,
        SPELL
    }
    private final ItemRepository itemRepository;

    public FightService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Pair<Integer, DamageType>> damageCalculate(Creature attacker, HitType damageType ) {
        List<Pair<Integer, DamageType>> damages = new ArrayList<>();

        if (attacker instanceof Player) {
            if (damageType.equals(HitType.MELEE)) {
                PlayerItem playerItem = ((Player) attacker).getItemBySlotType(SlotType.RIGHT_HAND);
                Optional<Item> optionalItem = itemRepository.findById(playerItem.getItemId());

                if (optionalItem.isEmpty()) {
                    throw new RuntimeException();
                }

                Item item = optionalItem.get();
                if (item.getBaseAttack() > 0) {
                    int itemDamage = this.damageHitItem((Player) attacker, item);

                    damages.add(new Pair<>(this.damageRandomize(itemDamage), DamageType.PHYSICAL));
                }

                for(ElementalDamage elementalDamage : item.getElementalDamage()) {
                    damages.add(new Pair<>(this.damageRandomize(elementalDamage.getValue()), elementalDamage.getType()));
                }
            }

        } else if (attacker instanceof MonsterRespawn) {
            for(ElementalDamage elementalDamage : attacker.getAbilities()) {
                damages.add(new Pair<>(this.damageRandomize(elementalDamage.getValue()), elementalDamage.getType()));
            }
        }

        return damages;
    }

    private int damageHitItem(Player attacker, Item item) {
        int baseDamage = item.getBaseAttack();
        int playerLevel = attacker.getLevel();

        ItemType weaponType = item.getType();

        int weaponMastery = attacker.getWeaponMastery(weaponType);

        return (int) ((baseDamage + playerLevel * LEVEL_FACTOR) * (1 + weaponMastery * MASTERY_FACTOR));
    }

    private int damageRandomize(int baseDamage) {
        Random random = new Random();

        int variation = (int) (baseDamage * 0.10);
        if (baseDamage < 10) {
            variation = 3; // Garante uma variação mínima de 1
        }

        int minDamage = baseDamage - variation;
        int maxDamage = baseDamage + variation;

        return random.nextInt(maxDamage - minDamage + 1) + minDamage;
    }
}
