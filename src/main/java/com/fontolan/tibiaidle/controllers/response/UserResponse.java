package com.fontolan.tibiaidle.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private List<PlayerResponse> players;
}
