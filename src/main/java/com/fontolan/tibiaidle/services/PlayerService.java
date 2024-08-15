package com.fontolan.tibiaidle.services;

import com.fontolan.tibiaidle.entities.Player;
import com.fontolan.tibiaidle.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAll(String userId) {
        Iterable<Player> iterablePlayers = playerRepository.findAllUserId(userId);

        return StreamSupport.stream(iterablePlayers.spliterator(), false)
                .collect(Collectors.toList());
    }
}
