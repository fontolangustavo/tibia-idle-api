package com.fontolan.tibiaidle.controllers.mappers;

import com.fontolan.tibiaidle.controllers.response.AuthResponse;
import com.fontolan.tibiaidle.controllers.response.PlayerResponse;
import com.fontolan.tibiaidle.controllers.response.UserResponse;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.entities.User;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AuthMapper {
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public AuthMapper(PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    public AuthResponse toAuthResponse(String token, User user) {
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .build();

        if (user != null) {
            List<PlayerResponse> players = new ArrayList<>();

            if (user.getPlayers() != null) {
                for(String playerId : user.getPlayers()) {
                    Optional<Player> player = playerRepository.findById(playerId);

                    player.ifPresent(value -> players.add(playerMapper.toPlayerResponse(value)));
                }
            }

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
