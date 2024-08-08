package com.fontolan.tibiaidle.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@RedisHash("monster")
public class Monster implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Indexed
    private String name;
    private int maxHealth;
    private int experience;
    private String image;
    private int speed;
    private int armor;
    private int charms;
    private float mitigation;
    private List<ElementalProtection> resistances;
    private List<ElementalDamage> abilities;
    private List<MonsterItem> loots;
}
