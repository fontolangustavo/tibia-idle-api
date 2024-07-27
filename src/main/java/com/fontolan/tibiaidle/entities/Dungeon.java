package com.fontolan.tibiaidle.entities;

import com.fasterxml.jackson.annotation.JsonClassDescription;
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
@RedisHash("dungeon")
@JsonClassDescription
public class Dungeon implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String title;
    private String image;
    private int minLevel;
    private int maxLevel;
    private int minProfitPerHour;
    private int maxProfitPerHour;
    private int minXpPerHour;
    private int maxXpPerHour;

    @OneToMany(mappedBy = "dungeon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();
}
