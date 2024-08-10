package com.fontolan.tibiaidle.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@RedisHash("monster_item")
public class MonsterItem {
    private String itemId;
    private String name;
    private String rarity;
    private int quantity_min;
    private int quantity_max;

    public double getDropRate(){
        return switch (rarity) {
            case "COMMON" -> 0.7;
            case "UNCOMMON" -> 0.5;
            case "SEMI_RARE" -> 0.3;
            case "RARE" -> 0.1;
            case "VERY_RARE" -> 0.05;
            case "DURING_INVASIONS" -> 0.2;
            case "DURING_EVENTS" -> 0.3;
            default -> 0.1;
        };
    }
}
