package com.fontolan.tibiaidle.controllers;

import com.fontolan.tibiaidle.controllers.mappers.PlayerMapper;
import com.fontolan.tibiaidle.controllers.request.GetAllRequest;
import com.fontolan.tibiaidle.controllers.response.PlayerResponse;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    public PlayerController(PlayerService playerService, PlayerMapper playerMapper) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAll(@Valid GetAllRequest request, JwtAuthenticationToken token){
        String userId = token.getName();

        List<Player> players = playerService.getAll(userId);

        var response = players.stream().map(playerMapper::toPlayerResponse).toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
