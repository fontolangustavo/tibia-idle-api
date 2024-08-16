package com.fontolan.tibiaidle.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DungeonResponse {
    private String id;
    private String title;
    private String image;
    private int minLevel;
    private int maxLevel;
    private int minProfitPerHour;
    private int maxProfitPerHour;
    private int minXpPerHour;
    private int maxXpPerHour;
}
