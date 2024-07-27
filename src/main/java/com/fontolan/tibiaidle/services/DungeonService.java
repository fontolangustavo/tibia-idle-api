package com.fontolan.tibiaidle.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.entities.Monster;
import com.fontolan.tibiaidle.entities.Room;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class DungeonService {
    private final DungeonRepository dungeonRepository;
    private final IdGenerationService idGenerationService;

    public DungeonService(DungeonRepository dungeonRepository, IdGenerationService idGenerationService) {
        this.dungeonRepository = dungeonRepository;
        this.idGenerationService = idGenerationService;
    }

    public void loadDungeonFromJson(String jsonFilePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Dungeon dungeon = objectMapper.readValue(new File(jsonFilePath), Dungeon.class);

            for (Room room : dungeon.getRooms()) {
                room.setId(idGenerationService.generateId());

                for (Monster monster : room.getMonsters()) {
                    monster.setId(idGenerationService.generateId());
                }
            }

            dungeonRepository.save(dungeon);
        } catch(Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}