package io.github.hyscript7.projectfusion.keycards.appliances.handlers;

import org.bukkit.block.Block;

public interface ApplianceHandler {
    boolean supports(Block block);
    void activate(Block block);
    void deactivate(Block block);
}
