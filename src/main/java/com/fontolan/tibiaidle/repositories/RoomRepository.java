package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r JOIN r.players p GROUP BY r HAVING COUNT(p) > 0")
    List<Room> findRoomsWithPlayers();
}
