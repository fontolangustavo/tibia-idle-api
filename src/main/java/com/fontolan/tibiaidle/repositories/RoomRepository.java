package com.fontolan.tibiaidle.repositories;

import com.fontolan.tibiaidle.entities.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, String> {
    @Query("SELECT r FROM Room r JOIN r.players p GROUP BY r HAVING COUNT(p) > 0")
    List<Room> findRoomsWithPlayers();
}
