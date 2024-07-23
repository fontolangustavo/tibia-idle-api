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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dungeon_id")
    private Dungeon dungeon;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Monster> monsters = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "room_players",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();

    @PostLoad
    @PostPersist
    @PostUpdate
    public void setRoomsDungeon() {
        for (Monster monster : monsters) {
            monster.setRoom(this);
        }
    }
}
