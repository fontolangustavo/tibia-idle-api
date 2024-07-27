package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.SlotType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("player_item")
public class PlayerItem implements Serializable {
    @Id
    private String id;

    private String playerId;

    private String itemId;

    private SlotType slotType;
}
