package com.fontolan.tibiaidle.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@RedisHash("monster_respawn")
public class MonsterRespawn extends Creature {
    private String monsterId;
    private int respawnIn;
    private Date diedAt;
    private String roomId;
    private String targetId;

    @Override
    public String toString() {
        return "MonsterRespawn{" +
                "monsterId='" + monsterId + '\'' +
                ", respawnIn=" + respawnIn +
                ", diedAt=" + diedAt +
                ", roomId='" + roomId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", health=" + health +
                '}';
    }
}
