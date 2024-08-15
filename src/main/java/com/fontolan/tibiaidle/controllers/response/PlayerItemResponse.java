package com.fontolan.tibiaidle.controllers.response;

import com.fontolan.tibiaidle.enums.SlotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlayerItemResponse {
    private String id;
    private ItemResponse item;
    private int quantity;
    private SlotType slotType;
}
