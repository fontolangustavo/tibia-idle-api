package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Monster;
import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.entities.Room;
import com.fontolan.tibiaidle.repositories.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CombatService {
    private final RoomRepository roomRepository;
    private final FightService fightService;

    public CombatService(RoomRepository roomRepository, FightService fightService) {
        this.roomRepository = roomRepository;
        this.fightService = fightService;
    }

    @Scheduled(fixedRate = 5000)
    public void performCombatTick() {
        List<Room> rooms = roomRepository.findRoomsWithPlayers();

        for (Room room : rooms) {
            log.info("Running a room {} - {}", room.getId(), new Date());

            List<Player> players = room.getPlayers();
            List<Monster> monsters = room.getMonsters();

            for (Player player : players) {
                Monster target = player.getTargetMonster();

                if (target == null
                    && !monsters.stream().filter(Monster::isAlive).toList().isEmpty()
                ) {
                    target = monsters.stream()
                            .filter(Monster::isAlive)
                            .toList()
                            .get(0);

                    player.setTargetMonster(target);
                }

                if (target != null && target.isAlive() && player.isAlive()) {
                    player.attack(target, fightService.damageCalculate(player));
                }

                if (target != null && !target.isAlive()) {
                    player.setTargetMonster(null);
                }
            }

            for (Monster monster : monsters) {
                Player target = monster.getTargetPlayer();

                if (target != null && !target.isAlive()) {
                    monster.setTargetPlayer(null);
                }

                if (target == null && !players.isEmpty()) {
                    target = players.stream()
                            .filter(Player::isAlive)
                            .toList()
                            .get(0);
                    monster.setTargetPlayer(target);
                }

                if (target != null && target.isAlive() && monster.isAlive()) {
                    monster.attack(target);
                }
            }

            // Remover jogadores e monstros mortos
            players.removeIf(player -> {
                boolean isAlive = !player.isAlive();

                player.setTargetMonster(null);
                player.setHealth(player.getMaxHealth());

                return isAlive;
            });

            List<Monster> deadMonsters = monsters.stream()
                    .filter(monster -> !monster.isAlive())
                    .toList();

            // Recompensar jogadores pelos monstros mortos
            for (Monster deadMonster : deadMonsters) {
                Player killer = deadMonster.getTargetPlayer();
                if (killer != null) {
                    killer.setExperience(killer.getExperience() + deadMonster.getExperience());
                    // Adicionar lógica para dropar itens
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
                            deadMonster.setDiedAt(null);
                        }
                    }
                })
                .toList();
    }
}
