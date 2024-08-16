package com.fontolan.tibiaidle.controllers.mappers;

import com.fontolan.tibiaidle.controllers.response.ItemResponse;
import com.fontolan.tibiaidle.controllers.response.PlayerItemResponse;
import com.fontolan.tibiaidle.entities.PlayerItem;
import org.springframework.stereotype.Component;

@Component
public class PlayerItemMapper {
    public PlayerItemResponse toPlayerItemResponse(PlayerItem playerItem) {
        PlayerItemResponse playerItemResponse = PlayerItemResponse.builder()
                .id(playerItem.getId())
                .quantity(playerItem.getQuantity())
                .slotType(playerItem.getSlotType())
                .build();

        ItemResponse itemResponse = ItemResponse.builder().build();

        return playerItemResponse;
    }
}
