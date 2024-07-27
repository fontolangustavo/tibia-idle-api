package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("room")
public class Room implements Serializable {
    @Id
    private String id;

    private String dungeonId;

    private List<Monster> monsters = new ArrayList<>();

    private List<String> players = new ArrayList<>();
}
