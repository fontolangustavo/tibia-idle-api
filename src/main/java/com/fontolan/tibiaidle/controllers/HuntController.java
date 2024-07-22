package com.fontolan.tibiaidle.controllers;

import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.services.HuntService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hunts")
public class HuntController {
    private final HuntService huntService;

    public HuntController(HuntService huntService) {
        this.huntService = huntService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Dungeon> startHunt(@PathVariable Long id) {
        Dungeon dungeon = huntService.createDungeonExample();
        return ResponseEntity.ok(dungeon);
    }
}