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
}
