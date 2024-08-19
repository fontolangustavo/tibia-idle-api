package com.fontolan.tibiaidle.controllers;

import com.fontolan.tibiaidle.controllers.mappers.DungeonMapper;
import com.fontolan.tibiaidle.controllers.request.GetAllRequest;
import com.fontolan.tibiaidle.controllers.response.DungeonResponse;
import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.services.DungeonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dungeons")
public class DungeonController {
    private final DungeonService dungeonService;
    private final DungeonMapper dungeonMapper;

    public DungeonController(DungeonService dungeonService, DungeonMapper dungeonMapper) {
        this.dungeonService = dungeonService;
        this.dungeonMapper = dungeonMapper;
    }
    @GetMapping
    public ResponseEntity<Page<DungeonResponse>> getAll(@Valid GetAllRequest request, JwtAuthenticationToken token){
        String userId = null;

        if (token != null) {
            userId = token.getName();
        }

        Page<Dungeon> dungeons = dungeonService.getAll(request.getPage(), request.getLimit(), userId);

        var response = dungeons.map(dungeonMapper::toDungeonResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
