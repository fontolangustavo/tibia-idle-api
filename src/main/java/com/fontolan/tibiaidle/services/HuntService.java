package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Dungeon;
import com.fontolan.tibiaidle.entities.Monster;
import com.fontolan.tibiaidle.entities.Room;
import com.fontolan.tibiaidle.repositories.DungeonRepository;
import org.springframework.stereotype.Service;

@Service
public class HuntService {
    private final DungeonRepository dungeonRepository;

    public HuntService(DungeonRepository dungeonRepository) {
        this.dungeonRepository = dungeonRepository;
    }

    public Dungeon createDungeonExample() {
        Dungeon dungeon = new Dungeon();
        dungeon.setTitle("Dwarf Dungeon");

        Room room1 = new Room();
        room1.setDungeon(dungeon);

        Monster dwarf = new Monster();
        dwarf.setType("Dwarf");
        dwarf.setHealth(90);
        dwarf.setExperience(45);
        dwarf.setRoom(room1);

        room1.getMonsters().add(dwarf);

        dungeon.getRooms().add(room1);

        dungeonRepository.save(dungeon);

        return dungeon;
    }
}
