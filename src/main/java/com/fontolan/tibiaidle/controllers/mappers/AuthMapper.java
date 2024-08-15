package com.fontolan.tibiaidle.controllers.mappers;

import com.fontolan.tibiaidle.controllers.response.AuthResponse;
import com.fontolan.tibiaidle.controllers.response.PlayerResponse;
import com.fontolan.tibiaidle.controllers.response.UserResponse;
import com.fontolan.tibiaidle.entities.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthMapper {
    public AuthResponse toAuthResponse(String token, User user) {
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .build();

        if (user != null) {
            List<PlayerResponse> players = new ArrayList<>();

            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .players(players)
                    .build();

            authResponse.setUser(userResponse);
        }

        return authResponse;
    }
}
