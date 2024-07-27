package com.fontolan.tibiaidle.configs;

import com.fontolan.tibiaidle.repositories.DungeonRepository;
import com.fontolan.tibiaidle.services.DungeonService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

@Configuration
@Slf4j
public class DungeonConfig {
    private final DungeonRepository dungeonRepository;
    private final DungeonService dungeonService;
    private final RedisTemplate redisTemplate;

    public DungeonConfig(DungeonRepository dungeonRepository, DungeonService dungeonService, RedisTemplate redisTemplate) {
        this.dungeonRepository = dungeonRepository;
        this.dungeonService = dungeonService;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    @Transactional
    public void init() throws IOException {
        clearRedisCache();

        log.info("Init loading dungeons: dwarf-dungeon");
        dungeonService.loadDungeonFromJson("dungeons/dwarf-dungeon.json");
    }

    private void clearRedisCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        log.info("Redis cache cleared.");
    }
}
