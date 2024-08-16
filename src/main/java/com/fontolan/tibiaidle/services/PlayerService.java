package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Item;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.enums.Vocation;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;

    public PlayerService(PlayerRepository playerRepository, ItemRepository itemRepository) {
        this.playerRepository = playerRepository;
        this.itemRepository = itemRepository;
    }

    public PageImpl<Player> getAll(int page, int limit, String userId) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        List<Player> players = playerRepository.findByUserId(userId);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), players.size());

        if (start > players.size()) {
            return new PageImpl<>(List.of(), pageable, players.size());
        }

        return new PageImpl<>(players.subList(start, end), pageable, players.size());
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
