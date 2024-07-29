package com.fontolan.tibiaidle.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fontolan.tibiaidle.entities.Item;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void initialize() {
        log.info("Init loading items: swords.json");
        this.loadFromJson("imports/items/swords.json", "swords");

        log.info("Init loading items: axes.json");
        this.loadFromJson("imports/items/axes.json","axes");

        log.info("Init loading items: clubs.json");
        this.loadFromJson("imports/items/clubs.json", "clubs");

        log.info("Init loading items: ammunitions.json");
        this.loadFromJson("imports/items/ammunitions.json",  "ammunitions");

        log.info("Init loading items: armors.json");
        this.loadFromJson("imports/items/armors.json",  "armors");

        log.info("Init loading items: boots.json");
        this.loadFromJson("imports/items/boots.json",  "boots");

        log.info("Init loading items: distances.json");
        this.loadFromJson("imports/items/distances.json",  "distances");

        log.info("Init loading items: helmets.json");
        this.loadFromJson("imports/items/helmets.json",  "helmets");

        log.info("Init loading items: legs.json");
        this.loadFromJson("imports/items/legs.json",  "legs");

        log.info("Init loading items: shields.json");
        this.loadFromJson("imports/items/shields.json",  "shields");

        log.info("Init loading items: spellbooks.json");
        this.loadFromJson("imports/items/spellbooks.json",  "spellbooks");

        log.info("Init loading items: rods.json");
        this.loadFromJson("imports/items/rods.json",  "rods");

        log.info("Init loading items: wands.json");
        this.loadFromJson("imports/items/wands.json",  "wands");
    }

    public void loadFromJson(String jsonFilePath, String url) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Item> items = objectMapper.readValue(new File(jsonFilePath), new TypeReference<List<Item>>(){});

            for(Item item : items) {
                item.setImage("items/" + url + "/" + item.getImage());

                itemRepository.save(item);
            }
        } catch(Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
