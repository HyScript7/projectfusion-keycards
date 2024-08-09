package io.github.hyscript7.projectfusion.keycards.listeners.impl;

import io.github.hyscript7.projectfusion.keycards.Keycards;
import io.github.hyscript7.projectfusion.keycards.Messages;
import io.github.hyscript7.projectfusion.keycards.data.Config;
import io.github.hyscript7.projectfusion.keycards.data.Keycard;
import io.github.hyscript7.projectfusion.keycards.data.KeycardDoor;
import io.github.hyscript7.projectfusion.keycards.listeners.interfaces.IDoorListener;
import io.github.hyscript7.projectfusion.keycards.utils.impl.DoorUtil;
import io.github.hyscript7.projectfusion.keycards.utils.impl.KeycardUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class DoorListener implements Listener, IDoorListener {
    private static final NamespacedKey DOOR_LEVEL_KEY = new NamespacedKey(Keycards.getPlugin(Keycards.class), "door_level");
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DoorListener.class);
    private final Logger logger = Keycards.getPlugin(Keycards.class).getLogger();

    private final Config config;
    private final DoorUtil doorUtil;
    private final KeycardUtil keycardUtil;

    public DoorListener(Config config, DoorUtil doorUtil, KeycardUtil keycardUtil) {
        this.config = config;
        this.doorUtil = doorUtil;
        this.keycardUtil = keycardUtil;
    }

    @Override
    @EventHandler
    public void onPowerableBlockPlaced(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType().equals(Material.AIR)) {
            return;
        }
        if (!doorUtil.isDoorItem(event.getItemInHand())) {
            logger.info("Item " + item + " is not a door item");
            return;
        }
        int level = doorUtil.getDoorItemLevel(event.getItemInHand());
        doorUtil.createDoor(config.nextDoorId(), level, event.getBlock(), 5);
        logger.info("Door created at " + event.getBlock().getLocation() + " with level " + level);
        event.getPlayer().sendMessage(Messages.PREFIX.append(Messages.DOOR_CREATED));
    }

    @Override
    @EventHandler
    public void onPowerableBlockBroken(BlockBreakEvent event) {
        Block block = event.getBlock();
        Block blockAbove = null;
        Block blockBelow = null;
        if (block.getType().equals(Material.IRON_DOOR)) {
            if (block.getRelative(BlockFace.DOWN).getType().equals(Material.IRON_DOOR)) {
                blockAbove = block.getRelative(BlockFace.UP);
            } else {
                blockBelow = block.getRelative(BlockFace.DOWN);
            }
        }
        int deleted = 0;
        if (doorUtil.isBlockDoor(block)) {
            doorUtil.deleteDoor(doorUtil.getDoorFromBlock(block).getId());
            deleted++;
        }
        if (blockAbove != null && doorUtil.isBlockDoor(blockAbove)) {
            doorUtil.deleteDoor(doorUtil.getDoorFromBlock(blockAbove).getId());
            deleted++;
        }
        if (blockBelow != null && doorUtil.isBlockDoor(blockAbove)) {
            doorUtil.deleteDoor(doorUtil.getDoorFromBlock(blockBelow).getId());
            deleted++;
        }
        if (deleted > 0) {
            logger.info("Door deleted near " + block.getLocation());
            event.getPlayer().sendMessage(Messages.PREFIX.append(Messages.DOOR_DELETED));
        }
    }

    @Override
    @EventHandler
    public void onPowerableBlockClicked(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!doorUtil.isBlockDoor(block)) {
            return;
        }
        KeycardDoor door = doorUtil.getDoorFromBlock(block);
        logger.info("Door clicked at " + block.getLocation());
        if (door == null) {
            logger.warning("Door is null, despite passing door check!");
            return;
        }
        boolean openableByHand = doorUtil.canPlayerOpenDoor(event.getPlayer(), door);
        ItemStack itemMain = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack itemOffhand = event.getPlayer().getInventory().getItemInOffHand();
        @Nullable Keycard keycardMain = keycardUtil.getKeycardFromItem(itemMain);
        @Nullable Keycard keycardOffhand = keycardUtil.getKeycardFromItem(itemOffhand);
        boolean openableByKeycardMain = keycardMain != null && doorUtil.canKeycardOpenDoor(keycardMain, door);
        boolean openableByKeycardOffhand = keycardOffhand != null && doorUtil.canKeycardOpenDoor(keycardOffhand, door);
        logger.info("Can player open door: " + openableByHand + ", by keycard: " + openableByKeycardMain + " or " + openableByKeycardOffhand);
        if (openableByHand || openableByKeycardOffhand || openableByKeycardMain) {
            logger.info("Opening door near " + block.getLocation());
            event.getPlayer().sendMessage(Messages.PREFIX.append(Messages.ACCESS_GRANTED));
            doorUtil.openDoor(door);
        } else {
            logger.info("Access denied for door located near " + block.getLocation());
            event.getPlayer().sendMessage(Messages.PREFIX.append(Messages.ACCESS_DENIED));
        }
    }

    /*
    @EventHandler
    public void debugGiveStuffEvent(PlayerJoinEvent event) {
        ItemStack itemStack = new ItemStack(Material.IRON_DOOR);
        itemStack = doorUtil.setDoorItem(itemStack, 1);
        event.getPlayer().getInventory().addItem(itemStack);
        Keycards.getPlugin(Keycards.class).getServer().getLogger().info("Level: " + doorUtil.getDoorItemLevel(itemStack));
        event.getPlayer().getInventory().addItem(keycardUtil.getKeycardItem(1));
    }
    */
}
