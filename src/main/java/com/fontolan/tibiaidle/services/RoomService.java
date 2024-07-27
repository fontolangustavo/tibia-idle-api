package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.entities.Room;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import com.fontolan.tibiaidle.utils.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class RoomService {
    private final DungeonRepository dungeonRepository;

    public RoomService(DungeonRepository dungeonRepository) {
        this.dungeonRepository = dungeonRepository;
    }

    public List<Room> findRoomsWithPlayers() {
        Iterable<Dungeon> dungeons  = dungeonRepository.findAll();

        return StreamSupport.stream(dungeons.spliterator(), false)
                .flatMap(dungeon -> dungeon.getRooms().stream())
                .filter(room -> !room.getPlayers().isEmpty())
                .toList();
    }

    public void updateRoom(Room room) {
        Iterable<Dungeon> dungeons  = dungeonRepository.findAll();
        Optional<Dungeon> optionalDungeon = StreamSupport.stream(dungeons.spliterator(), false)
                .filter(dungeon -> dungeon.getRooms().stream().anyMatch(room1 -> Objects.equals(room1.getId(), room.getId())))
                .findFirst();

        if (optionalDungeon.isEmpty()) {
            throw new RuntimeException();
        }

        Dungeon dungeon = optionalDungeon.get();

        int index = ArrayUtils.indexOfById(dungeon.getRooms(), room.getId(), Room::getId);

        if (index == -1) {
            throw new RuntimeException();
        }

        List<Room> rooms = dungeon.getRooms();

        rooms.set(index, room);

        dungeon.setRooms(rooms);

        dungeonRepository.save(dungeon);
    }
}
