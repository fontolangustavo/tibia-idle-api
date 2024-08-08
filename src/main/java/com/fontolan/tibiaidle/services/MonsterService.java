package com.fontolan.tibiaidle.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fontolan.tibiaidle.entities.*;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.repositories.MonsterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MonsterService {
    private final MonsterRepository monsterRepository;
    private final ItemRepository itemRepository;

    public MonsterService(MonsterRepository monsterRepository, ItemRepository itemRepository) {
        this.monsterRepository = monsterRepository;
        this.itemRepository = itemRepository;
    }

    public void initialize() {
        log.info("Init loading monsters: mammals.json");
        this.loadMonsterFromJson("imports/monsters/mammals.json");
    }

    private void loadMonsterFromJson(String jsonFilePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Monster> monsters = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Monster>>(){});

            for (Monster monster : monsters) {

                List<MonsterItem> loots = monster.getLoots();
                for(MonsterItem loot : loots) {
                    Optional<Item> item = itemRepository.findByName(loot.getName());

                    item.ifPresentOrElse(
                        value -> loot.setItemId(value.getId()),
                        () -> log.warn("Monster {} has an item {} out of the database", monster.getName(), loot.getName())
                    );
                }

                monsterRepository.save(monster);
            }

        } catch(Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
