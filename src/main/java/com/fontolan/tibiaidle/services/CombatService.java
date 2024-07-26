package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.DamageReceived;
import com.fontolan.tibiaidle.entities.Monster;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.entities.Room;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import com.fontolan.tibiaidle.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CombatService {
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final FightService fightService;

    public CombatService(RoomRepository roomRepository, PlayerRepository playerRepository, FightService fightService) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.fightService = fightService;
    }

    @Scheduled(fixedRate = 5000)
    public void performCombatTick() {
        List<Room> rooms = roomRepository.findRoomsWithPlayers();

        for (Room room : rooms) {
            List<Player> players = room.getPlayers();
            String playersName = players.stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));
            log.info("Running a room {} with players count {} at {} - players inside {}", room.getId(), players.size(), new Date() , String.join(", ", playersName));

            List<Monster> monsters = room.getMonsters();

            for (Player player : players) {
                Monster target = player.getTargetMonster();

                List<Monster> justLiveMonsters = monsters.stream()
                        .filter(Monster::isAlive)
                        .toList();

                if (target == null && !justLiveMonsters.isEmpty()) {
                    Monster newTarget = justLiveMonsters.get(new Random().nextInt(justLiveMonsters.size()));

                    player.setTargetMonster(newTarget);
                }

                if (target != null && target.isAlive() && player.isAlive()) {
                    player.attack(target, fightService.damageCalculate(player));
                }
            }

            for (Monster monster : monsters) {
                Player target = monster.getTargetPlayer();

                if (target == null && !players.isEmpty()){
                    Player newTarget = players.get(new Random().nextInt(players.size()));

                    monster.setTargetPlayer(newTarget);
                }

                if (target != null && target.isAlive() && monster.isAlive()) {
                    monster.attack(target);
                }
            }

            // Remover jogadores e monstros mortos
            players.removeIf(player -> {
                boolean isDead = !player.isAlive();

                if (isDead) {
                    player.setTargetMonster(null);
                    player.setHealth(player.getMaxHealth());
                    player.setRoom(null);
                    playerRepository.save(player);
                }

                return isDead;
            });

            for(Monster monster : monsters) {
                if(!monster.isAlive() && monster.getDiedAt() == null) {
                    monster.setDiedAt(new Date());

                    int totalXP = monster.getExperience();

                    List<DamageReceived> damageReceiveds = monster.getDamageReceiveds();
                    log.info("Monster total {} - {} damage received {}", monster.getId(), monster.getName(), damageReceiveds.stream().count());

                    int totalDamage = damageReceiveds.stream().mapToInt(DamageReceived::getDamage).sum();

                    for (DamageReceived damageReceived : damageReceiveds) {
                        Player player = damageReceived.getPlayer();

                        int damageDealt = damageReceived.getDamage();
                        int xpGained = (int) ((double) damageDealt / totalDamage * totalXP);

                        player.setExperience(player.getExperience() + xpGained);
                    }
                }
            }

            monsters = respawnMonsters(monsters);

            room.setPlayers(players);
            room.setMonsters(monsters);

            roomRepository.save(room);
        }
    }

    private List<Monster> respawnMonsters(List<Monster> deadMonsters) {
        return deadMonsters.stream()
                .peek(deadMonster -> {
                    if (deadMonster.getDiedAt() != null) {
                        Duration duration = Duration.between(deadMonster.getDiedAt().toInstant(), new Date().toInstant());

                        if (duration.getSeconds() >= deadMonster.getRespawnIn()) {
                            log.info("{} - {} respawned", deadMonster.getId(), deadMonster.getName());
                            deadMonster.setHealth(deadMonster.getMaxHealth());
                            deadMonster.setTargetPlayer(null);
                            deadMonster.setDiedAt(null);
                            deadMonster.setDamageReceiveds(null);
                        }
                    }
                })
                .toList();
    }
}
