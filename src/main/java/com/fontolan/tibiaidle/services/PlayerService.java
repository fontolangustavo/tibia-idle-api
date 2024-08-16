package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Item;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.enums.Vocation;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;

    public PlayerService(PlayerRepository playerRepository, ItemRepository itemRepository) {
        this.playerRepository = playerRepository;
        this.itemRepository = itemRepository;
    }

    public List<Player> getAll(String userId) {
        Iterable<Player> iterablePlayers = playerRepository.findAllUserId(userId);

        return StreamSupport.stream(iterablePlayers.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Player store(Player player) {
        return playerRepository.save(this.generatePlayer(player));
    }

    private Player generatePlayer(Player player) {
        player.setExperience(1);
        player.setMaxHealth(150);
        player.setMaxMana(70);
        player.setMana(70);
        player.setPlayerItems(new ArrayList<>());
        player.setVocation(Vocation.ROOKIE);

        Map<ItemType, Integer> weaponMastery = new HashMap<>();
        weaponMastery.put(ItemType.SWORD, 10);
        weaponMastery.put(ItemType.AXE, 10);
        weaponMastery.put(ItemType.CLUB, 10);
        weaponMastery.put(ItemType.DISTANCE, 10);
        weaponMastery.put(ItemType.SHIELD, 10);

        player.setWeaponMastery(weaponMastery);
        player.setHealth(150);
        player.setLevel(player.getLevel());

        Optional<Item> rightHand = itemRepository.findByName("Club");
        Optional<Item> apple = itemRepository.findByName("Red Apple");

        player.equipItem(rightHand.get(), SlotType.RIGHT_HAND);
        player.addItemBackpack(apple.get(), 5);

        return player;
    }
}
