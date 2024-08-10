package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.DamageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementalProtection {
    private int value;
    private DamageType type;
}
