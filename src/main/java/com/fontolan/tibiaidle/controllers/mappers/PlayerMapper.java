package com.fontolan.tibiaidle.controllers.mappers;

import com.fontolan.tibiaidle.controllers.response.PlayerResponse;
import com.fontolan.tibiaidle.entities.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public PlayerResponse toPlayerResponse(Player player) {
        PlayerResponse playerResponse = new PlayerResponse();

        playerResponse.setId(player.getId());
        playerResponse.setName(player.getName());
        playerResponse.setMaxHealth(player.getMaxHealth());
        playerResponse.setMaxMana(playerResponse.getMaxMana());
        playerResponse.setVocation(player.getVocation());

        return playerResponse;
    }
}
