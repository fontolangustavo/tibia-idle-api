package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.*;
import com.fontolan.tibiaidle.enums.DamageType;
import com.fontolan.tibiaidle.repositories.ItemRepository;
import com.fontolan.tibiaidle.repositories.MonsterRepository;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import com.fontolan.tibiaidle.utils.ArrayUtils;
import com.fontolan.tibiaidle.utils.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
            processRoom(room);
        }
    }

    private void processRoom(Room room) {
        List<String> players = room.getPlayers();
        List<MonsterRespawn> monsters = room.getMonsters();

        shuffleEntities(players, monsters);
        log.info("Running room {} with {} players at {} - players inside", room.getId(), players.size(), new Date());

        handlePlayerCombat(players, monsters);
        handleMonsterCombat(players, monsters);

        removeDeadPlayers(players);
        handleMonsterLoot(monsters);

        monsters = respawnMonsters(monsters);

        room.setPlayers(players);
        room.setMonsters(monsters);
        roomService.updateRoom(room);
    }

    private void shuffleEntities(List<String> players, List<MonsterRespawn> monsters) {
        Collections.shuffle(players);
        Collections.shuffle(monsters);
    }

    private void handlePlayerCombat(List<String> players, List<MonsterRespawn> monsters) {
        for (String playerId : players) {
            Player player = playerRepository.findById(playerId).orElseThrow();
            MonsterRespawn target = selectTargetForPlayer(player, monsters);

            if (target != null && player.isAlive() && target.isAlive()) {
                applyDamage(player, target, monsters);
            }
        }
    }

    private void handleMonsterCombat(List<String> players, List<MonsterRespawn> monsters) {
        for (MonsterRespawn monster : monsters) {
            Player target = selectTargetForMonster(monster, players);

            if (target != null && monster.isAlive()) {
                applyDamage(monster, target, monsters);
            }
        }
    }
    private void applyDamage(Creature attacker, Creature target, List<? extends  Creature> targets) {
        List<Pair<Integer, DamageType>> damages = fightService.damageCalculate(attacker, FightService.HitType.MELEE);

        for (Pair<Integer, DamageType> damage : damages) {
            int totalDamage = attacker.attack(target, damage.getFirst(), damage.getSecond());
            log.info("{} hit {} for {} damage.", attacker.getName(), target.getName(), totalDamage);
        }

        if (target instanceof MonsterRespawn) {
            updateTargetInList(target, (List<MonsterRespawn>) targets);
        }
    }

    private void updateTargetInList(Creature target, List<MonsterRespawn> creatures) {
        int index = creatures.indexOf(target);
        if (index != -1) {
            creatures.set(index, (MonsterRespawn) target);
        } else {
            throw new RuntimeException("Creature not found in the list");
        }
    }

    private MonsterRespawn selectTargetForPlayer(Player player, List<MonsterRespawn> monsters) {
        MonsterRespawn target = ArrayUtils.findById(monsters, player.getTargetId(), MonsterRespawn::getId);
        List<MonsterRespawn> aliveMonsters = monsters.stream().filter(MonsterRespawn::isAlive).toList();

        if ((target == null || !target.isAlive()) && !aliveMonsters.isEmpty()) {
            target = aliveMonsters.get(RANDOM.nextInt(aliveMonsters.size()));
            player.setTargetId(target.getId());
            playerRepository.save(player);
        }

        return target;
    }

    private Player selectTargetForMonster(MonsterRespawn monster, List<String> players) {
        Player target = null;
        if (monster.getTargetId() != null) {
            target = playerRepository.findById(monster.getTargetId()).orElse(null);
        }

        if (target == null && !players.isEmpty()) {
            String targetId = players.get(RANDOM.nextInt(players.size()));
            monster.setTargetId(targetId);
            target = playerRepository.findById(monster.getTargetId()).orElse(null);
        }

        return target;
    }

    private void removeDeadPlayers(List<String> players) {
        players.removeIf(playerId -> {
            Player player = playerRepository.findById(playerId).orElseThrow();
            boolean isDead = !player.isAlive();

            if (isDead) {
                resetPlayer(player);
            }

            return isDead;
        });
    }

    private void resetPlayer(Player player) {
        player.setTargetId(null);
        player.setHealth(player.getMaxHealth());
        player.setRoomId(null);
        playerRepository.save(player);
    }

    private void handleMonsterLoot(List<MonsterRespawn> monsters) {
        for (MonsterRespawn monster : monsters) {
            if (!monster.isAlive() && monster.getDiedAt() == null) {
                monster.setDiedAt(new Date());
                distributeLoot(monster);
            }
        }
    }

    private void distributeLoot(MonsterRespawn monster) {
        Optional<Monster> realMonster = monsterRepository.findById(monster.getMonsterId());

        int totalXP = realMonster.map(Monster::getExperience).orElse(0);
        List<DamageReceived> damageReceiveds = monster.getDamageReceived();
        int totalDamage = damageReceiveds.stream().mapToInt(DamageReceived::getDamage).sum();

        for (DamageReceived damageReceived : damageReceiveds) {
            Player player = playerRepository.findById(damageReceived.getAttackerId()).orElseThrow();
            int damageDealt = damageReceived.getDamage();
            int xpGained = (int) ((double) damageDealt / totalDamage * totalXP);

            player.setExperience(player.getExperience() + xpGained);
            player.setTargetId(null);

            playerRepository.save(player);
        }

        giveLootToTopDamager(monster, damageReceiveds, realMonster);
    }

    private void giveLootToTopDamager(MonsterRespawn monster, List<DamageReceived> damageReceiveds, Optional<Monster> realMonster) {
        damageReceiveds.sort(Comparator.comparingInt(DamageReceived::getDamage).reversed());
        DamageReceived topDamage = damageReceiveds.isEmpty() ? null : damageReceiveds.get(0);

        if (topDamage != null) {
            distributeLootToPlayer(monster, realMonster.get(), topDamage.getAttackerId());
        } else {
            log.error("Error while giving loot to player from monster {}.", monster.getName());
        }
    }

    private void distributeLootToPlayer(MonsterRespawn monster, Monster realMonster, String attackerId) {
        Player player = playerRepository.findById(attackerId).orElseThrow();
        List<MonsterItem> loots = realMonster.getLoots();

        StringBuilder lootMessage = new StringBuilder();
        for (MonsterItem loot : loots) {
            int quantity = RANDOM.nextInt(loot.getQuantity_max() - loot.getQuantity_min() + 1) + loot.getQuantity_min();
            if (shouldDrop(loot) && quantity > 0) {
                addItemToPlayer(player, loot, quantity, lootMessage);
            }
        }

        if (!lootMessage.isEmpty()) {
            log.info("Player {} received from {}: {}.", player.getName(), monster.getName(), lootMessage);
        }

        playerRepository.save(player);
    }

    private void addItemToPlayer(Player player, MonsterItem loot, int quantity, StringBuilder lootMessage) {
        Optional<Item> optionalItem = itemRepository.findById(loot.getItemId());

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            player.addItemBackpack(item, quantity);

            if (lootMessage.isEmpty()) {
                lootMessage.append(quantity).append(" ").append(loot.getName());
            }else {
                lootMessage.append(", ").append(quantity).append(" ").append(loot.getName());
            }
        }
    }

    private boolean shouldDrop(MonsterItem loot) {
        return RANDOM.nextDouble() < loot.getDropRate();
    }

    private List<MonsterRespawn> respawnMonsters(List<MonsterRespawn> monsters) {
        List<MonsterRespawn> updatedMonsters = new ArrayList<>();

        for (MonsterRespawn monster : monsters) {
            if (!monster.isAlive() && monster.canRespawn()) {
                updatedMonsters.add(respawnMonster(monster));
            } else {
                updatedMonsters.add(monster);
            }
        }

        return updatedMonsters;
    }

    private MonsterRespawn respawnMonster(MonsterRespawn monster) {
        Monster realMonster = monsterRepository.findByName(monster.getName())
            .orElseThrow();

        monster.setHealth(realMonster.getMaxHealth());
        monster.setTargetId(null);
        monster.setDiedAt(null);

        log.info("Monster {} has respawned.", monster.getName());
        return monster;
    }

}
