package com.fontolan.tibiaidle.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Monster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private int health;
    private int experience;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}