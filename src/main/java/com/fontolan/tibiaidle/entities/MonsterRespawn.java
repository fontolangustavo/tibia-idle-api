package com.fontolan.tibiaidle.entities;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;

import java.time.Duration;
import java.util.Date;

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

    public boolean canRespawn() {
        Duration duration = Duration.between(this.getDiedAt().toInstant(), new Date().toInstant());

        return duration.getSeconds() >= this.getRespawnIn();
    }

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
