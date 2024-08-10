package com.fontolan.tibiaidle.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fontolan.tibiaidle.entities.*;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import com.fontolan.tibiaidle.repositories.MonsterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Slf4j
@Service
public class DungeonService {
    private final DungeonRepository dungeonRepository;
    private final MonsterRepository monsterRepository;
    private final IdGenerationService idGenerationService;

    public DungeonService(DungeonRepository dungeonRepository, MonsterRepository monsterRepository, IdGenerationService idGenerationService) {
        this.dungeonRepository = dungeonRepository;
        this.monsterRepository = monsterRepository;
        this.idGenerationService = idGenerationService;
    }

    public void initialize() {
        log.info("Init loading dungeon: sewer-city.json");
        this.loadDungeonFromJson("imports/dungeons/rookgaard/sewer-city.json");
    }

    public void loadDungeonFromJson(String jsonFilePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Dungeon dungeon = objectMapper.readValue(new File(jsonFilePath), Dungeon.class);

            for (Room room : dungeon.getRooms()) {
                room.setId(idGenerationService.generateId());

                for (MonsterRespawn monsterRespawn : room.getMonsters()) {
                    monsterRespawn.setId(idGenerationService.generateId());

                    Optional<Monster> monsterOptional = monsterRepository.findByName(monsterRespawn.getName());
                    monsterOptional.ifPresentOrElse(
                            (value) -> {
                                monsterRespawn.setHealth(value.getMaxHealth());
                                monsterRespawn.setMonsterId(value.getId());
                                monsterRespawn.setResistances(value.getResistances());
                                monsterRespawn.setAbilities(value.getAbilities());
                                monsterRespawn.setArmor(value.getArmor());
                                monsterRespawn.setMitigation(value.getMitigation());
                            },
                            () -> log.warn("Room {} has a monster {} out of the database", room.getId(), monsterRespawn.getName())
                    );
                }
            }

            dungeonRepository.save(dungeon);
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }
}