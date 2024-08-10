package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.DamageType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class Creature implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected String id;
    protected String name;
    private int level;
    protected int health;
    private int experience;
    private int armor;
    private float mitigation;
    private List<ElementalProtection> resistances = new ArrayList<>();
    private List<ElementalDamage> abilities = new ArrayList<>();
    private List<DamageReceived> damageReceived = new ArrayList<>();
    public int getLevel(){
        return this.getLevelFromXP(this.experience);
    }
    public void setExperience(int experience) {
        int oldLevel = getLevel();

        this.experience = experience;

        if (oldLevel != getLevel()) {
            this.level = getLevel();
        }
    }
    protected int calculateTotalXP(int level) {
        return (50 * (level * level * level - level)) / 3;
    }
    protected int getLevelFromXP(int currentXP) {
        int level = 1;
        while (calculateTotalXP(level) <= currentXP) {
            level++;
        }
        return level - 1;
    }
    public boolean isAlive() {
        return this.health > 0;
    }
    public int attack(Creature target, int damage, DamageType damageType) {
        return target.receiveDamage(this, damage, damageType);
    }

    public int receiveDamage(Creature attacker, int damage, DamageType damageType) {
        int resistanceValue = resistances.stream()
                .filter(resistance -> resistance.getType().equals(damageType))
                .findFirst()
                .map(ElementalProtection::getValue)
                .orElse(100);

        float resistanceFactor = resistanceValue / 100.0f;
        float reducedDamage = damage * resistanceFactor;

        float armorReduction = armor / 2.0f;
        float armorReducedDamage = reducedDamage - armorReduction;
        if (armorReducedDamage < 0) {
            armorReducedDamage = 0;
        }

        int finalDamage = (int) (armorReducedDamage * (1 - mitigation));

        if (finalDamage > this.health){
            finalDamage = this.health;
        }

        this.health -= finalDamage;

        Optional<DamageReceived> optionalDamageReceived = this.damageReceived.stream().filter(damageReceived -> Objects.equals(damageReceived.getAttackerId(), attacker.getId())).findFirst();
        int index = -1;

        DamageReceived damageReceived = new DamageReceived();
        if (optionalDamageReceived.isPresent()) {
            damageReceived = optionalDamageReceived.get();
            damageReceived.setDamage(damageReceived.getDamage() + finalDamage);

            index = this.damageReceived.indexOf(damageReceived);
        } else {
            damageReceived = DamageReceived.builder()
                    .attackerId(attacker.getId())
                    .targetId(this.id)
                    .damage(finalDamage)
                    .build();
        }

        if (index == -1) {
            this.damageReceived.add(damageReceived);
        } else {
            this.damageReceived.set(index, damageReceived);
        }

        return finalDamage;
    }
}