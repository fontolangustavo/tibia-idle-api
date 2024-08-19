package com.fontolan.tibiaidle.controllers.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DungeonRequest {
    @NotBlank
    String playerId;
}
