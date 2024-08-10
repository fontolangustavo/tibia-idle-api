package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.*;
import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class HuntService {
    private final DungeonRepository dungeonRepository;
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;

    public HuntService(DungeonRepository dungeonRepository, PlayerRepository playerRepository, ItemRepository itemRepository) {
        this.dungeonRepository = dungeonRepository;
        this.playerRepository = playerRepository;
        this.itemRepository = itemRepository;
    }

    public Dungeon startDungeon(String id) {
        Optional<Dungeon> optionalDungeon = dungeonRepository.findById(id);

        if (optionalDungeon.isPresent()) {
            Dungeon dungeon = optionalDungeon.get();
            List<Room> rooms = dungeon.getRooms();
            Room room = rooms.get(0);

            Player player1 = generatePlayer("Quero Pix");
            Player player2 = generatePlayer("Mystic Lower");
            Player player3 = generatePlayer("Game player");
            player1.setRoomId(room.getId());
            player2.setRoomId(room.getId());
            player3.setRoomId(room.getId());

            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);

            room.getPlayers().add(player1.getId());
            room.getPlayers().add(player2.getId());
            room.getPlayers().add(player3.getId());

            log.info("Player {} joined into the dungeon {} at room {}.", player1.getName(), dungeon.getTitle(), room.getId());
            log.info("Player {} joined into the dungeon {} at room {}.", player2.getName(), dungeon.getTitle(), room.getId());
            log.info("Player {} joined into the dungeon {} at room {}.", player3.getName(), dungeon.getTitle(), room.getId());

            rooms.set(0, room);
            dungeon.setRooms(rooms);

            dungeonRepository.save(dungeon);

            return dungeon;
        }

        return null;
    }

    private Player generatePlayer(String name) {
        Player player = Player.builder()
                .maxHealth(150)
                .maxMana(70)
                .mana(70)
                .playerItems(new ArrayList<>())
                .build();

        Map<ItemType, Integer> weaponMastery = new HashMap<>();
        weaponMastery.put(ItemType.SWORD, 10);
        weaponMastery.put(ItemType.AXE, 10);
        weaponMastery.put(ItemType.CLUB, 10);
        weaponMastery.put(ItemType.DISTANCE, 10);

        player.setWeaponMastery(weaponMastery);
        player.setHealth(150);
        player.setName(name);
        player.setLevel(player.getLevel());

        Optional<Item> rightHand = itemRepository.findByName("Club");

        player.equipItem(rightHand.get(), SlotType.RIGHT_HAND);

        return player;
    }
}
