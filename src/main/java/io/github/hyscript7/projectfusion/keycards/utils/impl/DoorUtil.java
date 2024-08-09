package io.github.hyscript7.projectfusion.keycards.utils.impl;

import io.github.hyscript7.projectfusion.keycards.Keycards;
import io.github.hyscript7.projectfusion.keycards.data.Config;
import io.github.hyscript7.projectfusion.keycards.data.Keycard;
import io.github.hyscript7.projectfusion.keycards.data.KeycardDoor;
import io.github.hyscript7.projectfusion.keycards.utils.interfaces.IDoorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class DoorUtil implements IDoorUtil {
    private static final NamespacedKey DOOR_LEVEL_KEY = new NamespacedKey(Keycards.getPlugin(Keycards.class), "door_level");
    private final Config config;

    public DoorUtil(Config config) {
        this.config = config;
    }

    @Override
    public void createDoor(int id, int level, Block block, int openDuration) {
        KeycardDoor keycardDoor = new KeycardDoor(id, level, block, openDuration);
        config.getDoors().add(keycardDoor);
    }

    @Override
    public void deleteDoor(int id) {
        config.getDoors().removeIf(door -> door.getId() == id);
    }

    @Override
    public boolean isDoorItem(ItemStack item) {
        Component doorItemDisplayNameComponent = Component.text("Keycard Door");
        if (!item.hasItemMeta()) {
            return false;
        }
        if (!item.getItemMeta().hasDisplayName()) {
            return false;
        }
        if (!item.getItemMeta().displayName().equals(doorItemDisplayNameComponent)) {
            return false;
        }
        Component doorItemLevelLoreComponent = Component.text("Level: ");
        if (!item.getItemMeta().hasLore()) {
            return false;
        }
        return getDoorItemLevel(item) > Integer.MIN_VALUE;
    }

    @Override
    public int getDoorItemLevel(ItemStack item) {
        Component doorItemLevelLoreComponent = Component.text("Level: ");
        if (!item.getItemMeta().hasLore()) {
            Keycards.getPlugin(Keycards.class).getLogger().info("Item has no lore");
            return Integer.MIN_VALUE;
        }
        // Does the lore list contain any component starting with doorItemLevelLoreComponent? If not, return min value
        // Note that the lore list contains Components, which we may need to convert using .toString()
        if (Objects.requireNonNull(item.getItemMeta().lore()).stream().noneMatch(lore -> lore.toString().contains("Level"))) {
            Keycards.getPlugin(Keycards.class).getLogger().info("Item has no level");
            return Integer.MIN_VALUE;
        }
        Component doorItemLevel = Objects.requireNonNull(item.getItemMeta().lore()).stream().filter(lore -> lore.toString().contains("Level")).findFirst().get();
        Keycards.getPlugin(Keycards.class).getLogger().info("This dumb ass shit: " + doorItemLevel);
        // TextComponentImpl{content="Level: 1", style=StyleImpl{obfuscated=not_set, bold=not_set, strikethrough=not_set, underlined=not_set, italic=not_set, color=null, clickEvent=null, hoverEvent=null, insertion=null, font=null}, children=[]}
        String separator = "content=\"Level: ";
        int indexBegin = doorItemLevel.toString().indexOf(separator);
        int indexEnd = doorItemLevel.toString().indexOf("\",", indexBegin);
        String levelStr = doorItemLevel.toString().substring(indexBegin + separator.length(), indexEnd);
        Keycards.getPlugin(Keycards.class).getLogger().info("This dumb ass shit levelStr: " + levelStr);
        return Integer.parseInt(levelStr);
    }

    @Override
    public ItemStack setDoorItem(ItemStack item, int level) {
        Component doorItemDisplayNameComponent = Component.text("Keycard Door");
        Component doorItemLevelLoreComponent = Component.text("Level: " + level);
        ItemMeta itemMeta = item.getItemMeta().clone();
        itemMeta.displayName(doorItemDisplayNameComponent);
        itemMeta.lore(List.of(doorItemLevelLoreComponent));
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public boolean isBlockDoor(Block block) {
        return config.getDoors().stream().anyMatch(door -> door.getBlock().getLocation().equals(block.getLocation()));
    }

    @Override
    public @Nullable KeycardDoor getDoorFromBlock(Block block) {
        return config.getDoors().stream().filter(door -> door.getBlock().equals(block)).findFirst().orElse(null);
    }

    @Override
    public boolean canPlayerOpenDoor(Player player, KeycardDoor door) {
        return door.getLevel() < 0;
    }

    @Override
    public boolean canKeycardOpenDoor(Keycard keycard, KeycardDoor door) {
        return keycard.getLevel() >= door.getLevel();
    }

    @Override
    public void openDoor(KeycardDoor door) {
        Block block = door.getBlock();
        @Nullable Block otherBlock = null;
        if (block.getType().equals(Material.IRON_DOOR)) {
            if (block.getRelative(BlockFace.DOWN).getType().equals(Material.IRON_DOOR)) {
                otherBlock = block.getRelative(BlockFace.UP);
            } else {
                otherBlock = block.getRelative(BlockFace.DOWN);
            }
        }
        openAndThenCloseDoor(block, door.getOpenDuration());
        if (otherBlock != null) {
            openAndThenCloseDoor(otherBlock, door.getOpenDuration());
        }
    }

    private void openAndThenCloseDoor(Block block, int openDuration) {
        if (!block.getType().equals(Material.IRON_DOOR)) {
            return;
        }
        Door d = (Door) block.getBlockData();
        if (d.isOpen()) {
            return;
        }
        d.setOpen(true);
        block.setBlockData(d);
        new BukkitRunnable() {
            @Override
            public void run() {
                d.setOpen(false);
                block.setBlockData(d);
            }
        }.runTaskLater(Keycards.getPlugin(Keycards.class), openDuration * 20L);
    }
}