package com.fontolan.tibiaidle.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatureResponse {
    protected String id;
    protected String name;
    private int level;
    protected int health;
    private int experience;
    private int armor;
    private float mitigation;
}
