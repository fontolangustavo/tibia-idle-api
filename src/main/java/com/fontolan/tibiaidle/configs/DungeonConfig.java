package com.fontolan.tibiaidle.configs;

import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import com.fontolan.tibiaidle.services.DungeonService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;

import java.io.IOException;

@Configuration
public class DungeonConfig {
    private final DungeonRepository dungeonRepository;
    private final DungeonService dungeonService;

    public DungeonConfig(DungeonRepository dungeonRepository, DungeonService dungeonService) {
        this.dungeonRepository = dungeonRepository;
        this.dungeonService = dungeonService;
    }

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        if (!dungeonRepository.exists(Example.of(Dungeon.builder().title("Dwarf Mines").build()))) {
            System.out.println("init dwarf-dungeon");
            dungeonService.loadDungeonFromJson("dungeons/dwarf-dungeon.json");
        }
    }
}
