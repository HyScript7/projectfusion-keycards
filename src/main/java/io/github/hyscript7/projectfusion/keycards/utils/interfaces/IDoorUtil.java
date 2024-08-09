package io.github.hyscript7.projectfusion.keycards.utils.interfaces;

import io.github.hyscript7.projectfusion.keycards.data.Keycard;
import io.github.hyscript7.projectfusion.keycards.data.KeycardDoor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IDoorUtil {
    void createDoor(int id, int level, Block block, int openDuration);

    void deleteDoor(int id);

    boolean isDoorItem(ItemStack item); // Whether the item the player is holding will turn into a door when placed
    int getDoorItemLevel(ItemStack item);
    ItemStack setDoorItem(ItemStack item, int level); // Turns the held item into a door item with the given level

    boolean isBlockDoor(Block block); // Whether the block is a keycard door
    KeycardDoor getDoorFromBlock(Block block);

    boolean canPlayerOpenDoor(Player player, KeycardDoor door);
    boolean canKeycardOpenDoor(Keycard keycard, KeycardDoor door);

    void openDoor(KeycardDoor door);
}
