package com.fontolan.tibiaidle.controllers;

import com.fontolan.tibiaidle.controllers.request.DungeonRequest;
import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.services.HuntService;
import com.fontolan.tibiaidle.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/hunts")
public class HuntController {
    private final HuntService huntService;
    private final PlayerService playerService;

    public HuntController(HuntService huntService, PlayerService playerService) {
        this.huntService = huntService;
        this.playerService = playerService;
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Dungeon> startHunt(@PathVariable String id, @Valid DungeonRequest request) {
        Player player = playerService.show(request.getPlayerId());

        Dungeon dungeon = huntService.startDungeon(id, player);

        return ResponseEntity.ok(dungeon);
    }
}