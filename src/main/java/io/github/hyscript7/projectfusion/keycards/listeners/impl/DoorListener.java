package io.github.hyscript7.projectfusion.keycards.listeners.impl;

import io.github.hyscript7.projectfusion.keycards.Keycards;
import io.github.hyscript7.projectfusion.keycards.data.Config;
import io.github.hyscript7.projectfusion.keycards.data.Keycard;
import io.github.hyscript7.projectfusion.keycards.data.KeycardDoor;
import io.github.hyscript7.projectfusion.keycards.listeners.interfaces.IDoorListener;
import io.github.hyscript7.projectfusion.keycards.utils.impl.DoorUtil;
import io.github.hyscript7.projectfusion.keycards.utils.impl.KeycardUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class DoorListener implements Listener, IDoorListener {
    private static final NamespacedKey DOOR_LEVEL_KEY = new NamespacedKey(Keycards.getPlugin(Keycards.class), "door_level");

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
            return;
        }
        int level = doorUtil.getDoorItemLevel(event.getItemInHand());
        doorUtil.createDoor(config.nextDoorId(), level, event.getBlock(), 5);
        event.getPlayer().sendMessage("Created door with level " + level + " !");
    }

    @Override
    @EventHandler
    public void onPowerableBlockBroken(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!doorUtil.isBlockDoor(block)) {
            return;
        }
        doorUtil.deleteDoor(doorUtil.getDoorFromBlock(block).getId());
        event.getPlayer().sendMessage("Deleted door!");
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
        event.getPlayer().sendMessage("Clicked on a door...");
        KeycardDoor door = doorUtil.getDoorFromBlock(block);
        if (door == null) {
            event.getPlayer().sendMessage("Door is null??? wtf?");
            return;
        }
        boolean openableByHand = doorUtil.canPlayerOpenDoor(event.getPlayer(), door);
        ItemStack itemMain = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack itemOffhand = event.getPlayer().getInventory().getItemInOffHand();
        @Nullable Keycard keycardMain = keycardUtil.getKeycardFromItem(itemMain);
        @Nullable Keycard keycardOffhand = keycardUtil.getKeycardFromItem(itemOffhand);
        boolean openableByKeycardMain = keycardMain != null && doorUtil.canKeycardOpenDoor(keycardMain, door);
        boolean openableByKeycardOffhand = keycardOffhand != null && doorUtil.canKeycardOpenDoor(keycardOffhand, door);
        if (openableByHand || openableByKeycardOffhand || openableByKeycardMain) {
            event.getPlayer().sendMessage("Door opened!");
            doorUtil.openDoor(door);
        } else {
            event.getPlayer().sendMessage("You can't open this door");
        }
    }

    @EventHandler
    public void debugGiveStuffEvent(PlayerJoinEvent event) {
        ItemStack itemStack = new ItemStack(Material.IRON_DOOR);
        itemStack = doorUtil.setDoorItem(itemStack, 1);
        event.getPlayer().getInventory().addItem(itemStack);
        Keycards.getPlugin(Keycards.class).getServer().getLogger().info("Level: " + doorUtil.getDoorItemLevel(itemStack));
        event.getPlayer().getInventory().addItem(keycardUtil.getKeycardItem(1));
    }
}
