package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Dungeon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "dungeon", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();
}
