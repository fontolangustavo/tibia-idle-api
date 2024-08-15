package com.fontolan.tibiaidle.controllers.response;

import com.fontolan.tibiaidle.entities.PlayerItem;
import com.fontolan.tibiaidle.enums.Vocation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse extends CreatureResponse {
    private int maxHealth;
    private int maxMana;
    private int mana;
    private Vocation vocation;
    private List<PlayerItem> playerItems = new ArrayList<>();
    private String targetId;
    private String roomId;
}
