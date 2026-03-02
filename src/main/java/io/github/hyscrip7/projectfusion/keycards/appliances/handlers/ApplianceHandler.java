package io.github.hyscrip7.projectfusion.keycards.appliances.handlers;

import org.bukkit.block.Block;

public interface ApplianceHandler {
    boolean supports(Block block);
    void activate(Block block);
    void deactivate(Block block);
}
