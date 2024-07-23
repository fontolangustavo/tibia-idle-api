package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Dungeon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @PostLoad
    @PostPersist
    @PostUpdate
    public void setRoomsDungeon() {
        for (Room room : rooms) {
            room.setDungeon(this);
        }
    }
}
