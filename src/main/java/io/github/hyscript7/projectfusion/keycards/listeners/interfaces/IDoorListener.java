package io.github.hyscript7.projectfusion.keycards.listeners.interfaces;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface IDoorListener {
    void onPowerableBlockPlaced(BlockPlaceEvent event);
    void onPowerableBlockBroken(BlockBreakEvent event);
    void onPowerableBlockClicked(PlayerInteractEvent event);
}
