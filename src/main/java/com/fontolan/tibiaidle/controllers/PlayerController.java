package com.fontolan.tibiaidle.controllers;

import com.fontolan.tibiaidle.controllers.mappers.PlayerMapper;
import com.fontolan.tibiaidle.controllers.request.GetAllRequest;
import com.fontolan.tibiaidle.controllers.request.PlayerStore;
import com.fontolan.tibiaidle.controllers.response.PlayerResponse;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Page<PlayerResponse>> getAll(@Valid GetAllRequest request, JwtAuthenticationToken token){
        String userId = token.getName();

        Page<Player> players = playerService.getAll(request.getPage(), request.getLimit(), userId);

        var response = players.map(playerMapper::toPlayerResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlayerResponse> store(@Valid PlayerStore request, JwtAuthenticationToken token) {
        String userId = token.getName();

        Player player = Player.builder()
                .userId(userId)
                .build();

        player.setName(request.getName());

        var response = playerMapper.toPlayerResponse(playerService.store(player));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
