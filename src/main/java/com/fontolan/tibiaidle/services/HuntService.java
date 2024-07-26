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

        Player player1 = generatePlayer("Quero Pix");
        Player player2 = generatePlayer("Mystic Lower");
        Player player3 = generatePlayer("Game player");
        player1.setRoom(room);
        player2.setRoom(room);
        player3.setRoom(room);

        room.getPlayers().add(player1);
        room.getPlayers().add(player2);
        room.getPlayers().add(player3);

        log.info("Player {} joined into the dungeon {} at room {}.", player1.getName(), dungeon.getTitle(), room.getId());
        log.info("Player {} joined into the dungeon {} at room {}.", player2.getName(), dungeon.getTitle(), room.getId());
        log.info("Player {} joined into the dungeon {} at room {}.", player3.getName(), dungeon.getTitle(), room.getId());

        roomRepository.save(room);

        return dungeon;
    }

    private Player generatePlayer(String name) {
        Player player = Player.builder()
                .name(name)
                .maxHealth(150)
                .health(150)
                .maxMana(70)
                .mana(70)
                .experience(0)
                .build();

        Map<ItemType, Integer> weaponMastery = new HashMap<>();
        weaponMastery.put(ItemType.SWORD, 10);
        weaponMastery.put(ItemType.AXE, 0);
        weaponMastery.put(ItemType.CLUB, 0);
        weaponMastery.put(ItemType.BOW, 0);

        player.setWeaponMastery(weaponMastery);

        Item rightHand = itemRepository.save(Item.builder()
                .name("Spike Sword")
                .baseDamage(24)
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
