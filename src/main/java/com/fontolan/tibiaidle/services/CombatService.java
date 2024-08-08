package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.*;
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
    private final MonsterRepository monsterRepository;
    private final PlayerRepository playerRepository;
    private final FightService fightService;
    private final RoomService roomService;

    public CombatService(MonsterRepository monsterRepository, PlayerRepository playerRepository, FightService fightService, RoomService roomService) {
        this.monsterRepository = monsterRepository;
        this.playerRepository = playerRepository;
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
                    target = justLiveMonsters.get(new Random().nextInt(justLiveMonsters.size()));

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
                        log.warn("Monster {} not found experience.", "Dragon");
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
                }
            }

            monsters = respawnMonsters(monsters);

            room.setPlayers(players);
            room.setMonsters(monsters);

            roomService.updateRoom(room);
        }
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
