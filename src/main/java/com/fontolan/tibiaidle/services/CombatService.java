package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.*;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.repositories.MonsterRepository;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import com.fontolan.tibiaidle.utils.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class CombatService {
    private static final Random RANDOM = new Random();
    private final MonsterRepository monsterRepository;
    private final PlayerRepository playerRepository;
    private final ItemRepository itemRepository;
    private final FightService fightService;
    private final RoomService roomService;

    public CombatService(MonsterRepository monsterRepository, PlayerRepository playerRepository, ItemRepository itemRepository, FightService fightService, RoomService roomService) {
        this.monsterRepository = monsterRepository;
        this.playerRepository = playerRepository;
        this.itemRepository = itemRepository;
        this.fightService = fightService;
        this.roomService = roomService;
    }

    @Scheduled(fixedRate = 5000)
    public void performCombatTick() {
        List<Room> rooms = roomService.findRoomsWithPlayers();

        for (Room room : rooms) {
            List<String> players = room.getPlayers();
            List<MonsterRespawn> monsters = room.getMonsters();

            Collections.shuffle(players);
            Collections.shuffle(monsters);

            log.info("Running a room {} with players count {} at {} - players inside", room.getId(), players.size(), new Date());

            for (String playerId : players) {
                Player player = playerRepository.findById(playerId)
                        .orElseThrow();

                MonsterRespawn target = ArrayUtils.findById(monsters, player.getTargetId(), MonsterRespawn::getId);

                List<MonsterRespawn> justLiveMonsters = monsters.stream()
                        .filter(MonsterRespawn::isAlive)
                        .toList();

                if ((target == null || (target != null && !target.isAlive())) && !justLiveMonsters.isEmpty()) {
                    target = justLiveMonsters.get(RANDOM.nextInt(justLiveMonsters.size()));

                    player.setTargetId(target.getId());

                    playerRepository.save(player);
                }

                if (target != null && target.isAlive() && player.isAlive()) {
                    int damage = fightService.damageCalculate(player);
                    player.attack(target, damage);

                    int index = monsters.indexOf(target);

                    if (index != -1) {
                        monsters.set(index, target);
                    } else {
                        throw new RuntimeException();
                    }
                }
            }

            for (MonsterRespawn monster : monsters) {
                Player target = null;
                if (monster.getTargetId() != null) {
                    target = playerRepository.findById(monster.getTargetId())
                            .orElse(null);
                }

                if (target == null && !players.isEmpty()){
                    String targetId = players.get(new Random().nextInt(players.size()));
                    monster.setTargetId(targetId);

                    target = playerRepository.findById(monster.getTargetId())
                            .orElse(null);
                }

                if (target != null && target.isAlive() && monster.isAlive()) {
                    monster.attack(target);

                    playerRepository.save(target);
                }
            }

            // Remove jogadores mortos
            players.removeIf(playerId -> {
                Player player = playerRepository.findById(playerId)
                        .orElseThrow();

                boolean isDead = !player.isAlive();

                if (isDead) {
                    player.setTargetId(null);
                    player.setHealth(player.getMaxHealth());
                    player.setRoomId(null);
                    playerRepository.save(player);
                }

                return isDead;
            });

            for(MonsterRespawn monster : monsters) {
                if(!monster.isAlive() && monster.getDiedAt() == null) {
                    monster.setDiedAt(new Date());

                    Optional<Monster> realMonster = monsterRepository.findById(monster.getMonsterId());

                    int totalXP = 0;
                    if (realMonster.isPresent()) {
                        totalXP = realMonster.get().getExperience();
                    } else {
                        log.warn("Monster {} not found experience.", monster.getName());
                    }


                    List<DamageReceived> damageReceiveds = monster.getDamageReceived();
                    int totalDamage = damageReceiveds.stream().mapToInt(DamageReceived::getDamage).sum();

                    for (DamageReceived damageReceived : damageReceiveds) {
                        Optional<Player> optionalPlayer = playerRepository.findById(damageReceived.getPlayerId());

                        if (optionalPlayer.isEmpty()) {
                            throw new RuntimeException();
                        }

                        Player player = optionalPlayer.get();

                        int damageDealt = damageReceived.getDamage();
                        int xpGained = (int) ((double) damageDealt / totalDamage * totalXP);

                        player.setExperience(player.getExperience() + xpGained);
                        player.setTargetId(null);

                        playerRepository.save(player);
                    }

                    damageReceiveds.sort(Comparator.comparingInt(DamageReceived::getDamage).reversed());

                    DamageReceived topDamage = damageReceiveds.isEmpty() ? null : damageReceiveds.get(0);

                    if (topDamage != null) {
                        List<MonsterItem> loots = realMonster.get().getLoots();
                        Optional<Player> optionalPlayer = playerRepository.findById(topDamage.getPlayerId());

                        if (optionalPlayer.isEmpty()) {
                            throw new RuntimeException();
                        }

                        Player player = optionalPlayer.get();

                        StringBuilder lootMessage = new StringBuilder();
                        for(MonsterItem loot : loots) {
                            int quantity = RANDOM.nextInt(loot.getQuantity_max() - loot.getQuantity_min() + 1) + loot.getQuantity_min();
                            if(shouldDrop(loot) && quantity > 0) {
                                Optional<Item> optionalItem = itemRepository.findById(loot.getItemId());

                                if (optionalItem.isPresent()) {
                                    Item item = optionalItem.get();
                                    PlayerItem playerItem = player.getItemFromBackpack(loot.getItemId());

                                    if (playerItem != null) {
                                        playerItem.setQuantity(item.isGroupable() ? playerItem.getQuantity() + quantity : 1);

                                        int index = player.getIndexItemFromBackpack(loot.getItemId());
                                        player.getPlayerItems().set(index, playerItem);
                                    } else {
                                        playerItem = PlayerItem.builder()
                                                .playerId(player.getId())
                                                .itemId(loot.getItemId())
                                                .quantity(item.isGroupable() ? quantity : 1)
                                                .slotType(SlotType.BACKPACK)
                                                .build();

                                        player.getPlayerItems().add(playerItem);
                                    }

                                    if (lootMessage.isEmpty()) {
                                        lootMessage.append(quantity).append(" ").append(loot.getName());
                                    }else {
                                        lootMessage.append(", ").append(quantity).append(" ").append(loot.getName());
                                    }

                                } else {
                                    log.error("Item {} not found.", loot.getItemId());
                                }
                            }
                        }

                        if (!lootMessage.isEmpty()){
                            log.info("Player {} received of {}: {}.", player.getName(), monster.getName(), lootMessage);
                        }

                        playerRepository.save(player);
                    } else {
                        log.error("Error while give loots to player from monster {}.", monster.getName());
                    }
                }
            }

            monsters = respawnMonsters(monsters);

            room.setPlayers(players);
            room.setMonsters(monsters);

            roomService.updateRoom(room);
        }
    }

    private boolean shouldDrop(MonsterItem item) {
        return RANDOM.nextDouble() < getDropChance(item.getRarity());
    }

    private double getDropChance(String rarity) {
        return switch (rarity) {
            case "COMMON" -> 0.7;
            case "UNCOMMON" -> 0.5;
            case "SEMI_RARE" -> 0.3;
            case "RARE" -> 0.1;
            case "VERY_RARE" -> 0.05;
            case "DURING_INVASIONS" -> 0.2;
            case "DURING_EVENTS" -> 0.3;
            default -> 0.1;
        };
    }

    private List<MonsterRespawn> respawnMonsters(List<MonsterRespawn> deadMonsters) {
        return deadMonsters.stream()
                .peek(deadMonster -> {
                    if (deadMonster.getDiedAt() != null) {
                        Duration duration = Duration.between(deadMonster.getDiedAt().toInstant(), new Date().toInstant());

                        if (duration.getSeconds() >= deadMonster.getRespawnIn()) {
                            log.info("{} - {} respawned", deadMonster.getId(), deadMonster.getName());

                            Optional<Monster> realMonster = monsterRepository.findById(deadMonster.getMonsterId());

                            realMonster.ifPresent(monster -> deadMonster.setHealth(monster.getMaxHealth()));

                            deadMonster.setTargetId(null);
                            deadMonster.setDiedAt(null);
                            deadMonster.setDamageReceived(new ArrayList<>());
                        }
                    }
                })
                .toList();
    }
}
