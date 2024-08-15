package com.fontolan.tibiaidle.entities;

import com.fontolan.tibiaidle.enums.ItemType;
import com.fontolan.tibiaidle.enums.SlotType;
import com.fontolan.tibiaidle.enums.Vocation;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@RedisHash("player")
public class Player extends Creature {
    private int maxHealth;
    private int maxMana;
    private int mana;
    private Vocation vocation;
    private List<PlayerItem> playerItems = new ArrayList<>();
    private String targetId;
    private String roomId;
    @Indexed
    private String userId;

    @Indexed
    private Map<ItemType, Integer> weaponMastery = new HashMap<>();

    public int getWeaponMastery(ItemType type) {
        return weaponMastery.getOrDefault(type, 0);
    }

    public PlayerItem getItemBySlotType(SlotType slotType) {
        return playerItems.stream()
                .filter(item -> item.getSlotType().equals(slotType))
                .findFirst()
                .orElse(null);
    }

    public Optional<PlayerItem> getItemFromBackpack(String itemId) {
        return playerItems.stream()
                .filter(item -> item.getItemId().equals(itemId) && item.getSlotType().equals(SlotType.BACKPACK))
                .findFirst();
    }

    public int getIndexItemFromBackpack(String itemId) {
        for (int i = 0; i < playerItems.size(); i++) {
            PlayerItem item = playerItems.get(i);
            if (item.getItemId().equals(itemId) && item.getSlotType().equals(SlotType.BACKPACK)) {
                return i;
            }
        }

        return -1;
    }

    public boolean equipItem(Item item, SlotType slotType) {
        Optional<PlayerItem> hasItem = this.checkSlotType(slotType);

        if(slotType.equals(SlotType.BACKPACK)) {
            return false;
        }

        if(hasItem.isEmpty()) {
            this.unequipItem(slotType);
        }

        playerItems.add(
                PlayerItem.builder()
                        .itemId(item.getId())
                        .slotType(slotType)
                        .build()
        );

        return true;
    }

    public void addItemBackpack(Item item, int quantity){
        this.getItemFromBackpack(item.getId())
            .ifPresentOrElse(
                (value) -> {
                    value.setQuantity(item.isGroupable() ? value.getQuantity() + quantity : 1);

                    int index = this.getIndexItemFromBackpack(item.getId());

                    this.getPlayerItems().set(index, value);
                },
                () -> {
                    this.getPlayerItems().add(PlayerItem.builder()
                            .playerId(this.getId())
                            .itemId(item.getId())
                            .quantity(item.isGroupable() ? quantity : 1)
                            .slotType(SlotType.BACKPACK)
                            .build()
                    );
                }
            );
    }

    public Optional<PlayerItem> checkSlotType(SlotType slotType) {
        return this.playerItems.stream()
                .filter(playerItem ->
                        Objects.equals(playerItem.getPlayerId(), this.id)
                        && playerItem.getSlotType() == slotType
                )
                .findFirst();
    }

    public void unequipItem(SlotType slotType) {
        playerItems.stream()
                .filter(playerItem -> playerItem.getSlotType() == slotType)
                .forEach(pi -> pi.setSlotType(SlotType.BACKPACK));
    }

    @Override
    public String toString() {
        return "Player{" +
                "maxHealth=" + maxHealth +
                ", maxMana=" + maxMana +
                ", mana=" + mana +
                ", vocation=" + vocation +
                ", playerItems=" + playerItems +
                ", targetId='" + targetId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", weaponMastery=" + weaponMastery +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", health=" + health +
                '}';
    }
}
