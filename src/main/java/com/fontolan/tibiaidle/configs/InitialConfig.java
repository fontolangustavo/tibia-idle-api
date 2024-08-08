package com.fontolan.tibiaidle.configs;

import com.fontolan.tibiaidle.services.DungeonService;
import com.fontolan.tibiaidle.services.ItemService;
import com.fontolan.tibiaidle.services.MonsterService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Slf4j
public class InitialConfig {
    private final RedisTemplate redisTemplate;
    private final DungeonService dungeonService;
    private final ItemService itemService;
    private final MonsterService monsterService;

    public InitialConfig(RedisTemplate redisTemplate, DungeonService dungeonService, ItemService itemService, MonsterService monsterService) {
        this.redisTemplate = redisTemplate;
        this.dungeonService = dungeonService;
        this.itemService = itemService;
        this.monsterService = monsterService;
    }

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Initializing the gaming");
        log.info("Cleaning database");
        this.clearRedisCache();

        log.info("Loading items.json");
        this.itemService.initialize();

        log.info("Loading mammals.json");
        this.monsterService.initialize();

        log.info("Loading dungeons.json");
        this.dungeonService.initialize();
    }

    private void clearRedisCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        log.info("Redis cache cleared.");
    }
}
