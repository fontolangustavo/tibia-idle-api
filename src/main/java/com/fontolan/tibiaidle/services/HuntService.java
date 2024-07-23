package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.*;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import com.fontolan.tibiaidle.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HuntService {
    private final DungeonRepository dungeonRepository;
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;

    public HuntService(DungeonRepository dungeonRepository, RoomRepository roomRepository, PlayerRepository playerRepository, ItemRepository itemRepository) {
        this.dungeonRepository = dungeonRepository;
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.itemRepository = itemRepository;
    }

    public Dungeon startDungeon(Long id) {
        Dungeon dungeon = dungeonRepository.getById(id);

        List<Room> rooms = dungeon.getRooms();

        Room room = rooms.get(0);

        Player player = generatePlayer();

        room.getPlayers().add(player);

        log.info("Player {} joined into the dungeon {} at room {}.", player.getName(), dungeon.getTitle(), room.getId());

        roomRepository.save(room);

        return dungeon;
    }

    private Player generatePlayer() {
        Player player = Player.builder()
                .name("Quero Pix")
                .maxHealth(150)
                .health(150)
                .maxMana(70)
                .mana(70)
                .experience(0)
                .build();

        Map<ItemType, Integer> weaponMastery = new HashMap<>();
        weaponMastery.put(ItemType.SWORD, 110);
        weaponMastery.put(ItemType.AXE, 0);
        weaponMastery.put(ItemType.CLUB, 0);
        weaponMastery.put(ItemType.BOW, 0);

        player.setWeaponMastery(weaponMastery);

        Item rightHand = itemRepository.save(Item.builder()
                .name("Fire Sword")
                .baseDamage(35)
                .type(ItemType.SWORD)
                .build()
        );

        List<PlayerItem> playerItems = new ArrayList<>();
        PlayerItem playerItem = player.equipItem(rightHand, SlotType.RIGHT_HAND);
        playerItems.add(playerItem);

        player.setPlayerItems(playerItems);
        player.setLevel(player.getLevel());

        return playerRepository.save(player);
    }
}
