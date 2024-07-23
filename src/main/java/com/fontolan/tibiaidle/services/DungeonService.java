package com.fontolan.tibiaidle.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.entities.Monster;
import com.fontolan.tibiaidle.entities.Room;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import com.fontolan.tibiaidle.repositories.MonsterRepository;
import com.fontolan.tibiaidle.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DungeonService {
    private final DungeonRepository dungeonRepository;
    private final RoomRepository roomRepository;
    private final MonsterRepository monsterRepository;

    public DungeonService(DungeonRepository dungeonRepository, RoomRepository roomRepository, MonsterRepository monsterRepository) {
        this.dungeonRepository = dungeonRepository;
        this.roomRepository = roomRepository;
        this.monsterRepository = monsterRepository;
    }

    public Dungeon loadDungeonFromJson(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Dungeon dungeon = objectMapper.readValue(new File(jsonFilePath), Dungeon.class);

        dungeon.setRoomsDungeon();

        return dungeonRepository.save(dungeon);
    }
}