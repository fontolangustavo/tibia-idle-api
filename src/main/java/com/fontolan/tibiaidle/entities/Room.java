package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
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

}