package com.fontolan.tibiaidle.controllers.mappers;

import com.fontolan.tibiaidle.controllers.response.DungeonResponse;
import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.utils.UrlUtil;
import org.springframework.stereotype.Component;

@Component
public class DungeonMapper {
    public DungeonResponse toDungeonResponse(Dungeon dungeon) {
        DungeonResponse dungeonResponse = DungeonResponse.builder()
                .id(dungeon.getId())
                .title(dungeon.getTitle())
                .minLevel(dungeon.getMinLevel())
                .maxLevel(dungeon.getMaxLevel())
                .minProfitPerHour(dungeon.getMinProfitPerHour())
                .maxProfitPerHour(dungeon.getMaxProfitPerHour())
                .minXpPerHour(dungeon.getMinXpPerHour())
                .maxXpPerHour(dungeon.getMaxXpPerHour())
                .build();

        dungeonResponse.setImage(UrlUtil.buildUrlForImage(dungeon.getImage()));

        return dungeonResponse;
    }
}
