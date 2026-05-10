package io.github.hyscript7.projectfusion.keycards.appliances.handlers.types;

import io.github.hyscript7.projectfusion.keycards.appliances.handlers.ApplianceHandler;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;

public class DoorApplianceHandler implements ApplianceHandler {
    @Override
    public boolean supports(Block block) {
        return block.getBlockData() instanceof Door;
    }

    @Override
    public void activate(Block block) {
        if (!(block.getBlockData() instanceof Door door)) {
            return;
        }
        if (!door.isOpen()) {
            block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
        }
        door.setOpen(true);
        door.setPowered(true);
        block.setBlockData(door, false);
    }

    @Override
    public void deactivate(Block block) {
        if (!(block.getBlockData() instanceof Door door)) {
            return;
        }
        if (door.isOpen()) {
            block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
        }
        door.setOpen(false);
        door.setPowered(false);
        block.setBlockData(door, true);
    }
}
