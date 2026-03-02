package io.github.hyscrip7.projectfusion.keycards.listeners;

import io.github.hyscrip7.projectfusion.keycards.LinkManager;
import io.github.hyscrip7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscrip7.projectfusion.keycards.appliances.ApplianceService;
import io.github.hyscrip7.projectfusion.keycards.readers.Reader;
import io.github.hyscrip7.projectfusion.keycards.readers.ReaderService;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final ApplianceService applianceService;
    private final ReaderService readerService;
    private final LinkManager linkManager;

    public BlockBreakListener(ApplianceService applianceService, ReaderService readerService, LinkManager linkManager) {
        this.applianceService = applianceService;
        this.readerService = readerService;
        this.linkManager = linkManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Appliance appliance = applianceService.getAppliance(block.getLocation());
        if (appliance != null) {
            linkManager.purgeAppliance(appliance);
        }
        Reader reader = readerService.getReader(block.getLocation());
        if (reader != null) {
            linkManager.purgeReader(reader);
        }
    }
}
