package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.entities.Room;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Dungeon startDungeon(String id, Player player) {
        Optional<Dungeon> optionalDungeon = dungeonRepository.findById(id);

        if (optionalDungeon.isPresent()) {
            Dungeon dungeon = optionalDungeon.get();
            List<Room> rooms = dungeon.getRooms();
            Room room = rooms.get(0);

            player.setRoomId(room.getId());

            playerRepository.save(player);

            room.getPlayers().add(player.getId());

            log.info("Player {} joined into the dungeon {} at room {}.", player.getName(), dungeon.getTitle(), room.getId());

            rooms.set(0, room);
            dungeon.setRooms(rooms);

            dungeonRepository.save(dungeon);

            return dungeon;
        }

        return null;
    }
}
